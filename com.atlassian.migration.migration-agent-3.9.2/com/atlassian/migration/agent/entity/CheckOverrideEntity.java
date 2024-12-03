/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.Entity
 *  javax.persistence.Table
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.WithId;
import java.time.Instant;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="MIG_CHECK_OVERRIDE")
public class CheckOverrideEntity
extends WithId {
    @Column(name="executionId", nullable=false)
    private String executionId;
    @Column(name="checkType", nullable=false)
    private String checkType;
    @Column(name="created", nullable=false)
    private Instant created;

    public CheckOverrideEntity() {
    }

    public CheckOverrideEntity(String executionId, String checkType) {
        this.created = Instant.now();
        this.executionId = executionId;
        this.checkType = checkType;
    }

    public String getExecutionId() {
        return this.executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public String getCheckType() {
        return this.checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public Instant getCreated() {
        return this.created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CheckOverrideEntity other = (CheckOverrideEntity)o;
        return Objects.equals(this.checkType, other.checkType) && Objects.equals(this.executionId, other.executionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.checkType, this.executionId);
    }
}

