package com.push.app.config.oauth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

@Data
public class ResponseToken {
    @JsonProperty(value = "access_token")
    private String access_token;
    @JsonProperty(value = "token_type")
    private String token_type;
    @JsonProperty(value = "refresh_token")
    private String refresh_token;
    @JsonProperty(value = "expires_in")
    private int expires_in;
    @JsonProperty(value = "scope")
    private String scope;
    @JsonProperty(value = "jti")
    private String jti;
}
