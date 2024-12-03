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
import com.atlassian.confluence.user.ConfluenceUser;
import com.google.common.base.Preconditions;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

public class SpacePermissionsRemoveForUserEvent
extends SpacePermissionChangeEvent {
    private static final long serialVersionUID = -4110521038963938238L;
    private final ConfluenceUser user;

    public SpacePermissionsRemoveForUserEvent(Object src, ConfluenceUser user, List<SpacePermission> permissions) {
        super(src, permissions);
        this.user = (ConfluenceUser)Preconditions.checkNotNull((Object)user);
    }

    public ConfluenceUser getUser() {
        return this.user;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        SpacePermissionsRemoveForUserEvent that = (SpacePermissionsRemoveForUserEvent)o;
        return this.user.equals(that.user);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.user.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SpacePermissionsRemoveForUserEvent{user=" + this.user + "} " + super.toString();
    }
}

