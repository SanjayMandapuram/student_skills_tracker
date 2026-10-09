package com.tracker.repository;
import com.tracker.entity.Skill; import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface SkillRepository extends JpaRepository<Skill,Long>{Page<Skill> findByUserId(Long id,Pageable pageable);List<Skill> findByUserId(Long id);long countByUserId(Long id);}
