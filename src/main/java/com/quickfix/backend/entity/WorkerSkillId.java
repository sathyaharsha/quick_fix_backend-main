
package com.quickfix.backend.entity;

import java.io.Serializable;
import java.util.Objects;

public class WorkerSkillId implements Serializable {

    private Long worker;
    private Long category;

    public WorkerSkillId() {}

    public WorkerSkillId(Long worker, Long category) {
        this.worker = worker;
        this.category = category;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof WorkerSkillId that)) return false;
        return Objects.equals(worker, that.worker)
            && Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(worker, category);
    }
}
