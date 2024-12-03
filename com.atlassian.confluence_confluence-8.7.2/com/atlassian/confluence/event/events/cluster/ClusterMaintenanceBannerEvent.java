/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.event.events.cluster;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;
import com.atlassian.event.api.AsynchronousPreferred;
import java.util.Objects;

@AsynchronousPreferred
public class ClusterMaintenanceBannerEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = 3252560421397215210L;
    private final boolean enabled;
    private final String message;
    private final boolean wasEnabled;
    private final String previousMessage;

    public ClusterMaintenanceBannerEvent(Object src, boolean enabled, String message, boolean wasEnabled, String previousMessage) {
        super(src);
        this.enabled = enabled;
        this.message = message;
        this.wasEnabled = wasEnabled;
        this.previousMessage = previousMessage;
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
        ClusterMaintenanceBannerEvent that = (ClusterMaintenanceBannerEvent)o;
        return this.enabled == that.enabled && this.wasEnabled == that.wasEnabled && Objects.equals(this.message, that.message) && Objects.equals(this.previousMessage, that.previousMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.enabled, this.message, this.wasEnabled, this.previousMessage);
    }

    public String toString() {
        return "ClusterMaintenanceBannerEvent{enabled=" + this.enabled + ", message='" + this.message + "'}";
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean wasEnabled() {
        return this.wasEnabled;
    }

    public String getPreviousMessage() {
        return this.previousMessage;
    }
}

