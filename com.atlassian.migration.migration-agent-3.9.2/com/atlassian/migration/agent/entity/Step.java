/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.Embedded
 *  javax.persistence.Entity
 *  javax.persistence.JoinColumn
 *  javax.persistence.ManyToOne
 *  javax.persistence.Table
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.Plan;
import com.atlassian.migration.agent.entity.Progress;
import com.atlassian.migration.agent.entity.Task;
import com.atlassian.migration.agent.entity.WithId;
import com.atlassian.migration.agent.service.impl.StepSubType;
import com.atlassian.migration.agent.service.impl.StepType;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Table(name="MIG_STEP")
@Entity
public class Step
extends WithId {
    @ManyToOne
    @JoinColumn(name="taskId", nullable=false)
    private Task task;
    @ManyToOne
    @JoinColumn(name="planId", nullable=false)
    private Plan plan;
    @Column(name="stepType", nullable=false)
    private String type;
    @Column(name="stepSubType", nullable=true)
    private String subType;
    @Column(name="stepIndex", nullable=false)
    private int index;
    @Column(name="stepConfig", length=4000)
    private String config;
    @Column(name="node_id")
    private String nodeId;
    @Column(name="node_heartbeat")
    private Instant nodeHeartbeat;
    @Column(name="node_execution_id")
    private String nodeExecutionId;
    @Column(name="execution_state")
    private String executionState;
    @Column(name="transferId")
    private String transferId;
    @Embedded
    private Progress progress = Progress.created();

    public Task getTask() {
        return this.task;
    }

    public void setTask(Task task) {
        this.task = task;
        this.plan = task.getPlan();
    }

    public String getType() {
        return this.type;
    }

    public String getSubType() {
        return this.subType;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getConfig() {
        return this.config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public Progress getProgress() {
        return this.progress;
    }

    public void setProgress(Progress progress) {
        this.progress = progress;
    }

    public String getDetailedStatus() {
        if (this.getSubType() == null) {
            return StepType.valueOf(this.getType()).getDetailedStatus();
        }
        return StepSubType.valueOf(this.getSubType()).getDetailedStatus();
    }

    public String getDisplayName() {
        if (this.getSubType() == null) {
            return StepType.valueOf(this.getType()).getDisplayName();
        }
        return StepSubType.valueOf(this.getSubType()).getDisplayName();
    }

    public Plan getPlan() {
        return this.plan;
    }

    public String getNodeId() {
        return this.nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Instant getNodeHeartbeat() {
        return this.nodeHeartbeat;
    }

    public void setNodeHeartbeat(Instant nodeHeartbeat) {
        this.nodeHeartbeat = nodeHeartbeat;
    }

    public String getNodeExecutionId() {
        return this.nodeExecutionId;
    }

    public void setNodeExecutionId(String nodeExecutionId) {
        this.nodeExecutionId = nodeExecutionId;
    }

    public String getExecutionState() {
        return this.executionState;
    }

    public void setExecutionState(String executionState) {
        this.executionState = executionState;
    }

    public String getTransferId() {
        return this.transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }
}

