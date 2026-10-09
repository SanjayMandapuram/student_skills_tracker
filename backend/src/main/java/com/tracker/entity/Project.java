package com.tracker.entity;
import com.fasterxml.jackson.annotation.JsonIgnore; import jakarta.persistence.*; import jakarta.validation.constraints.*; import lombok.*; import java.time.*;
@Entity @Table(name="projects") @Getter @Setter @NoArgsConstructor
public class Project { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @NotBlank @Size(max=180) private String title; @Size(max=2000) private String description; @Size(max=1000) private String technologies; @Pattern(regexp="^$|^https?://\\S+$") private String projectUrl; @Pattern(regexp="^$|^https?://\\S+$") private String repositoryUrl; private LocalDate completedDate; private boolean published; @ManyToOne(optional=false) @JoinColumn(name="user_id") @JsonIgnore private User user; }
