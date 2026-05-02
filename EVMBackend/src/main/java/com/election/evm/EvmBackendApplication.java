package com.election.evm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EvmBackendApplication {

	public static void main(String[] args) {
		// Hardcoded database credentials provided by the user
		System.setProperty("spring.datasource.url", "jdbc:mysql://tramway.proxy.rlwy.net:11201/railway?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC");
		System.setProperty("spring.datasource.username", "root");
		System.setProperty("spring.datasource.password", "bpfmRrVuKtDGvYqVqbDqLuPBRECcWobz");

		// Hardcoded email credentials
		System.setProperty("spring.mail.username", "kdhanu0607@gmail.com");
		System.setProperty("spring.mail.password", "lteuxlpuhlmgijrk");
		System.setProperty("app.mail.from", "kdhanu0607@gmail.com");

		SpringApplication.run(EvmBackendApplication.class, args);
	}

}
