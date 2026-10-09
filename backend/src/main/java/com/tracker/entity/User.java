package com.tracker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name="users") @Getter @Setter @NoArgsConstructor
public class User {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false,length=100) private String name;
  @Column(nullable=false,unique=true,length=150) private String email;
  @JsonIgnore @Column(nullable=false) private String password;
  @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private Role role;
  @Column(length=30) private String phone;
  @Column(length=100) private String department;
  @Column(length=100) private String branch;
  @Column(length=30) private String academicYear;
  @Column(length=180) private String institution;
  @Column(length=30) private String studentId;
  @Column(length=30) private String graduationYear;
  @Column(length=2000) private String biography;
  @Column(length=500) private String careerInterests;
  @Column(length=150) private String targetCareerRole;
  @Column(length=300) private String githubUrl;
  @Column(length=300) private String linkedinUrl;
  @Column(length=300) private String portfolioUrl;
  @Column(length=300) private String profilePhotoUrl;
  @Column(length=100) private String designation;
  private LocalDateTime createdAt;
  @PrePersist void created(){ createdAt=LocalDateTime.now(); }
}
