
package com.quickfix.backend.controller;

import com.quickfix.backend.service.AdminDashboardService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(
            AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard(
            @RequestParam Long adminUserId) {
        return adminDashboardService.getDashboard(adminUserId);
    }
}
