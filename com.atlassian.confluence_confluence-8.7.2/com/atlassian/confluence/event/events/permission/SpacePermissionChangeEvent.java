/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.event.events.permission;

import com.atlassian.confluence.event.events.permission.SpacePermissionEvent;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.spaces.SpaceUpdateTrigger;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.Objects;

public class SpacePermissionChangeEvent
extends SpacePermissionEvent {
    private static final long serialVersionUID = -3966597436074388774L;
    protected final Iterable<SpacePermission> permissions;
    protected final SpaceUpdateTrigger updateTrigger;

    public SpacePermissionChangeEvent(Object src, SpacePermission permission) {
        this(src, Collections.singleton(permission));
    }

    public SpacePermissionChangeEvent(Object src, Iterable<SpacePermission> permissions) {
        this(src, permissions, SpaceUpdateTrigger.UNKNOWN);
    }

    public SpacePermissionChangeEvent(Object src, Iterable<SpacePermission> permissions, SpaceUpdateTrigger updateTrigger) {
        super(src);
        this.permissions = ImmutableList.copyOf(permissions);
        this.updateTrigger = updateTrigger;
    }

    public Iterable<SpacePermission> getPermissions() {
        return this.permissions;
    }

    public SpaceUpdateTrigger getUpdateTrigger() {
        return this.updateTrigger;
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
        SpacePermissionChangeEvent that = (SpacePermissionChangeEvent)o;
        return Objects.equals(this.permissions, that.permissions) && this.updateTrigger == that.updateTrigger;
    }

    @Override
    public int hashCode() {
        return Objects.hash(new Object[]{super.hashCode(), this.permissions, this.updateTrigger});
    }

    public String toString() {
        return "SpacePermissionChangeEvent{permissions=" + this.permissions + ", updateTrigger=" + this.updateTrigger + "}";
    }
}

