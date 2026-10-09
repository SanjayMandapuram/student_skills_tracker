package com.tracker.service;

import com.tracker.dto.AuthDtos.*;
import com.tracker.entity.Role;
import com.tracker.entity.User;
import com.tracker.exception.ApiException;
import com.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
  private final UserRepository users;
  private final PasswordEncoder encoder;
  private final AuthenticationManager auth;
  private final JwtService jwt;
  private final String facultyCode;

  public AuthService(UserRepository users, PasswordEncoder encoder, AuthenticationManager auth,
                     JwtService jwt, @Value("${app.faculty.access-code}") String facultyCode) {
    this.users = users;
    this.encoder = encoder;
    this.auth = auth;
    this.jwt = jwt;
    this.facultyCode = facultyCode;
  }

  public AuthResponse register(Register request) {
    if (!request.password().equals(request.confirmPassword())) {
      throw new ApiException("Passwords do not match", HttpStatus.BAD_REQUEST);
    }
    if (users.existsByEmailIgnoreCase(request.email())) {
      throw new ApiException("Email already registered", HttpStatus.CONFLICT);
    }
    Role role;
    try {
      role = Role.valueOf(request.role().trim().toUpperCase());
    } catch (IllegalArgumentException | NullPointerException ex) {
      throw new ApiException("Please select Student or Faculty", HttpStatus.BAD_REQUEST);
    }
    if (role == Role.ADMIN) {
      throw new ApiException("Admin accounts cannot be registered here", HttpStatus.FORBIDDEN);
    }
    if (role == Role.FACULTY && !facultyCode.equals(request.facultyCode())) {
      throw new ApiException("Invalid faculty access code. You cannot create a faculty account.", HttpStatus.FORBIDDEN);
    }

    User user = new User();
    user.setName(request.name().trim());
    user.setEmail(request.email().trim().toLowerCase());
    user.setPhone(request.phone());
    user.setPassword(encoder.encode(request.password()));
    user.setRole(role);
    users.save(user);
    return response(user, "Registration successful");
  }

  public AuthResponse login(Login request) {
    try {
      auth.authenticate(new UsernamePasswordAuthenticationToken(
          request.email().trim().toLowerCase(), request.password()));
    } catch (AuthenticationException ex) {
      throw new ApiException("Invalid email or password", HttpStatus.UNAUTHORIZED);
    }
    User user = users.findByEmailIgnoreCase(request.email()).orElseThrow();
    var details = org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
        .password(user.getPassword()).roles(user.getRole().name()).build();
    return new AuthResponse("Login successful", view(user), jwt.generate(details));
  }

  private AuthResponse response(User user, String message) {
    return new AuthResponse(message, view(user), null);
  }

  public UserView view(User user) {
    return new UserView(user.getId(), user.getName(), user.getEmail(), user.getPhone(),
        user.getDepartment(), user.getBranch(), user.getAcademicYear(), user.getInstitution(),
        user.getStudentId(), user.getGraduationYear(), user.getBiography(), user.getCareerInterests(),
        user.getTargetCareerRole(), user.getGithubUrl(), user.getLinkedinUrl(), user.getPortfolioUrl(),
        user.getProfilePhotoUrl(), user.getDesignation(), user.getRole().name());
  }
}
