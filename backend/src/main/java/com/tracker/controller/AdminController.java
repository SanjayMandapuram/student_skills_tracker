package com.tracker.controller;

import com.tracker.dto.AuthDtos.*;
import com.tracker.entity.*;
import com.tracker.exception.ApiException;
import com.tracker.repository.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController @RequestMapping("/api/admin")
public class AdminController {
  final UserRepository users; final SkillRepository skills; final CourseRepository courses;
  final CertificationRepository certs; final VerificationHistoryRepository history;
  public AdminController(UserRepository u, SkillRepository s, CourseRepository c, CertificationRepository x, VerificationHistoryRepository h){users=u;skills=s;courses=c;certs=x;history=h;}

  @GetMapping("/users") List<StudentSummary> users(@RequestParam(defaultValue="") String q){String query=q.toLowerCase(Locale.ROOT);return users.findAll().stream().filter(u->u.getRole()==Role.STUDENT&&(query.isBlank()||u.getName().toLowerCase(Locale.ROOT).contains(query)||u.getEmail().toLowerCase(Locale.ROOT).contains(query)||(u.getId()!=null&&u.getId().toString().equals(query)))).map(this::summary).toList();}
  private StudentSummary summary(User u){return new StudentSummary(u.getId(),u.getName(),u.getEmail(),u.getDepartment(),u.getBranch(),u.getAcademicYear(),skills.countByUserId(u.getId()),courses.countByUserId(u.getId()),certs.countByUserId(u.getId()),certs.countByUserIdAndStatus(u.getId(),CertStatus.APPROVED),certs.countByUserIdAndStatus(u.getId(),CertStatus.PENDING),certs.countByUserIdAndStatus(u.getId(),CertStatus.REJECTED));}
  @GetMapping("/users/{id}") Map<String,Object> student(@PathVariable Long id){User u=users.findById(id).orElseThrow(()->new ApiException("Student not found",HttpStatus.NOT_FOUND));if(u.getRole()!=Role.STUDENT)throw new ApiException("Student not found",HttpStatus.NOT_FOUND);Map<String,Object> out=new LinkedHashMap<>();out.put("student",u);out.put("skills",skills.findByUserId(id));out.put("courses",courses.findByUserId(id));out.put("certifications",certs.findByUserId(id));return out;}
  @GetMapping("/certifications") List<Certification> certs(@RequestParam(required=false) CertStatus status){return status==null?certs.findAll():certs.findAll().stream().filter(c->c.getStatus()==status).toList();}
  @GetMapping("/certifications/{id}") Certification cert(@PathVariable Long id){return certs.findById(id).orElseThrow(()->new ApiException("Certification not found",HttpStatus.NOT_FOUND));}
  @PutMapping("/certifications/{id}/approve") Certification approve(@PathVariable Long id,@RequestBody(required=false) Review r,Authentication a){return update(id,CertStatus.APPROVED,r==null?null:r.remarks(),a);}
  @PutMapping("/certifications/{id}/verify") Certification verify(@PathVariable Long id,@RequestBody(required=false) Review r,Authentication a){return approve(id,r,a);}
  @PutMapping("/certifications/{id}/reject") Certification reject(@PathVariable Long id,@Valid @RequestBody Review r,Authentication a){if(r==null||r.remarks()==null||r.remarks().isBlank())throw new ApiException("Remarks are required when rejecting",HttpStatus.BAD_REQUEST);return update(id,CertStatus.REJECTED,r.remarks(),a);}
  private Certification update(Long id,CertStatus status,String remarks,Authentication a){Certification c=certs.findById(id).orElseThrow(()->new ApiException("Certification not found",HttpStatus.NOT_FOUND));if(c.getStatus()!=CertStatus.PENDING)throw new ApiException("Only pending certifications can be reviewed",HttpStatus.CONFLICT);User reviewer=users.findByEmailIgnoreCase(a.getName()).orElseThrow();VerificationHistory h=new VerificationHistory();h.setCertification(c);h.setAdmin(reviewer);h.setPreviousStatus(c.getStatus());h.setNewStatus(status);h.setRemarks(remarks);history.save(h);c.setStatus(status);c.setReviewRemarks(remarks);c.setVerifiedAt(LocalDateTime.now());c.setVerifyingFaculty(reviewer);return certs.save(c);}
  @GetMapping("/verification-history") List<VerificationHistory> history(){return history.findAllByOrderByVerifiedAtDesc();}
  @GetMapping("/reports") Map<String,Object> reports(){return Map.of("students",users.findAll().stream().filter(u->u.getRole()==Role.STUDENT).count(),"skills",skills.count(),"courses",courses.count(),"certifications",certs.count(),"approved",certs.countByStatus(CertStatus.APPROVED),"pending",certs.countByStatus(CertStatus.PENDING),"rejected",certs.countByStatus(CertStatus.REJECTED));}
}
