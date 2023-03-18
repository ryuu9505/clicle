package com.elcilc.clicle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ClicleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClicleApplication.class, args);
	}

}
