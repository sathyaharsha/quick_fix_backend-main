
package com.quickfix.backend.repository;

import com.quickfix.backend.entity.WorkerSkill;
import com.quickfix.backend.entity.WorkerSkillId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkerSkillRepository
        extends JpaRepository<WorkerSkill, WorkerSkillId> {
}
