package com.push.app.config;

import com.push.app.model.data.MqttProperties;
import com.push.app.service.MqttService;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


//@Configuration
public class MqttConfig {

    @Autowired
    private MqttService mqttService;

    public static boolean isConnected;

    //@Bean
    public void mqttConnect() throws MqttException {
        isConnected = mqttService.connect();
    }

}
