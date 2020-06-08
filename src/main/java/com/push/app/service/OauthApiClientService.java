package com.push.app.service;

import com.push.app.config.oauth.Login;
import com.push.app.config.oauth.Oauth2Properties;
import com.push.app.config.oauth.ResponseToken;
import com.push.app.model.payload.Response;
import com.push.app.utility.Utility;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class OauthApiClientService {

    @Autowired
    private Oauth2Properties oauth2Properties;

    public ResponseEntity<Response> getToken(Login request, String token, boolean isRefresh) {
        String credentials = oauth2Properties.getCredentials();
        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBasicAuth(encodedCredentials);
        HttpEntity<String> req = new HttpEntity<String>(headers);
        String accessTokenUrl = "";
        if (!isRefresh) {
            accessTokenUrl = oauth2Properties.getTokenUrl();
            accessTokenUrl += "?username=" + request.getUsername();
            accessTokenUrl += "&password=" + request.getPassword();
            accessTokenUrl += "&deviceTimestamp=" + request.getDeviceTimestamp();
            accessTokenUrl += "&grant_type=password";
        } else {
            accessTokenUrl = oauth2Properties.getTokenUrl();
            accessTokenUrl += "?refresh_token=" + token;
            accessTokenUrl += "&grant_type=refresh_token";
        }

        ResponseEntity<ResponseToken> response = null;
        try {
            response = restTemplate.postForEntity(accessTokenUrl, req, ResponseToken.class);
        } catch (HttpClientErrorException e) {
            return Utility.setResponse(e.getStatusCode(), e.getResponseBodyAsString(), null);
        }
        return Utility.setResponse("", response.getBody());
    }
}
