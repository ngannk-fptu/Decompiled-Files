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
import com.atlassian.migration.agent.service.event.StepAllocation;
import java.util.List;
import lombok.Generated;

public class ExecuteStepsEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = 5648417917455407230L;
    private final List<StepAllocation> stepAllocations;

    public ExecuteStepsEvent(Object source, List<StepAllocation> stepAllocations) {
        super(source);
        this.stepAllocations = stepAllocations;
    }

    @Generated
    public List<StepAllocation> getStepAllocations() {
        return this.stepAllocations;
    }

    @Generated
    public String toString() {
        return "ExecuteStepsEvent(stepAllocations=" + this.getStepAllocations() + ")";
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ExecuteStepsEvent)) {
            return false;
        }
        ExecuteStepsEvent other = (ExecuteStepsEvent)((Object)o);
        if (!other.canEqual((Object)this)) {
            return false;
        }
        List<StepAllocation> this$stepAllocations = this.getStepAllocations();
        List<StepAllocation> other$stepAllocations = other.getStepAllocations();
        return !(this$stepAllocations == null ? other$stepAllocations != null : !((Object)this$stepAllocations).equals(other$stepAllocations));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof ExecuteStepsEvent;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        List<StepAllocation> $stepAllocations = this.getStepAllocations();
        result = result * 59 + ($stepAllocations == null ? 43 : ((Object)$stepAllocations).hashCode());
        return result;
    }
}

