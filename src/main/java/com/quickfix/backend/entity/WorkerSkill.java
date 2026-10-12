
package com.quickfix.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "worker_skills")
@IdClass(WorkerSkillId.class)
public class WorkerSkill {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id")
    private Worker worker;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ServiceCategory category;

    public WorkerSkill() {}

    public WorkerSkill(Worker worker, ServiceCategory category) {
        this.worker = worker;
        this.category = category;
    }

    public Worker getWorker() { return worker; }
    public ServiceCategory getCategory() { return category; }
}
