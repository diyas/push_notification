package com.push.app.service.interfaces;

import org.eclipse.paho.client.mqttv3.MqttException;

public interface IMqttService {
    public boolean connect(boolean isSsl) throws MqttException;

    public void publish(String topic, String message) throws MqttException;

    public void subscribe(String topic) throws MqttException;

    public void unsubscribe(String topic) throws MqttException;

    public void disconnect() throws MqttException;
}
