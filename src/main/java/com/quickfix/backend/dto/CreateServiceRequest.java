
package com.quickfix.backend.dto;

public record CreateServiceRequest(
        Long customerId,
        Long categoryId,
        String description,
        String city,
        String pincode,
        Double latitude,
        Double longitude
) {}
