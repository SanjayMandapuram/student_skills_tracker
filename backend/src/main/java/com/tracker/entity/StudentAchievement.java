package com.tracker.entity;
import com.fasterxml.jackson.annotation.JsonIgnore; import jakarta.persistence.*; import lombok.*; import java.time.LocalDateTime;
@Entity @Table(name="student_achievements", uniqueConstraints=@UniqueConstraint(columnNames={"user_id","code"})) @Getter @Setter @NoArgsConstructor
public class StudentAchievement { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(nullable=false,length=60) private String code; @Column(nullable=false,length=160) private String title; @Column(length=500) private String description; private int points; private LocalDateTime awardedAt; @ManyToOne(optional=false) @JoinColumn(name="user_id") @JsonIgnore private User user; @PrePersist void created(){if(awardedAt==null)awardedAt=LocalDateTime.now();} }
