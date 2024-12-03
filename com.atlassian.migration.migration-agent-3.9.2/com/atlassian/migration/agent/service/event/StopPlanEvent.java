/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 *  com.atlassian.confluence.event.events.cluster.ClusterEvent
 *  javax.annotation.Nullable
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.event;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;
import javax.annotation.Nullable;
import lombok.Generated;

public class StopPlanEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = -5638867452596715572L;
    private final String planId;

    public StopPlanEvent(Object source, @Nullable String planId) {
        super(source);
        this.planId = planId;
    }

    public String getPlanId() {
        return this.planId;
    }

    @Generated
    public String toString() {
        return "StopPlanEvent(planId=" + this.getPlanId() + ")";
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof StopPlanEvent)) {
            return false;
        }
        StopPlanEvent other = (StopPlanEvent)((Object)o);
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$planId = this.getPlanId();
        String other$planId = other.getPlanId();
        return !(this$planId == null ? other$planId != null : !this$planId.equals(other$planId));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof StopPlanEvent;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $planId = this.getPlanId();
        result = result * 59 + ($planId == null ? 43 : $planId.hashCode());
        return result;
    }
}

