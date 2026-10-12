
package com.quickfix.backend.dto;

import com.quickfix.backend.entity.UserRole;

public record AuthResponse(
    Long id,
    String name,
    String email,
    String phone,
    UserRole role,
    Long workerId
) {}
