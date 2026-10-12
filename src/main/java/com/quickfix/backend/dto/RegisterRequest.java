
package com.quickfix.backend.dto;

import com.quickfix.backend.entity.UserRole;
import jakarta.validation.constraints.*;

import java.util.List;

public record RegisterRequest(
    @NotBlank @Size(max = 100)
    String name,

    @NotBlank @Email @Size(max = 150)
    String email,

    @NotBlank @Size(min = 8, max = 72)
    String password,

    @Size(max = 20)
    String phone,

    @NotNull
    UserRole role,

    @DecimalMin("1.0") @DecimalMax("100.0")
    Double serviceRadiusKm,

    List<@NotNull @Positive Long> categoryIds
) {}
