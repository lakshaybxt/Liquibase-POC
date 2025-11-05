package com.testing.ex.security;

import com.testing.ex.domain.entity.User;
import com.testing.ex.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service to load user-specific data during authentication.
 */
@Service
@RequiredArgsConstructor
public class TestingUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  /**
   * Loads the user by their email.
   *
   * @param email the email of the user
   * @return UserDetails object containing user information
   * @throws UsernameNotFoundException if the user is not found
   */
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email)
        .orElseThrow(
            () -> new UsernameNotFoundException("User with email " + email + " not found"));

    return new TestingUserDetails(user);
  }
}
