/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.monitoring;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;
import java.util.Objects;

public class JmxConfigChangedEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = 1L;
    private final boolean isJmxEnabled;

    public JmxConfigChangedEvent(Object src, boolean isJmxEnabled) {
        super(src);
        this.isJmxEnabled = isJmxEnabled;
    }

    public boolean getJmxEnabled() {
        return this.isJmxEnabled;
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
        JmxConfigChangedEvent that = (JmxConfigChangedEvent)o;
        return this.isJmxEnabled == that.isJmxEnabled;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.isJmxEnabled);
    }
}

