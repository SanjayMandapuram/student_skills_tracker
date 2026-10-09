package com.tracker.controller;

import com.tracker.entity.*;
import com.tracker.exception.ApiException;
import com.tracker.repository.*;
import com.tracker.service.AccessService;
import com.tracker.service.PdfService;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;

@RestController @RequestMapping("/api/reports")
public class ReportController {
  private final AccessService access; private final PdfService pdf; private final SkillRepository skills;
  private final CourseRepository courses; private final CertificationRepository certs;
  public ReportController(AccessService a, PdfService p, SkillRepository s, CourseRepository c, CertificationRepository x){access=a;pdf=p;skills=s;courses=c;certs=x;}

  @GetMapping(value="/student/achievement", produces="application/pdf")
  public ResponseEntity<byte[]> studentAchievement(Authentication auth){
    User u=access.current(auth); if(u.getRole()!=Role.STUDENT) throw new ApiException("Student access required",HttpStatus.FORBIDDEN);
    List<String> lines=new ArrayList<>(); lines.add("Student: "+u.getName()); lines.add("Generated: "+LocalDate.now()); lines.add("Email: "+u.getEmail()); lines.add("");
    lines.add("SKILLS"); skills.findByUserId(u.getId()).forEach(s->lines.add("- "+s.getSkillName()+" | "+s.getCategory()+" | "+s.getProficiency()));
    lines.add(""); lines.add("COURSES"); courses.findByUserId(u.getId()).forEach(c->lines.add("- "+c.getCourseName()+" | "+c.getProvider()+" | "+c.getStatus()));
    lines.add(""); lines.add("CERTIFICATES (APPROVED ONLY)"); certs.findByUserId(u.getId()).stream().filter(c->c.getStatus()==CertStatus.APPROVED).forEach(c->lines.add("- "+c.getCertificationName()+" | "+c.getIssuingOrganization()+" | "+c.getIssueDate()));
    return download(pdf.textDocument("Achievement Report",lines),"achievement-report.pdf");
  }

  @GetMapping(value="/student/resume", produces="application/pdf")
  public ResponseEntity<byte[]> resume(Authentication auth){
    User u=access.current(auth); if(u.getRole()!=Role.STUDENT) throw new ApiException("Student access required",HttpStatus.FORBIDDEN);
    List<String> lines=new ArrayList<>(); lines.add(u.getName()); lines.add(u.getEmail());
    if(u.getBiography()!=null) { lines.add(""); lines.add("SUMMARY"); lines.add(u.getBiography()); }
    lines.add(""); lines.add("SKILLS"); skills.findByUserId(u.getId()).forEach(s->lines.add(s.getSkillName()+" - "+s.getProficiency()));
    lines.add(""); lines.add("COURSES"); courses.findByUserId(u.getId()).stream().filter(c->"COMPLETED".equalsIgnoreCase(c.getStatus())).forEach(c->lines.add(c.getCourseName()+" - "+c.getProvider()));
    lines.add(""); lines.add("CERTIFICATIONS"); certs.findByUserId(u.getId()).stream().filter(c->c.getStatus()==CertStatus.APPROVED).forEach(c->lines.add(c.getCertificationName()+" - "+c.getIssuingOrganization()));
    return download(pdf.textDocument("Resume",lines),"resume.pdf");
  }

  @GetMapping(value="/faculty/certificates.csv", produces="text/csv")
  public ResponseEntity<byte[]> facultyCertificates(Authentication auth){
    User u=access.current(auth); if(u.getRole()==Role.STUDENT) throw new ApiException("Faculty access required",HttpStatus.FORBIDDEN);
    StringBuilder csv=new StringBuilder("certificate,issuer,status,issue_date,submitted_at\n");
    certs.findAll().forEach(c->csv.append(cell(c.getCertificationName())).append(',').append(cell(c.getIssuingOrganization())).append(',').append(cell(c.getStatus().name())).append(',').append(cell(String.valueOf(c.getIssueDate()))).append(',').append(cell(String.valueOf(c.getSubmittedAt()))).append('\n'));
    return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"certificate-report.csv\"").contentType(MediaType.parseMediaType("text/csv")).body(csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
  }
  private String cell(String value){String v=value==null?"":value.replace("\"","\"\"");if(v.startsWith("=")||v.startsWith("+")||v.startsWith("-")||v.startsWith("@"))v="'"+v;return "\""+v+"\"";}

  private ResponseEntity<byte[]> download(byte[] body,String name){return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+name+"\"").contentType(MediaType.APPLICATION_PDF).body(body);}
}
