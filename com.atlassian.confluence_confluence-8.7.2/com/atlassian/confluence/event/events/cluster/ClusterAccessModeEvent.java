/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.accessmode.AccessMode
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.event.events.cluster;

import com.atlassian.confluence.api.model.accessmode.AccessMode;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
public class ClusterAccessModeEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = -8278124740627385776L;
    private final AccessMode accessMode;

    public ClusterAccessModeEvent(Object src, AccessMode accessMode) {
        super(src);
        this.accessMode = accessMode;
    }

    public AccessMode getAccessMode() {
        return this.accessMode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ClusterAccessModeEvent that = (ClusterAccessModeEvent)o;
        return this.accessMode == that.accessMode;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.accessMode.hashCode();
        return result;
    }

    public String toString() {
        return "ClusterAccessModeEvent{accessMode=" + this.accessMode.name() + "}";
    }
}

