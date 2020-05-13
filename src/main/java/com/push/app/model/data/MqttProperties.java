package com.push.app.model.data;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("mqtt")
public class MqttProperties {
    private boolean automaticReconnect;
    private boolean cleanSession;
    private boolean isSsl;
    private int connectionTimeout;
    private String clientId;
    private String hostname;
    private int port;
}
