
package com.quickfix.backend.dto;

import com.quickfix.backend.entity.ServiceRequest;

import java.time.LocalDateTime;

public record ServiceRequestResponse(
        Long id,
        Long categoryId,
        String categoryName,
        String description,
        String city,
        String pincode,
        String status,
        LocalDateTime createdAt
) {
    public static ServiceRequestResponse from(ServiceRequest request) {
        return new ServiceRequestResponse(
                request.getId(),
                request.getCategory().getId(),
                request.getCategory().getName(),
                request.getDescription(),
                request.getCity(),
                request.getPincode(),
                request.getStatus().name(),
                request.getCreatedAt()
        );
    }
}
