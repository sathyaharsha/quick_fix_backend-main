
package com.quickfix.backend.service;

import com.quickfix.backend.dto.CreateServiceRequest;
import com.quickfix.backend.dto.ServiceRequestResponse;
import com.quickfix.backend.entity.ServiceCategory;
import com.quickfix.backend.entity.ServiceRequest;
import com.quickfix.backend.entity.User;
import com.quickfix.backend.entity.UserRole;
import com.quickfix.backend.repository.ServiceCategoryRepository;
import com.quickfix.backend.repository.ServiceRequestRepository;
import com.quickfix.backend.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ServiceRequestService {

    private final ServiceRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ServiceCategoryRepository categoryRepository;

    public ServiceRequestService(
            ServiceRequestRepository requestRepository,
            UserRepository userRepository,
            ServiceCategoryRepository categoryRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public ServiceRequestResponse create(CreateServiceRequest input) {

        if (input.customerId() == null ||
            input.categoryId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Customer and category are required");
        }

        if (input.description() == null ||
            input.description().isBlank() ||
            input.description().trim().length() > 1000) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Enter a problem description of up to 1000 characters");
        }

        if (input.city() == null || input.city().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "City is required");
        }

        if (input.pincode() == null ||
            !input.pincode().trim().matches("[0-9]{6}")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Enter a valid 6-digit pincode");
        }

        User customer = userRepository.findById(input.customerId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Customer not found"));

        if (customer.getRole() != UserRole.CUSTOMER) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only customer accounts can create requests");
        }

        ServiceCategory category = categoryRepository
                .findById(input.categoryId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Service category not found"));

        ServiceRequest request = new ServiceRequest();
        request.setCustomer(customer);
        request.setCategory(category);
        request.setDescription(input.description().trim());
        request.setCity(input.city().trim());
        request.setPincode(input.pincode().trim());
        request.setLatitude(input.latitude());
        request.setLongitude(input.longitude());

        ServiceRequest saved = requestRepository.saveAndFlush(request);

        return ServiceRequestResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<ServiceRequestResponse> getCustomerRequests(Long customerId) {

        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Customer not found"));

        if (customer.getRole() != UserRole.CUSTOMER) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This account is not a customer");
        }

        return requestRepository
                .findByCustomer_IdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(ServiceRequestResponse::from)
                .toList();
    }
}
