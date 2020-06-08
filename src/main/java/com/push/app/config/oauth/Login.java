package com.push.app.config.oauth;

import lombok.Data;

@Data
public class Login {
    private String username;
    private String password;
    private String deviceTimestamp;
}
