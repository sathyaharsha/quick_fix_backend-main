
package com.quickfix.backend.service;

import com.quickfix.backend.entity.ServiceRequest;
import com.quickfix.backend.entity.User;
import com.quickfix.backend.entity.UserRole;
import com.quickfix.backend.entity.Worker;
import com.quickfix.backend.repository.ServiceRequestRepository;
import com.quickfix.backend.repository.UserRepository;
import com.quickfix.backend.repository.WorkerRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;
    private final ServiceRequestRepository requestRepository;

    public AdminDashboardService(
            UserRepository userRepository,
            WorkerRepository workerRepository,
            ServiceRequestRepository requestRepository) {
        this.userRepository = userRepository;
        this.workerRepository = workerRepository;
        this.requestRepository = requestRepository;
    }

    private void checkAdmin(Long adminUserId) {
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Admin account not found"));

        if (admin.getRole() != UserRole.ADMIN) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Admin access required");
        }
    }

    private Map<String, Object> userDetails(User user) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", user.getId());
        result.put("name", user.getName());
        result.put("email", user.getEmail());
        result.put("phone", user.getPhone());
        result.put("role", user.getRole().name());
        result.put("createdAt", user.getCreatedAt());
        return result;
    }

    private Map<String, Object> workerDetails(Worker worker) {
        Map<String, Object> result = userDetails(worker.getUser());
        result.put("workerId", worker.getId());
        result.put("workCity", worker.getWorkCity());
        result.put("workPincode", worker.getWorkPincode());
        result.put("online", worker.isOnline());
        result.put("available", worker.isAvailable());
        result.put("serviceRadiusKm", worker.getServiceRadiusKm());
        return result;
    }

    private Map<String, Object> requestDetails(ServiceRequest request) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", request.getId());
        result.put("categoryId", request.getCategory().getId());
        result.put("categoryName", request.getCategory().getName());
        result.put("description", request.getDescription());
        result.put("city", request.getCity());
        result.put("pincode", request.getPincode());
        result.put("status", request.getStatus().name());
        result.put("createdAt", request.getCreatedAt());
        result.put("customer", userDetails(request.getCustomer()));

        Worker worker = request.getWorker();
        result.put("provider",
                worker == null ? null : workerDetails(worker));

        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboard(Long adminUserId) {
        checkAdmin(adminUserId);

        List<User> allUsers = userRepository.findAll();
        List<Worker> workers = workerRepository.findAll();
        List<ServiceRequest> requests = requestRepository.findAll();

        List<Map<String, Object>> customers = allUsers.stream()
                .filter(user -> user.getRole() == UserRole.CUSTOMER)
                .map(this::userDetails)
                .toList();

        List<Map<String, Object>> providers = workers.stream()
                .map(this::workerDetails)
                .toList();

        List<Map<String, Object>> allRequests = requests.stream()
                .map(this::requestDetails)
                .toList();

        List<Map<String, Object>> completedRequests = requests.stream()
                .filter(request ->
                        "COMPLETED".equals(request.getStatus().name()))
                .map(this::requestDetails)
                .toList();

        long pendingCount = requests.stream()
                .filter(request ->
                        "PENDING".equals(request.getStatus().name()))
                .count();

        long activeCount = requests.stream()
                .filter(request -> {
                    String status = request.getStatus().name();
                    return "ACCEPTED".equals(status)
                            || "IN_PROGRESS".equals(status);
                })
                .count();

        long onlineCount = workers.stream()
                .filter(Worker::isOnline)
                .count();

        Map<String, Object> dashboard = new LinkedHashMap<>();
        dashboard.put("totalUsers", allUsers.size());
        dashboard.put("totalCustomers", customers.size());
        dashboard.put("totalProviders", providers.size());
        dashboard.put("onlineProviders", onlineCount);
        dashboard.put("totalRequests", allRequests.size());
        dashboard.put("pendingRequests", pendingCount);
        dashboard.put("activeRequests", activeCount);
        dashboard.put("completedRequests", completedRequests.size());
        dashboard.put("customers", customers);
        dashboard.put("providers", providers);
        dashboard.put("requests", allRequests);
        dashboard.put("completedJobs", completedRequests);

        return dashboard;
    }
}
