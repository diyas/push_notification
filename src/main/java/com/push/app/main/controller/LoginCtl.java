package com.push.app.main.controller;

import com.push.app.config.oauth.Login;
import com.push.app.config.oauth.Oauth2Properties;
import com.push.app.config.oauth.ResponseToken;
import com.push.app.model.payload.Response;
import com.push.app.service.MqttService;
import com.push.app.utility.ErrorHandler;
import com.push.app.utility.Utility;
import org.apache.tomcat.util.codec.binary.Base64;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
public class LoginCtl {

    @Autowired
    private Oauth2Properties oauth2Properties;

    @Autowired
    private MqttService mqttService;

    @PostMapping("/login")
    public ResponseEntity<Response> getToken(@RequestBody Login request) {
        String credentials = oauth2Properties.getCredentials();
        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));
        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.setErrorHandler(new ErrorHandler());
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Basic " + encodedCredentials);
        headers.setBasicAuth(oauth2Properties.getClientId(), oauth2Properties.getClientSecret());
        HttpEntity<String> req = new HttpEntity<String>(headers);

        String accessTokenUrl = oauth2Properties.getTokenUrl();
        accessTokenUrl += "?username=" + request.getUsername();
        accessTokenUrl += "&password=" + request.getPassword();
        accessTokenUrl += "&grant_type=password";
        ResponseEntity<ResponseToken> response = restTemplate.exchange(accessTokenUrl, HttpMethod.POST, req, ResponseToken.class);
        return Utility.setResponse("", response.getBody());
    }

    @PostMapping(value = "/publish_token")
    @ApiIgnore
    public ResponseEntity<Response> publishToken(@RequestBody ResponseToken responseToken) throws MqttException {
        if (responseToken != null)
            mqttService.publish("/Pairing/POS01", responseToken.getAccess_token());
        return Utility.setResponse("Token Published.", null);
    }

    @GetMapping(value = "/connect")
    @ApiIgnore
    public ResponseEntity<Boolean> connectMqtt() throws MqttException {
        mqttService.connect();
        return new ResponseEntity<Boolean>(mqttService.isConnected(), HttpStatus.OK);
    }
}
