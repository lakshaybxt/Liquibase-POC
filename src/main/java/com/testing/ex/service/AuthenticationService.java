package com.testing.ex.service;

import com.testing.ex.domain.dto.request.LoginUserDto;
import com.testing.ex.domain.dto.request.RegisterUserDto;
import com.testing.ex.domain.dto.request.VerifyUserDto;
import com.testing.ex.domain.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Service contract for authentication-related operations such as signup,
 * login and account verification.
 */
public interface AuthenticationService {
  /**
   * Register a new user account.
   *
   * @param request registration payload
   * @return saved {@link User} entity
   */
  User signup(RegisterUserDto request);

  /**
   * Authenticate a user and return {@link UserDetails} for the authenticated user.
   *
   * @param request login request containing credentials
   * @return authenticated {@link UserDetails}
   */
  UserDetails authenticate(LoginUserDto request);

  /**
   * Verify a user's account using a previously issued verification code.
   *
   * @param request verification payload
   */
  void verifyUser(VerifyUserDto request);
}
