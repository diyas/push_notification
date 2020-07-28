package com.push.app.main.controller;

import com.push.app.config.oauth.Login;
import com.push.app.config.oauth.Oauth2Properties;
import com.push.app.config.oauth.TokenPayload;
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

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/auth", produces = APPLICATION_JSON_VALUE)
public class LoginCtl {

    @Autowired
    private OauthApiClientService oauthApiClientService;

    @Autowired
    private MqttService mqttService;

    @PostMapping("/login")
    public ResponseEntity<Response> getToken(@RequestBody Login request) {
        return oauthApiClientService.getToken(request, null, false);
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<Response> getRefreshToken(@RequestBody TokenPayload request) {
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
