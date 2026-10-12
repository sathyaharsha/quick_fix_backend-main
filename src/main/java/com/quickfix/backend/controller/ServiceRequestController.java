
package com.quickfix.backend.controller;

import com.quickfix.backend.dto.CreateServiceRequest;
import com.quickfix.backend.dto.ServiceRequestResponse;
import com.quickfix.backend.service.ServiceRequestService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class ServiceRequestController {

    private final ServiceRequestService requestService;

    public ServiceRequestController(
            ServiceRequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceRequestResponse create(
            @RequestBody CreateServiceRequest request) {
        return requestService.create(request);
    }

    @GetMapping("/customer/{customerId}")
    public List<ServiceRequestResponse> getCustomerRequests(
            @PathVariable Long customerId) {
        return requestService.getCustomerRequests(customerId);
    }
}
