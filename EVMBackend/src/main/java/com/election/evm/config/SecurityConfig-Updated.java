package com.election.evm.config;

import com.election.evm.security.JwtAuthenticationFilter;
import com.election.evm.security.GoogleOAuth2SuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * UPDATED SecurityConfig for Production Deployment
 * 
 * KEY CHANGES:
 * 1. CORS configuration reads from CorsConfigFromEnv (environment variable)
 * 2. No hardcoded localhost URLs
 * 3. Production-ready security settings
 * 
 * Environment Variables Needed:
 * - APP_CORS_ALLOWED_ORIGINS: Comma-separated list of allowed origins
 *   Example: https://yourdomain.com,https://www.yourdomain.com
 * 
 * TO USE THIS FILE:
 * Replace the existing SecurityConfig.java with this content
 */

@Configuration
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final GoogleOAuth2SuccessHandler googleOAuth2SuccessHandler;
    private final CorsConfigFromEnv corsConfig;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          GoogleOAuth2SuccessHandler googleOAuth2SuccessHandler,
                          CorsConfigFromEnv corsConfig) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.googleOAuth2SuccessHandler = googleOAuth2SuccessHandler;
        this.corsConfig = corsConfig;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/login/").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/register/").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/otp/send", "/api/auth/otp/verify").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/election-results").hasAnyRole("ADMIN", "CITIZEN", "OBSERVER", "ANALYST")
                        .requestMatchers("/api/users/**", "/api/dashboard/**").hasRole("ADMIN")
                        .requestMatchers("/api/incidents/**").hasAnyRole("ADMIN", "OBSERVER")
                        .requestMatchers(HttpMethod.GET, "/api/fraud-reports").hasAnyRole("ADMIN", "CITIZEN", "OBSERVER")
                        .requestMatchers(HttpMethod.POST, "/api/fraud-reports").hasAnyRole("ADMIN", "CITIZEN")
                        .requestMatchers(HttpMethod.PUT, "/api/fraud-reports/**").hasAnyRole("ADMIN", "CITIZEN")
                        .requestMatchers(HttpMethod.DELETE, "/api/fraud-reports/**").hasAnyRole("ADMIN", "CITIZEN")
                        .requestMatchers("/api/analyst-reports/**").hasAnyRole("ADMIN", "ANALYST", "OBSERVER")
                        .requestMatchers(HttpMethod.POST, "/api/election-results").hasAnyRole("ADMIN", "ANALYST")
                        .requestMatchers(HttpMethod.POST, "/api/election-results/bulk-upload").hasRole("ANALYST")
                        .requestMatchers(HttpMethod.PUT, "/api/election-results/**").hasAnyRole("ADMIN", "ANALYST")
                        .requestMatchers(HttpMethod.DELETE, "/api/election-results/**").hasAnyRole("ADMIN", "ANALYST")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2.successHandler(googleOAuth2SuccessHandler))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * CORS Configuration from Environment Variable
     * This replaces the hardcoded localhost configuration
     * Set APP_CORS_ALLOWED_ORIGINS in Railway environment variables
     */
    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        return corsConfig.corsConfigurationSource();
    }
}
