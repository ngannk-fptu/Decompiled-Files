/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.Event
 */
package com.atlassian.confluence.event.events.cluster;

import com.atlassian.confluence.event.events.cluster.ClusterEvent;
import com.atlassian.confluence.security.seraph.ConfluenceUserPrincipal;
import com.atlassian.event.Event;
import java.util.Objects;

public final class ExpireUserSessionsClusterNotificationEvent
extends Event
implements ClusterEvent {
    private static final long serialVersionUID = -6649534183994284961L;
    private final ConfluenceUserPrincipal principal;

    public ExpireUserSessionsClusterNotificationEvent(Object source, ConfluenceUserPrincipal principal) {
        super(source);
        this.principal = principal;
    }

    public ConfluenceUserPrincipal getPrincipal() {
        return this.principal;
    }

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
        ExpireUserSessionsClusterNotificationEvent event = (ExpireUserSessionsClusterNotificationEvent)o;
        return this.principal.equals(event.principal);
    }

    public int hashCode() {
        return Objects.hash(super.hashCode(), this.principal);
    }
}

