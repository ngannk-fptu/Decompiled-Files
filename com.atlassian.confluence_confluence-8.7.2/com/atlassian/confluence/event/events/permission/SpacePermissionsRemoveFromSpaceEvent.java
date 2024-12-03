/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.permission;

import com.atlassian.confluence.event.events.permission.SpacePermissionChangeEvent;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceUpdateTrigger;
import com.google.common.base.Preconditions;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

public class SpacePermissionsRemoveFromSpaceEvent
extends SpacePermissionChangeEvent {
    private static final long serialVersionUID = 1131718438518907402L;
    private final Space space;

    public SpacePermissionsRemoveFromSpaceEvent(Object src, Space space, List<SpacePermission> permissions) {
        this(src, space, permissions, SpaceUpdateTrigger.UNKNOWN);
    }

    public SpacePermissionsRemoveFromSpaceEvent(Object src, Space space, List<SpacePermission> permissions, SpaceUpdateTrigger updateTrigger) {
        super(src, permissions, updateTrigger);
        this.space = (Space)Preconditions.checkNotNull((Object)space);
    }

    public Space getSpace() {
        return this.space;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        SpacePermissionsRemoveFromSpaceEvent that = (SpacePermissionsRemoveFromSpaceEvent)o;
        return this.space.equals(that.space);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.space.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SpacePermissionsRemoveFromSpaceEvent{space=" + this.space + "} " + super.toString();
    }
}

