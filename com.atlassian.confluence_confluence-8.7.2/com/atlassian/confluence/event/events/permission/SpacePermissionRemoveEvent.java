/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.permission;

import com.atlassian.confluence.event.events.permission.SpacePermissionChangeEvent;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.spaces.Space;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public class SpacePermissionRemoveEvent
extends SpacePermissionChangeEvent {
    private static final long serialVersionUID = -2233574125515044720L;
    private final Space space;

    public SpacePermissionRemoveEvent(Object src, SpacePermission permission, Space space) {
        super(src, permission);
        this.space = space;
    }

    public Space getSpace() {
        return this.space;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        SpacePermissionRemoveEvent that = (SpacePermissionRemoveEvent)o;
        return Objects.equals(this.space, that.space);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.space);
    }
}

