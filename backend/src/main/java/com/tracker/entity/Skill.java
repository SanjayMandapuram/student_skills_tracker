package com.tracker.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*; import jakarta.validation.constraints.*; import lombok.*; import java.time.LocalDateTime;
@Entity @Table(name="skills") @Getter @Setter @NoArgsConstructor
public class Skill { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @NotBlank @Size(max=100) private String skillName; @NotBlank @Size(max=50) private String category; @NotBlank @Pattern(regexp="Beginner|Intermediate|Advanced|Expert") private String proficiency; @NotNull @DecimalMin("0") @DecimalMax("50") private Double experience; @Size(max=1000) private String description; private java.time.LocalDate acquiredDate; private java.time.LocalDate lastPracticedDate; private String evidenceUrl; private LocalDateTime createdAt; @ManyToOne(optional=false) @JoinColumn(name="user_id") @JsonIgnore private User user; @PrePersist void created(){createdAt=LocalDateTime.now();} }
