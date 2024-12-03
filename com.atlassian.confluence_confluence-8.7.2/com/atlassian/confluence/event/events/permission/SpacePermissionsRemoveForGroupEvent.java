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
import com.google.common.base.Preconditions;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

public class SpacePermissionsRemoveForGroupEvent
extends SpacePermissionChangeEvent {
    private static final long serialVersionUID = -1648140770664867248L;
    private final String groupName;

    public SpacePermissionsRemoveForGroupEvent(Object src, String groupName, List<SpacePermission> permissions) {
        super(src, permissions);
        this.groupName = (String)Preconditions.checkNotNull((Object)groupName);
    }

    public String getGroupName() {
        return this.groupName;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        SpacePermissionsRemoveForGroupEvent that = (SpacePermissionsRemoveForGroupEvent)o;
        return this.groupName.equals(that.groupName);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.groupName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SpacePermissionsRemoveForGroupEvent{groupName='" + this.groupName + "'} " + super.toString();
    }
}

