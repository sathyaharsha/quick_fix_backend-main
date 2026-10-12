
package com.quickfix.backend.repository;

import com.quickfix.backend.entity.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceCategoryRepository
        extends JpaRepository<ServiceCategory, Long> {
    boolean existsByNameIgnoreCase(String name);
}
