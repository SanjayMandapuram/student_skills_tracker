package com.tracker.entity;
import jakarta.persistence.*; import jakarta.validation.constraints.*; import lombok.*; import java.time.*;
@Entity @Table(name="recognitions") @Getter @Setter @NoArgsConstructor
public class Recognition { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @NotBlank @Size(max=180) private String title; @Size(max=2000) private String description; @Size(max=80) private String category; private LocalDate awardDate; private boolean visible=true; @ManyToOne(optional=false) @JoinColumn(name="student_id") private User student; @ManyToOne(optional=false) @JoinColumn(name="faculty_id") private User faculty; }
