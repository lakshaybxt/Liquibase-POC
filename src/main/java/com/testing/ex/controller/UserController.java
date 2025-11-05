package com.testing.ex.controller;

import com.testing.ex.domain.dto.request.LoginUserDto;
import com.testing.ex.domain.dto.request.RegisterUserDto;
import com.testing.ex.domain.dto.request.VerifyUserDto;
import com.testing.ex.domain.dto.response.LoginResponse;
import com.testing.ex.domain.entity.User;
import com.testing.ex.security.TestingUserDetails;
import com.testing.ex.service.AuthenticationService;
import com.testing.ex.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * REST controller that exposes user-related endpoints such as registration,
 * authentication (login) and account verification.
 *
 * <p>Endpoints are mounted under <code>/api/users</code>. This controller
 * delegates business logic to the {@link AuthenticationService} and returns
 * lightweight DTOs or HTTP status responses.</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Operations related to user registration, login and "
    + "verification")
public class UserController {

  private final AuthenticationService authenticationService;
  private final JwtService jwtService;

  /**
   * Register a new user account.
   *
   * @param request validated registration payload
   * @return the created User
   */
  @Operation(summary = "Register a new user", description = "Creates a new user account. Returns "
      + "the saved user.")
  @PostMapping(path = "/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterUserDto request) {
    User user = authenticationService.signup(request);
    // return ResponseEntity.ok(user);
    return ResponseEntity.ok("User registered successfully. Please check your email for code: " +
        user.getVerificationCode());
  }

  /**
   * Authenticate a user and return a JWT token on success.
   *
   * @param request login credentials
   * @return token and expiration information wrapped in {@link LoginResponse}
   */
  @Operation(summary = "Authenticate user (login)", description = "Authenticates credentials and "
      + "returns a JWT token and expiration time.")
  @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody LoginUserDto request) {
    UserDetails authenticatedUser = authenticationService.authenticate(request);
    LoginResponse loginResponse = LoginResponse.builder()
        .token(jwtService.generateToken(authenticatedUser))
        .expiration(jwtService.getExpirationTime())
        .build();
    return ResponseEntity.ok(loginResponse);
  }

  /**
   * Verify a user's account using a verification code previously issued at signup.
   *
   * @param request payload containing email and verification code
   * @return success message or bad request with error message
   */
  @Operation(summary = "Verify user account", description = "Verify a user's account using a "
      + "verification code sent during signup.")
  @PostMapping(path = "/verify")
  public ResponseEntity<?> verifyUser(@Valid @RequestBody VerifyUserDto request) {
    try {
      authenticationService.verifyUser(request);
      return ResponseEntity.ok("Account verified successfully");
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping(path = "/mock", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<LoginResponse> authenticateMock(
      @RequestParam("userId") Long userId,
      @Valid @RequestBody LoginUserDto request) {

    User user = User.builder()
        .id(userId)
        .username("ajay sodhi")
        .email(request.getEmail())
        .password(request.getPassword()) // {noop} means plain text for testing
        .enabled(true)
        .build();

    // Wrap it in your custom UserDetails
    UserDetails authenticatedUser = new TestingUserDetails(user);

    LoginResponse loginResponse = LoginResponse.builder()
        .token(jwtService.generateToken(authenticatedUser))
        .expiration(jwtService.getExpirationTime())
        .build();
    return ResponseEntity.ok(loginResponse);
  }


}
