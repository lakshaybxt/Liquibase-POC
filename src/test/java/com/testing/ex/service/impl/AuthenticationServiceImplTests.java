package com.testing.ex.service.impl;

import com.testing.ex.domain.dto.request.LoginUserDto;
import com.testing.ex.domain.dto.request.RegisterUserDto;
import com.testing.ex.domain.dto.request.VerifyUserDto;
import com.testing.ex.domain.entity.User;
import com.testing.ex.repos.UserRepository;
import com.testing.ex.security.TestingUserDetails;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationServiceImpl Tests")
public class AuthenticationServiceImplTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder encoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private User testUser;
    private RegisterUserDto registerRequest;
    private LoginUserDto loginRequest;
    private VerifyUserDto verifyRequest;

    @BeforeEach
    void init() {
        registerRequest = RegisterUserDto.builder()
                .email("testing@testmail.com")
                .username("testuser")
                .password("testing123")
                .build();

        loginRequest = LoginUserDto.builder()
                .email("testing@testmail.com")
                .password("testing123")
                .build();

        verifyRequest = VerifyUserDto.builder()
                .email("testing@testmail.com")
                .verificationCode("123456")
                .build();

        testUser = User.builder()
                .id(1L)
                .email("testing@testmail.com")
                .username("testuser")
                .password("testing123")
                .enabled(false)
                .verificationCode("123456")
                .verificationCodeExpiry(LocalDateTime.now())
                .build();
    }


    @Nested
    @DisplayName("Signup Method Tests")
    class SignupTests {

        @Test
        @DisplayName("Should register a new user successfully")
        void testSignup_Success() {
            // Given
            Mockito.when(userRepository.existsByEmail(registerRequest.getEmail()))
                    .thenReturn(false);
            Mockito.when(userRepository.existsByUsername(registerRequest.getUsername()))
                    .thenReturn(false);
            Mockito.when(encoder.encode(registerRequest.getPassword()))
                    .thenReturn("encodedPass");
            Mockito.when(userRepository.save(ArgumentMatchers.any(User.class)))
                    .thenReturn(testUser);

            // When
            final User result = authenticationService.signup(registerRequest);

            // Then
            assertNotNull(result);
            assertEquals(testUser, result);
            Mockito.verify(userRepository, Mockito.times(1))
                    .save(ArgumentMatchers.any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void testSignup_EmailAlreadyExists() {

            // Given
            Mockito.when(userRepository.existsByEmail(registerRequest.getEmail()))
                    .thenReturn(true);

            // When & Then
            assertThrows(IllegalArgumentException.class,
                    () -> authenticationService.signup(registerRequest));

            Mockito.verify(userRepository, Mockito.never())
                    .save(Mockito.any());


        }

        @Test
        @DisplayName("Should throw exception when username already exists")
        void testSignup_UsernameAlreadyExists() {
            // Given
            Mockito.when(userRepository.existsByEmail(registerRequest.getEmail()))
                    .thenReturn(false);
            Mockito.when(userRepository.existsByUsername(registerRequest.getUsername()))
                    .thenReturn(true);

            // When & Then
            assertThrows(IllegalArgumentException.class,
                    () -> authenticationService.signup(registerRequest));

            Mockito.verify(userRepository, Mockito.times(1))
                            .existsByEmail(Mockito.any());
            Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        @DisplayName("Should handle unexpected exceptions during signup")
        void testSignup_UnexpectedException() {
            // Given
            Mockito.when(userRepository.existsByEmail(registerRequest.getEmail()))
                    .thenReturn(false);
            Mockito.when(userRepository.existsByUsername(registerRequest.getUsername()))
                    .thenReturn(false);
            Mockito.when(encoder.encode(registerRequest.getPassword()))
                    .thenReturn("encodedPass");
            Mockito.when(userRepository.save(ArgumentMatchers.any(User.class)))
                    .thenThrow(new RuntimeException("Database error"));

            // When & Then
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> authenticationService.signup(registerRequest));

            assertEquals("Database error", ex.getMessage());
        }

    }

    @Nested
    @DisplayName("Login Method Tests")
    class LoginTests {

        @Test
        @DisplayName("Should authenticate user successfully")
        void testLogin_Success() {
            // Given
            testUser.setEnabled(true);
            testUser.setVerificationCode(null);
            testUser.setVerificationCodeExpiry(null);

            Mockito.when(userRepository.findByEmail(loginRequest.getEmail()))
                    .thenReturn(Optional.of(testUser));

            Mockito.when(authenticationManager.authenticate(ArgumentMatchers.any()))
                    .thenReturn(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            UserDetails userDetails = new TestingUserDetails(testUser);
            Mockito.when(userDetailsService.loadUserByUsername(loginRequest.getEmail()))
                    .thenReturn(userDetails);
            // When
            UserDetails result = assertDoesNotThrow(() -> authenticationService.authenticate(loginRequest));

            // Then
            assertNotNull(result);
            assertEquals(testUser.getEmail(), result.getUsername());

            Mockito.verify(authenticationManager, Mockito.times(1))
                    .authenticate(ArgumentMatchers.any());
            Mockito.verify(userRepository, Mockito.times(1))
                    .findByEmail(testUser.getEmail());
            Mockito.verify(userDetailsService, Mockito.times(1))
                    .loadUserByUsername(testUser.getEmail());
            Mockito.verifyNoMoreInteractions(userRepository, authenticationManager, userDetailsService);
        }

        @Test
        @DisplayName("Should throw exception for email not verified")
        void testLogin_EmailNotVerified() {
            // Given
            testUser.setEnabled(false);
            Mockito.when(userRepository.findByEmail(loginRequest.getEmail()))
                    .thenReturn(Optional.of(testUser));
            // When
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> authenticationService.authenticate(loginRequest));

            // Then
            assertEquals("Email not verified", ex.getMessage());

            Mockito.verify(userRepository, Mockito.times(1))
                    .findByEmail(testUser.getEmail());
            Mockito.verify(userDetailsService, Mockito.never())
                    .loadUserByUsername(testUser.getEmail());
        }

        @Test
        @DisplayName("Should throw BadCredentialsException for invalid credentials")
        void testLogin_InvalidCredentials() {
            // Given
            testUser.setEnabled(true);
            Mockito.when(userRepository.findByEmail(loginRequest.getEmail()))
                    .thenReturn(Optional.of(testUser));

            Mockito.doThrow(BadCredentialsException.class)
                    .when(authenticationManager)
                    .authenticate(ArgumentMatchers.any());

            // When
            BadCredentialsException ex = assertThrows(BadCredentialsException.class,
                    () -> authenticationService.authenticate(loginRequest));

            // Then
            assertEquals("Invalid username or password", ex.getMessage());
            Mockito.verify(authenticationManager, Mockito.times(1))
                    .authenticate(ArgumentMatchers.any());
            Mockito.verify(userRepository, Mockito.times(1))
                    .findByEmail(testUser.getEmail());
            Mockito.verify(userDetailsService, Mockito.never())
                    .loadUserByUsername(testUser.getEmail());
        }

        @Test
        @DisplayName("Should handle unexpected exceptions during login")
        void testLogin_UnexpectedException() {
            // Given
            testUser.setEnabled(true);
            Mockito.when(userRepository.findByEmail(loginRequest.getEmail()))
                    .thenReturn(Optional.of(testUser));

            Mockito.when(authenticationManager.authenticate(ArgumentMatchers.any()))
                    .thenThrow(new RuntimeException("Authentication error"));

            // When
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> authenticationService.authenticate(loginRequest));

            // Then
            assertEquals("Authentication error", ex.getMessage());
            Mockito.verify(authenticationManager, Mockito.times(1))
                    .authenticate(ArgumentMatchers.any());
            Mockito.verify(userRepository, Mockito.times(1))
                    .findByEmail(testUser.getEmail());
            Mockito.verify(userDetailsService, Mockito.never())
                    .loadUserByUsername(testUser.getEmail());
        }
    }

    @Nested
    @DisplayName("Verify User Method Tests")
    class VerifyUserTests {

        @Test
        @DisplayName("Should verify user successfully")
        void testVerifyUser_Success() {
            // Given
            testUser.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(15));
            Mockito.when(userRepository.findByEmail(verifyRequest.getEmail()))
                    .thenReturn(Optional.of(testUser));

            // When
            assertDoesNotThrow(() -> authenticationService.verifyUser(verifyRequest));

            // Then
            Mockito.verify(userRepository, Mockito.times(1))
                    .save(ArgumentMatchers.argThat(user ->
                            user.isEnabled() &&
                                    user.getVerificationCode() == null &&
                                    user.getVerificationCodeExpiry() == null
                    ));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for user is already verified")
        void testVerifyUser_UserAlreadyVerified() {
            // Given
            testUser.setEnabled(true);
            Mockito.when(userRepository.findByEmail(verifyRequest.getEmail()))
                    .thenReturn(Optional.of(testUser));

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> authenticationService.verifyUser(verifyRequest));

            assertEquals("User already verified", ex.getMessage());
            Mockito.verify(userRepository, Mockito.times(1))
                    .findByEmail(verifyRequest.getEmail());

            Mockito.verify(userRepository, Mockito.never())
                    .save(ArgumentMatchers.any());

        }

        @Test
        @DisplayName("Should throw exception when verificaiton code is expired")
        void testVerifyUser_CodeExpired() {
            verifyRequest.setVerificationCode("123456");
            testUser.setVerificationCode("123456");
            testUser.setVerificationCodeExpiry(LocalDateTime.now().minusMinutes(10));

            Mockito.when(userRepository.findByEmail(verifyRequest.getEmail()))
                    .thenReturn(Optional.of(testUser));

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> authenticationService.verifyUser(verifyRequest));

            assertEquals("Verification code expired", ex.getMessage());
            Mockito.verify(userRepository, Mockito.times(1))
                    .findByEmail(verifyRequest.getEmail());

            Mockito.verify(userRepository, Mockito.never())
                    .save(ArgumentMatchers.any());

        }

        @Test
        @DisplayName("Should throw exception when verification code is invalid")
        void testVerifyUser_InvalidCode() {
            verifyRequest.setVerificationCode("654321");
            testUser.setVerificationCode("123456");
            testUser.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(10));

            Mockito.when(userRepository.findByEmail(verifyRequest.getEmail()))
                    .thenReturn(Optional.of(testUser));

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> authenticationService.verifyUser(verifyRequest));

            assertEquals("Invalid verification code", ex.getMessage());
            Mockito.verify(userRepository, Mockito.times(1))
                    .findByEmail(verifyRequest.getEmail());

            Mockito.verify(userRepository, Mockito.never())
                    .save(ArgumentMatchers.any());


        }

        @Test
        @DisplayName("Should throw error when user not found")
        void testVerifyUser_UserNotFound() {
            Mockito.when(userRepository.findByEmail(verifyRequest.getEmail()))
                    .thenReturn(Optional.empty());

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> authenticationService.verifyUser(verifyRequest));

            assertEquals("User not found", ex.getMessage());
            Mockito.verify(userRepository, Mockito.times(1))
                    .findByEmail(verifyRequest.getEmail());

            Mockito.verify(userRepository, Mockito.never())
                    .save(ArgumentMatchers.any());


        }

    }
}

