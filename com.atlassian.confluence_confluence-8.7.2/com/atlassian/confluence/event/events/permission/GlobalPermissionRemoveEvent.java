/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.permission;

import com.atlassian.confluence.event.events.permission.GlobalPermissionChangeEvent;
import com.atlassian.confluence.security.SpacePermission;

public class GlobalPermissionRemoveEvent
extends GlobalPermissionChangeEvent {
    private static final long serialVersionUID = -5542330270258870544L;

    public GlobalPermissionRemoveEvent(Object src, SpacePermission permission) {
        super(src, permission);
    }
}

