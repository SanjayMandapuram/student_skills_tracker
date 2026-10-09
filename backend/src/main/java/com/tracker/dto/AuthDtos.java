package com.tracker.dto;
import jakarta.validation.constraints.*;
public final class AuthDtos {
  private AuthDtos(){}
  public record Register(@NotBlank @Size(max=100) String name,@NotBlank @Email String email,@Pattern(regexp="^$|[0-9+\\-\\s]{7,20}") String phone,@NotBlank @Size(min=8,max=72) String password,@NotBlank String confirmPassword,@NotBlank String role,String facultyCode){}
  public record Login(@NotBlank @Email String email,@NotBlank String password){}
  public record ForgotPassword(@NotBlank @Email String email){}
  public record UserView(Long id,String name,String email,String phone,String department,String branch,String academicYear,String institution,String studentId,String graduationYear,String biography,String careerInterests,String targetCareerRole,String githubUrl,String linkedinUrl,String portfolioUrl,String profilePhotoUrl,String designation,String role){}
  public record Profile(@NotBlank @Size(max=100) String name,@Pattern(regexp="^$|[0-9+\\-\\s]{7,20}") String phone,String department,String branch,String academicYear,String institution,String studentId,String graduationYear,@Size(max=2000) String biography,@Size(max=500) String careerInterests,String targetCareerRole,@Pattern(regexp="^$|^https?://\\S+$") String githubUrl,@Pattern(regexp="^$|^https?://\\S+$") String linkedinUrl,@Pattern(regexp="^$|^https?://\\S+$") String portfolioUrl,String profilePhotoUrl,String designation){}
  public record StudentSummary(Long id,String name,String email,String department,String branch,String academicYear,long skills,long courses,long certificates,long approved,long pending,long rejected){}
  public record FacultyCode(@NotBlank String code){}
  public record AuthResponse(String message,UserView user,String token){}
  public record Review(String remarks){}
}
