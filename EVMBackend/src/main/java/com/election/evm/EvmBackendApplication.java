package com.election.evm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EvmBackendApplication {

	public static void main(String[] args) {
		// Forcefully override any broken SPRING_DATASOURCE_* environment variables in Railway
		// and map them directly to Railway's auto-injected MYSQL_* variables instead.
		System.setProperty("spring.datasource.url", "jdbc:mysql://${MYSQLHOST:localhost}:${MYSQLPORT:3306}/${MYSQLDATABASE:evm}?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC");
		System.setProperty("spring.datasource.username", "${MYSQLUSER:root}");
		System.setProperty("spring.datasource.password", "${MYSQLPASSWORD:password}");

		SpringApplication.run(EvmBackendApplication.class, args);
	}

}
