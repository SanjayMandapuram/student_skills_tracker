package com.tracker.controller;

import com.tracker.entity.*;
import com.tracker.exception.ApiException;
import com.tracker.repository.CertificationRepository;
import com.tracker.service.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController @RequestMapping("/api/certifications")
public class EvidenceController {
  private final CertificationRepository certs; private final AccessService access; private final EvidenceStorageService storage;
  public EvidenceController(CertificationRepository c,AccessService a,EvidenceStorageService s){certs=c;access=a;storage=s;}
  @PostMapping(value="/{id}/evidence", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
  public Certification upload(@PathVariable Long id,@RequestPart("file") MultipartFile file,Authentication auth){Certification c=find(id);User u=access.current(auth);if(c.getUser().getId().equals(u.getId())==false)throw new ApiException("You do not have access to this record",HttpStatus.FORBIDDEN);if(c.getStatus()==CertStatus.APPROVED)throw new ApiException("Approved evidence cannot be replaced without resubmission",HttpStatus.CONFLICT);var stored=storage.store(file);c.setEvidenceStorageKey(stored.key());c.setEvidenceFileName(stored.name());c.setEvidenceContentType(stored.contentType());return certs.save(c);}
  @GetMapping("/{id}/evidence")
  public ResponseEntity<ByteArrayResource> download(@PathVariable Long id,Authentication auth){Certification c=find(id);User u=access.current(auth);if(!c.getUser().getId().equals(u.getId())&&u.getRole()==Role.STUDENT)throw new ApiException("Evidence access denied",HttpStatus.FORBIDDEN);if(c.getEvidenceStorageKey()==null)throw new ApiException("Evidence not found",HttpStatus.NOT_FOUND);return ResponseEntity.ok().contentType(MediaType.parseMediaType(c.getEvidenceContentType())).header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+c.getEvidenceFileName()+"\"").body(new ByteArrayResource(storage.read(c.getEvidenceStorageKey())));}
  private Certification find(Long id){return certs.findById(id).orElseThrow(()->new ApiException("Certification not found",HttpStatus.NOT_FOUND));}
}
