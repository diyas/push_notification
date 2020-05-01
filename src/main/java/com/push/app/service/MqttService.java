package com.push.app.service;

import com.push.app.MmPushNotificationApplication;
import com.push.app.model.data.MqttProperties;
import com.push.app.service.interfaces.IMqttService;
import com.push.app.utility.SocketFactory;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

@Service
public class MqttService implements IMqttService {

    private IMqttClient mqttClient;

    @Autowired
    private MqttProperties prop;

    @ConfigurationProperties(prefix = "mqtt")
    public MqttConnectOptions mqttConnectOptions() {
        return new MqttConnectOptions();
    }

    @Override
    public boolean connect(boolean isSsl) throws MqttException {
        String clientId = MqttClient.generateClientId();
        mqttClient = new MqttClient(isSsl ? "ssl://" : "tcp://" + prop.getHostname() + ":" + prop.getPort(), clientId);
        if (isSsl) {
            SocketFactory.SocketFactoryOptions socketFactoryOptions = new SocketFactory.SocketFactoryOptions();
            try {
                String fileName = "raw/ca.crt";
                ClassLoader classLoader = new MmPushNotificationApplication().getClass().getClassLoader();
                InputStream stream = new FileInputStream(new File(classLoader.getResource(fileName).getFile()));
                socketFactoryOptions.withCaInputStream(stream);
                mqttConnectOptions().setSocketFactory(new SocketFactory(socketFactoryOptions));
            } catch (IOException | NoSuchAlgorithmException | KeyStoreException | CertificateException | KeyManagementException | UnrecoverableKeyException e) {
                e.printStackTrace();
            }
        }
        mqttClient.connect(mqttConnectOptions());
        return mqttClient.isConnected();
    }

    @Override
    public void publish(String topic, String message) throws MqttException {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(message.getBytes());
        mqttClient.publish(topic, mqttMessage);
    }

    @Override
    public void subscribe(String topic) throws MqttException {
        System.out.println("Messages received:");
        mqttClient.subscribeWithResponse(topic, (tpic, msg) -> {
            System.out.println(msg.getId() + " -> " + new String(msg.getPayload()));
        });
        mqttClient.subscribe(topic);
    }

    @Override
    public void unsubscribe(String topic) throws MqttException {
        mqttClient.unsubscribe(topic);
    }

    @Override
    public void disconnect() throws MqttException {
        mqttClient.disconnect();

    }

}
