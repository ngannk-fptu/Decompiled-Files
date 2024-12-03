/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.permission;

import com.atlassian.confluence.event.events.permission.SpacePermissionChangeEvent;
import com.atlassian.confluence.security.SpacePermission;

public class SpacePermissionSaveEvent
extends SpacePermissionChangeEvent {
    private static final long serialVersionUID = 280433633323231148L;

    public SpacePermissionSaveEvent(Object src, SpacePermission permission) {
        super(src, permission);
    }
}

