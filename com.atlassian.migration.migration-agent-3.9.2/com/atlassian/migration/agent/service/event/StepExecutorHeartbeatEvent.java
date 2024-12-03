/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 *  com.atlassian.confluence.event.events.cluster.ClusterEvent
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.event;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;
import java.util.Set;
import lombok.Generated;

public class StepExecutorHeartbeatEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = -7841348184612296692L;
    private final String nodeId;
    private final long heartbeatTime;
    private final Set<String> stepExecutionIds;

    public StepExecutorHeartbeatEvent(Object src, String nodeId, long heartbeatTime, Set<String> stepExecutionIds) {
        super(src);
        this.nodeId = nodeId;
        this.heartbeatTime = heartbeatTime;
        this.stepExecutionIds = stepExecutionIds;
    }

    public boolean containsExecution(String nodeExecutionId) {
        return this.stepExecutionIds.contains(nodeExecutionId);
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof StepExecutorHeartbeatEvent)) {
            return false;
        }
        StepExecutorHeartbeatEvent other = (StepExecutorHeartbeatEvent)((Object)o);
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        String this$nodeId = this.getNodeId();
        String other$nodeId = other.getNodeId();
        if (this$nodeId == null ? other$nodeId != null : !this$nodeId.equals(other$nodeId)) {
            return false;
        }
        if (this.getHeartbeatTime() != other.getHeartbeatTime()) {
            return false;
        }
        Set<String> this$stepExecutionIds = this.getStepExecutionIds();
        Set<String> other$stepExecutionIds = other.getStepExecutionIds();
        return !(this$stepExecutionIds == null ? other$stepExecutionIds != null : !((Object)this$stepExecutionIds).equals(other$stepExecutionIds));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof StepExecutorHeartbeatEvent;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        String $nodeId = this.getNodeId();
        result = result * 59 + ($nodeId == null ? 43 : $nodeId.hashCode());
        long $heartbeatTime = this.getHeartbeatTime();
        result = result * 59 + (int)($heartbeatTime >>> 32 ^ $heartbeatTime);
        Set<String> $stepExecutionIds = this.getStepExecutionIds();
        result = result * 59 + ($stepExecutionIds == null ? 43 : ((Object)$stepExecutionIds).hashCode());
        return result;
    }

    @Generated
    public String getNodeId() {
        return this.nodeId;
    }

    @Generated
    public long getHeartbeatTime() {
        return this.heartbeatTime;
    }

    @Generated
    public Set<String> getStepExecutionIds() {
        return this.stepExecutionIds;
    }

    @Generated
    public String toString() {
        return "StepExecutorHeartbeatEvent(nodeId=" + this.getNodeId() + ", heartbeatTime=" + this.getHeartbeatTime() + ", stepExecutionIds=" + this.getStepExecutionIds() + ")";
    }
}

