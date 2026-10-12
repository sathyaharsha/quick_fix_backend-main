
package com.quickfix.backend.repository;

import com.quickfix.backend.entity.ServiceRequest;
import com.quickfix.backend.entity.ServiceRequestStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServiceRequestRepository
        extends JpaRepository<ServiceRequest, Long> {

    List<ServiceRequest> findByCustomer_IdOrderByCreatedAtDesc(
            Long customerId);

    List<ServiceRequest> findByStatusOrderByCreatedAtDesc(
            ServiceRequestStatus status);

    List<ServiceRequest> findByWorker_IdOrderByCreatedAtDesc(
            Long workerId);

    @Query("""
        SELECT DISTINCT r
        FROM ServiceRequest r
        JOIN WorkerSkill ws ON ws.category.id = r.category.id
        WHERE ws.worker.id = :workerId
          AND r.status = :status
          AND r.worker IS NULL
          AND r.pincode = :pincode
        ORDER BY r.createdAt DESC
        """)
    List<ServiceRequest> findMatchingRequests(
            @Param("workerId") Long workerId,
            @Param("status") ServiceRequestStatus status,
            @Param("pincode") String pincode);

    @Modifying
    @Query(value = """
        UPDATE service_requests
        SET worker_id = :workerId,
            status = 'ACCEPTED'
        WHERE id = :requestId
          AND status = 'PENDING'
          AND worker_id IS NULL
        """, nativeQuery = true)
    int acceptIfPending(
            @Param("requestId") Long requestId,
            @Param("workerId") Long workerId);
}
