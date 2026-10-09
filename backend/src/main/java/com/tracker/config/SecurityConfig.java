package com.tracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
  @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
  @Bean org.springframework.security.authentication.AuthenticationManager authenticationManager(
      org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration c) throws Exception {
    return c.getAuthenticationManager();
  }
  @Bean SecurityFilterChain filter(HttpSecurity h, JwtAuthFilter jwt, FacultyAccessFilter faculty,
      @Value("${app.cors.allowed-origins}") String allowedOrigins) throws Exception {
    List<String> origins = Arrays.stream(allowedOrigins.split(",")).map(String::trim).filter(s -> !s.isBlank()).toList();
    return h.csrf(c -> c.disable())
        .cors(c -> c.configurationSource(req -> {
          CorsConfiguration config = new CorsConfiguration();
          config.setAllowedOrigins(origins);
          config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
          config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
          config.setAllowCredentials(false);
          return config;
        }))
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
        .authorizeHttpRequests(a -> a.requestMatchers("/api/auth/**", "/error").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/public/portfolio/**").permitAll()
            .requestMatchers("/api/goals/**", "/api/projects/**", "/api/portfolio/**", "/api/achievements/**", "/api/reports/student/**", "/api/career-roles/*/gap", "/api/analytics/student").hasRole("STUDENT")
            .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "FACULTY")
            .anyRequest().authenticated())
        .addFilterBefore(jwt, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(faculty, JwtAuthFilter.class)
        .build();
  }
}
