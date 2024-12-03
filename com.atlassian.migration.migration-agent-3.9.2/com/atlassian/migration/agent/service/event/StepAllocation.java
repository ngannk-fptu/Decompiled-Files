/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.event;

import java.io.Serializable;
import lombok.Generated;

public class StepAllocation
implements Serializable {
    private static final long serialVersionUID = -7092383324251211220L;
    private final String stepId;
    private final String taskId;
    private final String nodeId;
    private final String nodeExecutionId;

    @Generated
    public StepAllocation(String stepId, String taskId, String nodeId, String nodeExecutionId) {
        this.stepId = stepId;
        this.taskId = taskId;
        this.nodeId = nodeId;
        this.nodeExecutionId = nodeExecutionId;
    }

    @Generated
    public String getStepId() {
        return this.stepId;
    }

    @Generated
    public String getTaskId() {
        return this.taskId;
    }

    @Generated
    public String getNodeId() {
        return this.nodeId;
    }

    @Generated
    public String getNodeExecutionId() {
        return this.nodeExecutionId;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof StepAllocation)) {
            return false;
        }
        StepAllocation other = (StepAllocation)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$stepId = this.getStepId();
        String other$stepId = other.getStepId();
        if (this$stepId == null ? other$stepId != null : !this$stepId.equals(other$stepId)) {
            return false;
        }
        String this$taskId = this.getTaskId();
        String other$taskId = other.getTaskId();
        if (this$taskId == null ? other$taskId != null : !this$taskId.equals(other$taskId)) {
            return false;
        }
        String this$nodeId = this.getNodeId();
        String other$nodeId = other.getNodeId();
        if (this$nodeId == null ? other$nodeId != null : !this$nodeId.equals(other$nodeId)) {
            return false;
        }
        String this$nodeExecutionId = this.getNodeExecutionId();
        String other$nodeExecutionId = other.getNodeExecutionId();
        return !(this$nodeExecutionId == null ? other$nodeExecutionId != null : !this$nodeExecutionId.equals(other$nodeExecutionId));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof StepAllocation;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $stepId = this.getStepId();
        result = result * 59 + ($stepId == null ? 43 : $stepId.hashCode());
        String $taskId = this.getTaskId();
        result = result * 59 + ($taskId == null ? 43 : $taskId.hashCode());
        String $nodeId = this.getNodeId();
        result = result * 59 + ($nodeId == null ? 43 : $nodeId.hashCode());
        String $nodeExecutionId = this.getNodeExecutionId();
        result = result * 59 + ($nodeExecutionId == null ? 43 : $nodeExecutionId.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "StepAllocation(stepId=" + this.getStepId() + ", taskId=" + this.getTaskId() + ", nodeId=" + this.getNodeId() + ", nodeExecutionId=" + this.getNodeExecutionId() + ")";
    }
}

