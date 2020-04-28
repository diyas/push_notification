package com.push.app.service;

import com.push.app.model.data.MqttProperties;
import com.push.app.service.interfaces.IMqttService;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

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
    public boolean connect() throws MqttException {
        String clientId = MqttClient.generateClientId();
        mqttClient = new MqttClient("tcp://" + prop.getHostname() + ":" + prop.getPort(), clientId);
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
