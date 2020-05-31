package com.push.app.service;

import com.push.app.MmPushNotificationApplication;
import com.push.app.model.data.MqttProperties;
import com.push.app.service.interfaces.IMqttService;
import com.push.app.utility.SocketFactory;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
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
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MqttService implements IMqttService {

    //private static final Logger logger = LoggerFactory.getLogger(MqttService.class);

    private IMqttClient mqttClient;

    @Autowired
    private MqttProperties prop;

    @Override
    public boolean connect() throws MqttException {
        boolean isSsl = prop.isSsl();
        //String clientId = MqttClient.generateClientId();
        String connection = isSsl ? "ssl://" : "tcp://";
        mqttClient = new MqttClient(connection + prop.getHostname() + ":" + prop.getPort(), prop.getClientId(), new MemoryPersistence());
        if (isSsl) {
            SocketFactory.SocketFactoryOptions socketFactoryOptions = new SocketFactory.SocketFactoryOptions();
            System.out.println("Init  " + mqttClient.getServerURI());
            try {
                String fileName = "raw/m2mqtt_dev_ca.crt";
                ClassLoader classLoader = new MmPushNotificationApplication().getClass().getClassLoader();
                InputStream stream = new FileInputStream(new File(classLoader.getResource(fileName).getFile()));
                socketFactoryOptions.withCaInputStream(stream);
                prop.setSocketFactory(new SocketFactory(socketFactoryOptions));
            } catch (IOException | NoSuchAlgorithmException | KeyStoreException | CertificateException | KeyManagementException | UnrecoverableKeyException e) {
                System.out.println("Exception  " + mqttClient.getServerURI());
                e.printStackTrace();
            }
        }
        mqttClient.connect(prop.getMqttConnectOptions());
        return mqttClient.isConnected();
    }

    @Override
    public boolean isConnected() throws MqttException {
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

    //@Async
//    public CompletableFuture<String> findUser(String user) throws InterruptedException {
//        logger.info("Looking up " + user);
//        // Artificial delay of 1s for demonstration purposes
//        Thread.sleep(1000L);
//        return CompletableFuture.completedFuture("async running");
//    }
}
