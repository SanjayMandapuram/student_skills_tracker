package com.tracker.entity;
import com.fasterxml.jackson.annotation.JsonIgnore; import jakarta.persistence.*; import jakarta.validation.constraints.*; import lombok.*;
@Entity @Table(name="portfolios", uniqueConstraints=@UniqueConstraint(columnNames="public_slug")) @Getter @Setter @NoArgsConstructor
public class Portfolio { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @NotBlank @Size(max=100) private String publicSlug; private boolean publicEnabled; private boolean showEmail; private boolean showSkills=true; private boolean showProjects=true; private boolean showCourses=true; private boolean showCertificates=true; @Size(max=2000) private String introduction; @OneToOne(optional=false) @JoinColumn(name="user_id", unique=true) @JsonIgnore private User user; }
