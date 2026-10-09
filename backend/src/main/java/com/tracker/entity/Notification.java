package com.tracker.entity;
import jakarta.persistence.*; import jakarta.validation.constraints.*; import lombok.*; import java.time.*;
@Entity @Table(name="notifications") @Getter @Setter @NoArgsConstructor
public class Notification { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @ManyToOne(optional=false) @JoinColumn(name="user_id") private User user; @NotBlank @Size(max=180) private String title; @Size(max=1000) private String message; @Size(max=300) private String link; private boolean read; private LocalDateTime createdAt; @PrePersist void created(){createdAt=LocalDateTime.now();} }
