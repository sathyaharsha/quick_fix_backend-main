
package com.quickfix.backend.service;

import com.quickfix.backend.dto.ServiceRequestResponse;
import com.quickfix.backend.entity.ServiceRequestStatus;
import com.quickfix.backend.entity.Worker;
import com.quickfix.backend.repository.ServiceRequestRepository;
import com.quickfix.backend.repository.WorkerRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class WorkerDashboardService {

    private final WorkerRepository workerRepository;
    private final ServiceRequestRepository serviceRequestRepository;

    public WorkerDashboardService(
            WorkerRepository workerRepository,
            ServiceRequestRepository serviceRequestRepository) {
        this.workerRepository = workerRepository;
        this.serviceRequestRepository = serviceRequestRepository;
    }

    private Worker getWorker(Long workerId) {
        return workerRepository.findById(workerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Worker not found"));
    }

    @Transactional
    public void setAvailability(Long workerId, boolean online) {
        Worker worker = getWorker(workerId);

        if (online && (worker.getWorkPincode() == null
                || worker.getWorkPincode().isBlank())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Save your work pincode before going online");
        }

        worker.setOnline(online);
        worker.setAvailable(online);
        workerRepository.save(worker);
    }

    @Transactional
    public Map<String, String> saveWorkArea(
            Long workerId, String city, String pincode) {

        Worker worker = getWorker(workerId);

        if (city == null || city.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "City is required");
        }

        if (pincode == null || !pincode.trim().matches("\\d{6}")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Enter a valid six-digit pincode");
        }

        worker.setWorkCity(city.trim());
        worker.setWorkPincode(pincode.trim());
        workerRepository.save(worker);

        return Map.of("message", "Work area saved successfully");
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getWorkArea(Long workerId) {
        Worker worker = getWorker(workerId);

        return Map.of(
                "city", worker.getWorkCity() == null
                        ? "" : worker.getWorkCity(),
                "pincode", worker.getWorkPincode() == null
                        ? "" : worker.getWorkPincode(),
                "online", worker.isOnline(),
                "available", worker.isAvailable());
    }

    @Transactional(readOnly = true)
    public List<ServiceRequestResponse> getAvailableRequests(
            Long workerId) {

        Worker worker = getWorker(workerId);

        if (!worker.isOnline() || !worker.isAvailable()
                || worker.getWorkPincode() == null
                || worker.getWorkPincode().isBlank()) {
            return List.of();
        }

        return serviceRequestRepository.findMatchingRequests(
                        workerId,
                        ServiceRequestStatus.PENDING,
                        worker.getWorkPincode())
                .stream()
                .map(ServiceRequestResponse::from)
                .toList();
    }

    @Transactional
    public void acceptRequest(Long workerId, Long requestId) {
        Worker worker = getWorker(workerId);

        if (!worker.isOnline() || !worker.isAvailable()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Go online and become available before accepting a job");
        }

        int updated = serviceRequestRepository.acceptIfPending(
                requestId, workerId);

        if (updated == 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Request not found or already accepted");
        }

        worker.setAvailable(false);
        workerRepository.save(worker);
    }

    @Transactional(readOnly = true)
    public List<ServiceRequestResponse> getMyRequests(Long workerId) {
        getWorker(workerId);

        return serviceRequestRepository
                .findByWorker_IdOrderByCreatedAtDesc(workerId)
                .stream()
                .map(ServiceRequestResponse::from)
                .toList();
    }
}
