package com.tracker.repository;
import com.tracker.entity.Course; import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface CourseRepository extends JpaRepository<Course,Long>{Page<Course> findByUserId(Long id,Pageable pageable);List<Course> findByUserId(Long id);long countByUserId(Long id);}
