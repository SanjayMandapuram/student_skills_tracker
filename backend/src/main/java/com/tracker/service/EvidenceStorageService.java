package com.tracker.service;

import com.tracker.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.Set;
import java.util.UUID;

@Service
public class EvidenceStorageService {
  private static final long MAX_BYTES = 10 * 1024 * 1024;
  private static final Set<String> TYPES = Set.of("application/pdf", "image/jpeg", "image/png");
  private final Path root;
  public EvidenceStorageService(@Value("${app.storage.local-directory:./data/evidence}") String directory) { root=Paths.get(directory).toAbsolutePath().normalize(); }
  public StoredFile store(MultipartFile file) {
    if(file==null||file.isEmpty()||file.getSize()>MAX_BYTES) throw new ApiException("Evidence must be a non-empty PDF, JPEG, or PNG no larger than 10 MB",HttpStatus.BAD_REQUEST);
    String type=file.getContentType()==null?"":file.getContentType().toLowerCase(); if(!TYPES.contains(type)||!magicMatches(file,type)) throw new ApiException("Unsupported or invalid evidence file",HttpStatus.BAD_REQUEST);
    String key=UUID.randomUUID().toString(); try { Files.createDirectories(root); Path target=root.resolve(key).normalize(); if(!target.getParent().equals(root)) throw new ApiException("Invalid storage key",HttpStatus.BAD_REQUEST); Files.copy(file.getInputStream(),target,StandardCopyOption.REPLACE_EXISTING); return new StoredFile(key,file.getOriginalFilename()==null?"evidence":file.getOriginalFilename().replaceAll("[^A-Za-z0-9._-]","_"),type); } catch(IOException e){throw new ApiException("Evidence could not be stored",HttpStatus.INTERNAL_SERVER_ERROR);}
  }
  public byte[] read(String key){if(key==null||!key.matches("^[0-9a-fA-F-]{36}$"))throw new ApiException("Evidence not found",HttpStatus.NOT_FOUND);try{return Files.readAllBytes(root.resolve(key).normalize());}catch(IOException e){throw new ApiException("Evidence not found",HttpStatus.NOT_FOUND);}}
  private boolean magicMatches(MultipartFile f,String type){try{byte[] b=f.getBytes();if(type.equals("application/pdf"))return b.length>4&&b[0]=='%'&&b[1]=='P'&&b[2]=='D'&&b[3]=='F';if(type.equals("image/png"))return b.length>8&&b[0]=='\u0089'&&b[1]=='P'&&b[2]=='N'&&b[3]=='G';return b.length>3&&(b[0]&255)==255&&(b[1]&255)==216&&(b[2]&255)==255;}catch(IOException e){return false;}}
  public record StoredFile(String key,String name,String contentType){}
}
