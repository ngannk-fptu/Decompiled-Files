/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.retention.RetentionPolicy
 */
package com.atlassian.confluence.event.events.retention;

import com.atlassian.confluence.api.model.retention.RetentionPolicy;
import com.atlassian.confluence.event.events.retention.RetentionPolicyChangedEvent;
import com.atlassian.confluence.spaces.Space;
import java.util.Objects;

public abstract class SpaceRetentionPolicyEvent
extends RetentionPolicyChangedEvent {
    private final Space space;

    public SpaceRetentionPolicyEvent(RetentionPolicy oldPolicy, RetentionPolicy newPolicy, Space space) {
        super(oldPolicy, newPolicy);
        this.space = space;
    }

    public Space getSpace() {
        return this.space;
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
        SpaceRetentionPolicyEvent that = (SpaceRetentionPolicyEvent)o;
        return this.space.equals(that.space);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.space);
    }
}

