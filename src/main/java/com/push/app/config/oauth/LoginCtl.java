package com.push.app.config.oauth;

import com.push.app.model.payload.Response;
import com.push.app.utility.ErrorHandler;
import com.push.app.utility.Utility;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
public class LoginCtl {

    @Autowired
    private Oauth2Properties oauth2Properties;

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

    public void refreshToken() {

    }

}
