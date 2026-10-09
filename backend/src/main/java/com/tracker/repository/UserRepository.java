package com.tracker.repository;
import com.tracker.entity.*; import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface UserRepository extends JpaRepository<User,Long>{Page<User> findByRole(Role role,Pageable pageable);Optional<User> findByEmailIgnoreCase(String email);boolean existsByEmailIgnoreCase(String email);List<User> findByRoleAndNameContainingIgnoreCaseOrRoleAndEmailContainingIgnoreCase(Role r1,String n,Role r2,String e);}
