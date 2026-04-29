package com.election.evm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Arrays;

/**
 * CORS Configuration that reads allowed origins from environment variable
 * This allows dynamic CORS configuration without code changes
 * 
 * Usage:
 * Set environment variable: APP_CORS_ALLOWED_ORIGINS=https://frontend.com,https://www.frontend.com
 * 
 * For local development:
 * APP_CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:3000
 */
@Component
public class CorsConfigFromEnv {

    private final String allowedOrigins;

    public CorsConfigFromEnv(
            @Value("${app.cors.allowed-origins:http://localhost:5173}") String allowedOrigins
    ) {
        this.allowedOrigins = allowedOrigins;
    }

    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .toList();

        if (origins.contains("*")) {
            config.setAllowedOriginPatterns(List.of("*"));
        } else {
            config.setAllowedOrigins(origins);
        }
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return source;
    }
}
