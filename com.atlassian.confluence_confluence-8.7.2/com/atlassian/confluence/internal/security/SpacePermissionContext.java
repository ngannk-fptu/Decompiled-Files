/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.internal.security;

import com.atlassian.confluence.spaces.SpaceUpdateTrigger;
import com.google.common.base.Preconditions;
import java.util.Objects;

public class SpacePermissionContext {
    private final SpaceUpdateTrigger updateTrigger;
    private final boolean sendEvents;

    private SpacePermissionContext(Builder builder) {
        this.updateTrigger = (SpaceUpdateTrigger)((Object)Preconditions.checkNotNull((Object)((Object)builder.updateTrigger)));
        this.sendEvents = builder.sendEvents;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(SpacePermissionContext context) {
        return SpacePermissionContext.builder().updateTrigger(context.getUpdateTrigger()).sendEvents(context.shouldSendEvents());
    }

    public static SpacePermissionContext createDefault() {
        return SpacePermissionContext.builder().updateTrigger(SpaceUpdateTrigger.UNKNOWN).build();
    }

    public SpaceUpdateTrigger getUpdateTrigger() {
        return this.updateTrigger;
    }

    public boolean shouldSendEvents() {
        return this.sendEvents;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SpacePermissionContext context = (SpacePermissionContext)o;
        if (this.sendEvents != context.sendEvents) {
            return false;
        }
        return this.updateTrigger == context.updateTrigger;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.updateTrigger, this.sendEvents});
    }

    public String toString() {
        return "SpacePermissionContext{updateTrigger=" + this.updateTrigger + ", sendEvents=" + this.sendEvents + "}";
    }

    public static class Builder {
        private SpaceUpdateTrigger updateTrigger;
        private boolean sendEvents = true;

        public Builder updateTrigger(SpaceUpdateTrigger updateTrigger) {
            this.updateTrigger = updateTrigger;
            return this;
        }

        public Builder sendEvents(boolean sendEvents) {
            this.sendEvents = sendEvents;
            return this;
        }

        public SpacePermissionContext build() {
            return new SpacePermissionContext(this);
        }
    }
}

