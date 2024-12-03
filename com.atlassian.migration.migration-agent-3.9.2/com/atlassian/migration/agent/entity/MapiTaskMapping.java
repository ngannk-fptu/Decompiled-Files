/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.Entity
 *  javax.persistence.Id
 *  javax.persistence.Table
 *  lombok.Generated
 */
package com.atlassian.migration.agent.entity;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Generated;

@Entity
@Table(name="MIG_MAPI_TASK_MAPPING")
public class MapiTaskMapping {
    @Id
    @Column(name="taskId", nullable=false)
    private String taskId;
    @Column(name="jobId", nullable=false)
    private String jobId;
    @Column(name="planId")
    private String planId;
    @Column(name="cloudId")
    private String cloudId;
    @Column(name="status")
    private String status;
    @Column(name="commandName")
    private String commandName;
    @Column(name="lastUpdate", nullable=false)
    private Instant lastUpdate;

    @Generated
    public MapiTaskMapping(String taskId, String jobId, String planId, String cloudId, String status, String commandName, Instant lastUpdate) {
        this.taskId = taskId;
        this.jobId = jobId;
        this.planId = planId;
        this.cloudId = cloudId;
        this.status = status;
        this.commandName = commandName;
        this.lastUpdate = lastUpdate;
    }

    @Generated
    public MapiTaskMapping() {
    }

    @Generated
    public String getTaskId() {
        return this.taskId;
    }

    @Generated
    public String getJobId() {
        return this.jobId;
    }

    @Generated
    public String getPlanId() {
        return this.planId;
    }

    @Generated
    public String getCloudId() {
        return this.cloudId;
    }

    @Generated
    public String getStatus() {
        return this.status;
    }

    @Generated
    public String getCommandName() {
        return this.commandName;
    }

    @Generated
    public Instant getLastUpdate() {
        return this.lastUpdate;
    }

    @Generated
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @Generated
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    @Generated
    public void setPlanId(String planId) {
        this.planId = planId;
    }

    @Generated
    public void setCloudId(String cloudId) {
        this.cloudId = cloudId;
    }

    @Generated
    public void setStatus(String status) {
        this.status = status;
    }

    @Generated
    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    @Generated
    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Generated
    public String toString() {
        return "MapiTaskMapping(taskId=" + this.getTaskId() + ", jobId=" + this.getJobId() + ", planId=" + this.getPlanId() + ", cloudId=" + this.getCloudId() + ", status=" + this.getStatus() + ", commandName=" + this.getCommandName() + ", lastUpdate=" + this.getLastUpdate() + ")";
    }
}

