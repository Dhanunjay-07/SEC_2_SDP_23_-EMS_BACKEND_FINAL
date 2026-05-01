package com.election.evm.config;

import com.election.evm.security.JwtAuthenticationFilter;
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

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigFromEnv corsConfig;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CorsConfigFromEnv corsConfig
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.corsConfig = corsConfig;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Enable CORS using CorsConfigFromEnv
                .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))

                // Disable CSRF for API/JWT
                .csrf(csrf -> csrf.disable())

                // Stateless for JWT
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // IMPORTANT: Allow all preflight requests
                        .requestMatchers(HttpMethod.OPTIONS, "/").permitAll()

                        // Health endpoints
                        .requestMatchers(
                                HttpMethod.GET,
                                "/",
                                "/health",
                                "/api/health"
                        ).permitAll()

                        // Swagger
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/",
                                "/v3/api-docs/"
                        ).permitAll()

                        // Auth APIs
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/login",
                                "/api/auth/login/",
                                "/api/auth/register",
                                "/api/auth/register/",
                                "/api/auth/otp/send",
                                "/api/auth/otp/verify",
                                "/api/auth/refresh"
                        ).permitAll()

                        // OAuth / Google Login
                        .requestMatchers(
                                "/oauth2/","/login/"
                        ).permitAll()

                        // Current user
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/auth/me"
                        ).authenticated()

                        // Election results
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/election-results"
                        ).hasAnyRole("ADMIN", "CITIZEN", "OBSERVER", "ANALYST")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/election-results"
                        ).hasAnyRole("ADMIN", "ANALYST")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/election-results/bulk-upload"
                        ).hasRole("ANALYST")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/election-results/"
                        ).hasAnyRole("ADMIN", "ANALYST")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/election-results/"
                        ).hasAnyRole("ADMIN", "ANALYST")

                        // Admin
                        .requestMatchers(
                                "/api/users/",
                                "/api/dashboard/"
                        ).hasRole("ADMIN")

                        // Incidents
                        .requestMatchers(
                                "/api/incidents/"
                        ).hasAnyRole("ADMIN", "OBSERVER")

                        // Fraud Reports
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/fraud-reports"
                        ).hasAnyRole("ADMIN", "CITIZEN", "OBSERVER")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/fraud-reports"
                        ).hasAnyRole("ADMIN", "CITIZEN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/fraud-reports/"
                        ).hasAnyRole("ADMIN", "CITIZEN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/fraud-reports/"
                        ).hasAnyRole("ADMIN", "CITIZEN")

                        // Analyst Reports
                        .requestMatchers(
                                "/api/analyst-reports/**"
                        ).hasAnyRole("ADMIN", "ANALYST", "OBSERVER")

                        // Everything else
                        .anyRequest().authenticated()
                )

                // JWT Filter
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}