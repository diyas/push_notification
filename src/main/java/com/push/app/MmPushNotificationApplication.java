package com.push.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class MmPushNotificationApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(MmPushNotificationApplication.class, args);
	}

}
