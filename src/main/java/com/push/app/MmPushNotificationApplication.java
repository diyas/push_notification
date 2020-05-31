package com.push.app;

import com.push.app.config.MqttConfig;
import com.push.app.service.MqttService;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
public class MmPushNotificationApplication extends SpringBootServletInitializer {

	@Autowired
	private static MqttConfig mqttConfig;

	public static void main(String[] args) {
		SpringApplication.run(MmPushNotificationApplication.class, args);
	}

//	@Bean
//	public Executor taskExecutor() {
//		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//		executor.setCorePoolSize(2);
//		executor.setMaxPoolSize(2);
//		executor.setQueueCapacity(500);
//		executor.setThreadNamePrefix("GithubLookup-");
//		executor.initialize();
//		return executor;
//	}

}
