package com.elcilc.clicle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class ClicleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClicleApplication.class, args);
	}

}
