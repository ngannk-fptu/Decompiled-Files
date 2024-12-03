/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.user.crowd;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;
import java.util.Objects;

public class ConfluenceLdapDirectoryClearingClusterEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = -7564564375475672345L;
    private final long directoryId;

    public ConfluenceLdapDirectoryClearingClusterEvent(Object src, long directoryId) {
        super(src);
        this.directoryId = directoryId;
    }

    public long getDirectoryId() {
        return this.directoryId;
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
        ConfluenceLdapDirectoryClearingClusterEvent event = (ConfluenceLdapDirectoryClearingClusterEvent)o;
        return this.directoryId == event.directoryId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.directoryId);
    }
}

