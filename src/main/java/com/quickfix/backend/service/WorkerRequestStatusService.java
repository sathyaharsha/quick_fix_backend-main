
package com.quickfix.backend.service;

import com.quickfix.backend.dto.ServiceRequestResponse;
import com.quickfix.backend.entity.ServiceRequest;
import com.quickfix.backend.entity.ServiceRequestStatus;
import com.quickfix.backend.entity.Worker;
import com.quickfix.backend.repository.ServiceRequestRepository;
import com.quickfix.backend.repository.WorkerRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WorkerRequestStatusService {

    private final ServiceRequestRepository requestRepository;
    private final WorkerRepository workerRepository;

    public WorkerRequestStatusService(
            ServiceRequestRepository requestRepository,
            WorkerRepository workerRepository) {
        this.requestRepository = requestRepository;
        this.workerRepository = workerRepository;
    }

    // Find a worker by ID or return a 404 error.
    private Worker getWorker(Long workerId) {
        return workerRepository.findById(workerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Worker not found"));
    }

    @Transactional
    public ServiceRequestResponse updateStatus(
            Long workerId,
            Long requestId,
            String newStatus) {

        Worker worker = getWorker(workerId);

        ServiceRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Service request not found"));

        // Ensure that the request belongs to this worker.
        if (request.getWorker() == null
                || !request.getWorker().getId().equals(worker.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "This request does not belong to this worker");
        }

        if (newStatus == null || newStatus.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status is required");
        }

        ServiceRequestStatus requestedStatus;

        try {
            requestedStatus = ServiceRequestStatus.valueOf(
                    newStatus.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid status. Use IN_PROGRESS or COMPLETED");
        }

        if (requestedStatus == ServiceRequestStatus.IN_PROGRESS) {

            if (request.getStatus() != ServiceRequestStatus.ACCEPTED) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Only accepted requests can be started");
            }

        } else if (requestedStatus == ServiceRequestStatus.COMPLETED) {

            if (request.getStatus() != ServiceRequestStatus.IN_PROGRESS) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Only in-progress requests can be completed");
            }

        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Workers can only set IN_PROGRESS or COMPLETED");
        }

        request.setStatus(requestedStatus);

        ServiceRequest savedRequest = requestRepository.save(request);

        // Make the worker available again when the job is completed.
        if (requestedStatus == ServiceRequestStatus.COMPLETED) {
            worker.setAvailable(true);
            workerRepository.save(worker);
        }

        return ServiceRequestResponse.from(savedRequest);
    }
}
