package com.testing.ex.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for verifying a user.
 */
@Getter
@Setter
public class VerifyUserDto {

  @NotBlank(message = "Verification code is required")
  private String verificationCode;

  @Email(message = "Email format is not valid")
  @NotBlank(message = "Email is required")
  private String email;
}
