/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.permission;

import com.atlassian.confluence.event.events.permission.GlobalPermissionEvent;
import com.atlassian.confluence.security.SpacePermission;
import com.google.common.base.Preconditions;
import org.checkerframework.checker.nullness.qual.Nullable;

public class GlobalPermissionChangeEvent
extends GlobalPermissionEvent {
    private static final long serialVersionUID = 3253221895363329770L;
    protected final SpacePermission permission;

    public GlobalPermissionChangeEvent(Object src, SpacePermission permission) {
        super(src);
        this.permission = (SpacePermission)Preconditions.checkNotNull((Object)permission);
    }

    public SpacePermission getPermission() {
        return this.permission;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        GlobalPermissionChangeEvent that = (GlobalPermissionChangeEvent)o;
        return this.permission.equals(that.permission);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.permission.hashCode();
        return result;
    }

    public String toString() {
        return this.getClass().getSimpleName() + "{permission=" + this.permission + "} " + super.toString();
    }
}

