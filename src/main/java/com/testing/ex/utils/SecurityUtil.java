package com.testing.ex.utils;

import com.testing.ex.security.TestingUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class for security-related operations.
 */
public class SecurityUtil {

  /**
   * Retrieves the ID of the currently authenticated user.
   *
   * @return the user ID if available, otherwise null
   */
  public static Long getCurrentUserId() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof TestingUserDetails user) {
      return user.getUser().getId(); // Assuming TestingUser has getUser()
    }
    return null;
  }
}
