
package com.quickfix.backend.controller;

import com.quickfix.backend.dto.ServiceRequestResponse;
import com.quickfix.backend.service.WorkerRequestStatusService;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/workers")
public class WorkerRequestStatusController {

    private final WorkerRequestStatusService statusService;

    public WorkerRequestStatusController(
            WorkerRequestStatusService statusService) {
        this.statusService = statusService;
    }

    @PatchMapping("/{workerId}/requests/{requestId}/status")
    public ServiceRequestResponse updateStatus(
            @PathVariable Long workerId,
            @PathVariable Long requestId,
            @RequestBody Map<String, String> body) {

        String status = body.get("status");

        if (status == null || status.isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Status is required");
        }

        return statusService.updateStatus(workerId, requestId, status);
    }
}
