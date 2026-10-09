package com.tracker.repository;
import com.tracker.entity.*; import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface CertificationRepository extends JpaRepository<Certification,Long>{Page<Certification> findByUserId(Long id,Pageable pageable);List<Certification> findByUserId(Long id);long countByUserId(Long id);long countByUserIdAndStatus(Long id,CertStatus s);long countByStatus(CertStatus s);}
