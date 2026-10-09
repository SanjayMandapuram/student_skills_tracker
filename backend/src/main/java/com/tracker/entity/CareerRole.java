package com.tracker.entity;
import jakarta.persistence.*; import jakarta.validation.constraints.*; import lombok.*; import java.util.*;
@Entity @Table(name="career_roles") @Getter @Setter @NoArgsConstructor
public class CareerRole { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @NotBlank @Column(unique=true) private String name; @OneToMany(mappedBy="role",cascade=CascadeType.ALL,orphanRemoval=true) private List<CareerRoleSkill> skills=new ArrayList<>(); }
