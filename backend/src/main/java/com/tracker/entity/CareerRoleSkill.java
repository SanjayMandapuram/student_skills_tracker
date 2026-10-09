package com.tracker.entity;
import jakarta.persistence.*; import jakarta.validation.constraints.*; import lombok.*;
@Entity @Table(name="career_role_skills", uniqueConstraints=@UniqueConstraint(columnNames={"role_id","skill_name"})) @Getter @Setter @NoArgsConstructor
public class CareerRoleSkill { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @ManyToOne(optional=false) @JoinColumn(name="role_id") private CareerRole role; @NotBlank private String skillName; @NotBlank private String targetProficiency; @Size(max=500) private String recommendation; }
