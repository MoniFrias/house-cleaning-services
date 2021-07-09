package com.example.houseCleaning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication
public class HouseCleaningServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HouseCleaningServiceApplication.class, args);
	}

}
