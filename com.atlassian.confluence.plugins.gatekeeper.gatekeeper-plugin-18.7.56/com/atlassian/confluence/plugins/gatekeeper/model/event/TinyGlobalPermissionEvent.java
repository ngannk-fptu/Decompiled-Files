/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.SpacePermission
 */
package com.atlassian.confluence.plugins.gatekeeper.model.event;

import com.atlassian.confluence.plugins.gatekeeper.model.event.EventType;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyEvent;
import com.atlassian.confluence.security.SpacePermission;

public class TinyGlobalPermissionEvent
extends TinyEvent {
    private static final long serialVersionUID = -8152649511718869189L;
    private String permission;

    protected TinyGlobalPermissionEvent(EventType eventType) {
        super(eventType);
    }

    public static TinyEvent added(SpacePermission permission) {
        TinyGlobalPermissionEvent e = new TinyGlobalPermissionEvent(EventType.GLOBAL_PERMISSION_ADDED);
        e.permission = e.convertGlobalPermission(permission);
        return e;
    }

    public static TinyEvent deleted(SpacePermission permission) {
        TinyGlobalPermissionEvent e = new TinyGlobalPermissionEvent(EventType.GLOBAL_PERMISSION_DELETED);
        e.permission = e.convertGlobalPermission(permission);
        return e;
    }

    public String getPermission() {
        return this.permission;
    }

    private String convertGlobalPermission(SpacePermission globalPermission) {
        if (globalPermission.isGroupPermission()) {
            return "g:" + globalPermission.getGroup();
        }
        if (globalPermission.isUserPermission()) {
            return "u:" + globalPermission.getUserSubject().getName().toLowerCase();
        }
        if (globalPermission.isAnonymousPermission()) {
            return "a";
        }
        return null;
    }

    public String toString() {
        return this.eventType + "{permission='" + this.permission + "'}";
    }
}

