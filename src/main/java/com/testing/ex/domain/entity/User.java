package com.testing.ex.domain.entity;

import com.testing.ex.domain.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User entity representing application users.
 * Includes fields for verification code and its expiration.
 * Handles automatic timestamping for creation and updates.
 */
@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Builder.Default
  private boolean enabled = false;

  @Column(name = "verification_code")
  private String verificationCode;

  private Role role;

  @Column(name = "verification_expiration")
  private LocalDateTime verificationCodeExpiry;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  /**
   * Sets the creation and update timestamps before persisting.
   */
  @PrePersist
  public void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  /**
   * Updates the update timestamp before updating.
   */
  @PreUpdate
  public void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

}
