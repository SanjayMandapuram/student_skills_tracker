package com.tracker.config;

import com.tracker.entity.Role;
import com.tracker.entity.User;
import com.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
  @Bean CommandLineRunner seed(UserRepository users, PasswordEncoder encoder,
      @Value("${seed.admin.email:}") String adminEmail, @Value("${seed.admin.password:}") String adminPassword,
      @Value("${seed.student.email:}") String studentEmail, @Value("${seed.student.password:}") String studentPassword) {
    return args -> {
      if (!adminEmail.isBlank() && !adminPassword.isBlank() && !users.existsByEmailIgnoreCase(adminEmail)) {
        User user = new User(); user.setName("System Administrator"); user.setEmail(adminEmail.trim().toLowerCase());
        user.setPassword(encoder.encode(adminPassword)); user.setRole(Role.ADMIN); users.save(user);
      }
      if (!studentEmail.isBlank() && !studentPassword.isBlank() && !users.existsByEmailIgnoreCase(studentEmail)) {
        User user = new User(); user.setName("Demo Student"); user.setEmail(studentEmail.trim().toLowerCase());
        user.setPassword(encoder.encode(studentPassword)); user.setRole(Role.STUDENT); users.save(user);
      }
    };
  }
}
