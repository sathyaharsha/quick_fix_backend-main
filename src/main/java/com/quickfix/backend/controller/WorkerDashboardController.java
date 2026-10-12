
package com.quickfix.backend.controller;

import com.quickfix.backend.dto.ServiceRequestResponse;
import com.quickfix.backend.service.WorkerDashboardService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workers")
public class WorkerDashboardController {

    private final WorkerDashboardService workerDashboardService;

    public WorkerDashboardController(
            WorkerDashboardService workerDashboardService) {
        this.workerDashboardService = workerDashboardService;
    }

    @PutMapping("/{workerId}/availability")
    public Map<String, String> updateAvailability(
            @PathVariable Long workerId,
            @RequestBody Map<String, Boolean> body) {

        boolean online = Boolean.TRUE.equals(body.get("online"));
        workerDashboardService.setAvailability(workerId, online);

        return Map.of(
                "message",
                online ? "You are online" : "You are offline");
    }

    @PutMapping("/{workerId}/work-area")
    public Map<String, String> saveWorkArea(
            @PathVariable Long workerId,
            @RequestBody Map<String, String> body) {

        return workerDashboardService.saveWorkArea(
                workerId,
                body.get("city"),
                body.get("pincode"));
    }

    @GetMapping("/{workerId}/work-area")
    public Map<String, Object> getWorkArea(
            @PathVariable Long workerId) {
        return workerDashboardService.getWorkArea(workerId);
    }

    @GetMapping("/{workerId}/requests")
    public List<ServiceRequestResponse> getAvailableRequests(
            @PathVariable Long workerId) {
        return workerDashboardService.getAvailableRequests(workerId);
    }

    @PostMapping("/{workerId}/requests/{requestId}/accept")
    public Map<String, String> acceptRequest(
            @PathVariable Long workerId,
            @PathVariable Long requestId) {

        workerDashboardService.acceptRequest(workerId, requestId);

        return Map.of("message", "Request accepted successfully");
    }

    @GetMapping("/{workerId}/my-requests")
    public List<ServiceRequestResponse> getMyRequests(
            @PathVariable Long workerId) {
        return workerDashboardService.getMyRequests(workerId);
    }
}
