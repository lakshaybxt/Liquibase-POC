package com.testing.ex.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Configuration class for application security settings.
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

  private final UserDetailsService userDetailsService;

  /**
   * Configures the AuthenticationManager bean.
   *
   * @param configuration the authentication configuration
   * @return the authentication manager
   * @throws Exception if an error occurs during configuration
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {
    return configuration.getAuthenticationManager();
  }

  /**
   * Configures the AuthenticationProvider bean.
   *
   * @return the authentication provider
   */
  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setPasswordEncoder(bcryptpasswordencoder());
    provider.setUserDetailsService(userDetailsService);
    return provider;
  }

  /**
   * Configures the BCryptPasswordEncoder bean.
   *
   * @return the bcrypt password encoder
   */
  @Bean
  public BCryptPasswordEncoder bcryptpasswordencoder() {
    return new BCryptPasswordEncoder(12);
  }
}
