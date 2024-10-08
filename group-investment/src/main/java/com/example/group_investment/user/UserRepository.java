package com.example.group_investment.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Boolean existsByLoginId(String loginId);
    Boolean existsByEmail(String email);
    Optional<User> findByLoginId(String loginId);
    Optional<User> findByEmail(String email);
}
