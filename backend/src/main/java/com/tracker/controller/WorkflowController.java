package com.tracker.controller;

import com.tracker.entity.CertStatus;
import com.tracker.entity.Certification;
import com.tracker.entity.User;
import com.tracker.exception.ApiException;
import com.tracker.repository.CertificationRepository;
import com.tracker.repository.CourseRepository;
import com.tracker.repository.SkillRepository;
import com.tracker.repository.UserRepository;
import com.tracker.service.AccessService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/** Small workflow endpoints kept separate from the legacy CRUD controller. */
@RestController
@RequestMapping("/api")
public class WorkflowController {
  private final CertificationRepository certifications;
  private final AccessService access;
  private final UserRepository users;
  private final SkillRepository skills;
  private final CourseRepository courses;

  public WorkflowController(CertificationRepository certifications, AccessService access,
      UserRepository users, SkillRepository skills, CourseRepository courses) {
    this.certifications = certifications; this.access = access; this.users = users;
    this.skills = skills; this.courses = courses;
  }

  @PutMapping("/certifications/{id}/resubmit")
  public Certification resubmit(@PathVariable Long id, @Valid @RequestBody Certification request,
      Authentication authentication) {
    User student = access.current(authentication);
    if (!student.getRole().name().equals("STUDENT"))
      throw new ApiException("Student access required", HttpStatus.FORBIDDEN);
    Certification current = certifications.findById(id)
        .orElseThrow(() -> new ApiException("Certification not found", HttpStatus.NOT_FOUND));
    if (!current.getUser().getId().equals(student.getId()))
      throw new ApiException("You do not have access to this record", HttpStatus.FORBIDDEN);
    if (current.getStatus() != CertStatus.REJECTED)
      throw new ApiException("Only rejected certifications can be resubmitted", HttpStatus.CONFLICT);
    current.setCertificationName(request.getCertificationName());
    current.setIssuingOrganization(request.getIssuingOrganization());
    current.setIssueDate(request.getIssueDate());
    current.setExpiryDate(request.getExpiryDate());
    current.setCredentialId(request.getCredentialId());
    current.setCategory(request.getCategory());
    current.setCredentialUrl(request.getCredentialUrl());
    current.setDescription(request.getDescription());
    current.setStatus(CertStatus.PENDING);
    current.setReviewRemarks(null);
    current.setVerifiedAt(null);
    current.setVerifyingFaculty(null);
    current.setSubmittedAt(LocalDateTime.now());
    return certifications.save(current);
  }

  @GetMapping("/faculty/dashboard")
  public Map<String, Object> facultyDashboard(Authentication authentication) {
    User user = access.current(authentication);
    if (user.getRole().name().equals("STUDENT"))
      throw new ApiException("Faculty access required", HttpStatus.FORBIDDEN);
    Map<String, Object> result = new LinkedHashMap<>();
    result.put("students", users.findAll().stream().filter(x -> x.getRole().name().equals("STUDENT")).count());
    result.put("skills", skills.count()); result.put("courses", courses.count());
    result.put("certificates", certifications.count());
    result.put("approved", certifications.countByStatus(CertStatus.APPROVED));
    result.put("pending", certifications.countByStatus(CertStatus.PENDING));
    result.put("rejected", certifications.countByStatus(CertStatus.REJECTED));
    return result;
  }
}
