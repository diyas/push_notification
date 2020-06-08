package com.push.app.config.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class RefreshToken implements Serializable {
    @JsonProperty(value = "refresh_token")
    private String refreshToken;
}
