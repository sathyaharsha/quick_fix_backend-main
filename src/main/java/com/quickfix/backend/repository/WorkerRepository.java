
package com.quickfix.backend.repository;

import com.quickfix.backend.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkerRepository extends JpaRepository<Worker, Long> {

    Optional<Worker> findByUserId(Long userId);
}
