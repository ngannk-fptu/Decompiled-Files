/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.permission;

import com.atlassian.confluence.event.events.permission.GlobalPermissionChangeEvent;
import com.atlassian.confluence.security.SpacePermission;

public class GlobalPermissionSaveEvent
extends GlobalPermissionChangeEvent {
    private static final long serialVersionUID = 8252507237168997668L;

    public GlobalPermissionSaveEvent(Object src, SpacePermission permission) {
        super(src, permission);
    }
}

