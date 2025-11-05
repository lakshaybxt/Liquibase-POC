package com.testing.ex.repos;

import com.testing.ex.domain.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for User persistence operations.
 */
public interface UserRepository extends JpaRepository<User, Long> {
  /**
   * Check if an email is already registered.
   */
  boolean existsByEmail(String email);

  /**
   * Check if a username is already taken.
   */
  boolean existsByUsername(String username);

  /**
   * Find a user by email.
   */
  Optional<User> findByEmail(String email);

}
