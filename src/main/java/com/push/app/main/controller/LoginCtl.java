package com.push.app.main.controller;

import com.push.app.config.oauth.Login;
import com.push.app.config.oauth.Oauth2Properties;
import com.push.app.config.oauth.RefreshToken;
import com.push.app.config.oauth.ResponseToken;
import com.push.app.model.payload.Response;
import com.push.app.service.MqttService;
import com.push.app.service.OauthApiClientService;
import com.push.app.utility.Utility;
import org.apache.tomcat.util.codec.binary.Base64;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
public class LoginCtl {

    @Autowired
    private Oauth2Properties oauth2Properties;

    @Autowired
    private OauthApiClientService oauthApiClientService;

    @Autowired
    private MqttService mqttService;

    @PostMapping("/login")
    public ResponseEntity<Response> getToken(@RequestBody Login request) {
//        String credentials = oauth2Properties.getCredentials();
//        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));
//        RestTemplate restTemplate = new RestTemplate();
//        //restTemplate.setErrorHandler(new ErrorHandler());
//        HttpHeaders headers = new HttpHeaders();
//        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//        //headers.add("Authorization", "Basic " + encodedCredentials);
//        headers.setBasicAuth(encodedCredentials);
//        HttpEntity<String> req = new HttpEntity<String>(headers);
//
//        String accessTokenUrl = oauth2Properties.getTokenUrl();
//        accessTokenUrl += "?username=" + request.getUsername();
//        accessTokenUrl += "&password=" + request.getPassword();
//        accessTokenUrl += "&grant_type=password";
//        ResponseEntity<ResponseToken> response = null;
//        try {
//            response = restTemplate.postForEntity(accessTokenUrl, req, ResponseToken.class);
//        } catch (HttpClientErrorException e){
//            return Utility.setResponse("", Utility.getClientMessage(e.getMessage()));
//        }
//        return Utility.setResponse("", response.getBody());
        return oauthApiClientService.getToken(request, null, false);
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<Response> getToken(@RequestBody RefreshToken request) {
//        String credentials = oauth2Properties.getCredentials();
//        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//        headers.add("Authorization", "Basic " + encodedCredentials);
//        headers.setBasicAuth(oauth2Properties.getClientId(), oauth2Properties.getClientSecret());
//        HttpEntity<String> req = new HttpEntity<String>(headers);
//
//        String accessTokenUrl = oauth2Properties.getTokenUrl();
//        accessTokenUrl += "?refresh_token=" + request.getRefreshToken();
//        accessTokenUrl += "&grant_type=refresh_token";
//        ResponseEntity<ResponseToken> response = null;
//        try {
//            response = restTemplate.postForEntity(accessTokenUrl, req, ResponseToken.class);
//        } catch (HttpClientErrorException e){
//            return Utility.setResponse("", e.getMessage());
//        }
//        return Utility.setResponse("", response.getBody());
        return oauthApiClientService.getToken(null, request.getRefreshToken(), true);
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
