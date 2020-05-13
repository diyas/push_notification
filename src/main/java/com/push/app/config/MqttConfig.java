package com.push.app.config;

import com.push.app.MmPushNotificationApplication;
import com.push.app.model.data.MqttProperties;
import com.push.app.service.MqttService;
import com.push.app.utility.SocketFactory;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

@Configuration
public class MqttConfig {

    @Autowired
    private MqttService mqttService;

    public static boolean isConnected;

    @Autowired
    @Bean
    public void mqttConnect() throws MqttException {
        this.isConnected = mqttService.connect(false);
    }

}
