package com.tracker.entity;
import jakarta.persistence.*; import lombok.*; import java.time.*;
@Entity @Table(name="workshop_registrations", uniqueConstraints=@UniqueConstraint(columnNames={"workshop_id","student_id"})) @Getter @Setter @NoArgsConstructor
public class WorkshopRegistration { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @ManyToOne(optional=false) @JoinColumn(name="workshop_id") private Workshop workshop; @ManyToOne(optional=false) @JoinColumn(name="student_id") private User student; private LocalDateTime registeredAt; private boolean attended; @PrePersist void created(){registeredAt=LocalDateTime.now();} }
