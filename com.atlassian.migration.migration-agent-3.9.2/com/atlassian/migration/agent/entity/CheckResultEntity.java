/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.Entity
 *  javax.persistence.EnumType
 *  javax.persistence.Enumerated
 *  javax.persistence.Table
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.CheckExecutionStatus;
import com.atlassian.migration.agent.entity.WithId;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name="MIG_CHECK_RESULT")
public class CheckResultEntity
extends WithId {
    @Column(name="executionId", nullable=false)
    private String executionId;
    @Column(name="checkType", nullable=false)
    private String checkType;
    @Column(name="created", nullable=false)
    private Instant created;
    @Column(name="lastUpdated", nullable=false)
    private Instant lastUpdated;
    @Column(name="details", nullable=true)
    private String details;
    @Enumerated(value=EnumType.STRING)
    @Column(name="status", nullable=false)
    private CheckExecutionStatus status;
    @Column(name="lastExecutionTime", nullable=true)
    private Instant lastExecutionTime;

    public CheckResultEntity() {
    }

    public CheckResultEntity(String executionId, String checkType) {
        Instant now = Instant.now();
        this.executionId = executionId;
        this.checkType = checkType;
        this.created = now;
        this.lastUpdated = now;
        this.status = CheckExecutionStatus.CREATED;
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

    public Instant getLastUpdated() {
        return this.lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getDetails() {
        return this.details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public CheckExecutionStatus getStatus() {
        return this.status;
    }

    public void setStatus(CheckExecutionStatus status) {
        this.status = status;
    }

    public Instant getLastExecutionTime() {
        return this.lastExecutionTime;
    }

    public void setLastExecutionTime(Instant lastExecutionTime) {
        this.lastExecutionTime = lastExecutionTime;
    }
}

