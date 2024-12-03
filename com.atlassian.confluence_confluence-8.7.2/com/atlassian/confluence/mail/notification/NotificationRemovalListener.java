/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.mail.notification;

import com.atlassian.confluence.event.events.permission.SpacePermissionRemoveEvent;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.event.api.EventListener;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Qualifier;

public class NotificationRemovalListener {
    private final NotificationManager notificationManager;
    private final PermissionManager permissionManager;

    public NotificationRemovalListener(@Qualifier(value="notificationManager") NotificationManager notificationManager, @Qualifier(value="permissionManager") PermissionManager permissionManager) {
        this.notificationManager = notificationManager;
        this.permissionManager = permissionManager;
    }

    @EventListener
    public void onSpacePermissionRemovedEvent(SpacePermissionRemoveEvent e) {
        Preconditions.checkNotNull((Object)e.getSpace());
        for (SpacePermission permission : e.getPermissions()) {
            if (!permission.getType().equals("VIEWSPACE")) continue;
            this.notificationManager.getNotificationsBySpaceAndType(e.getSpace(), null).stream().filter(o -> !this.permissionManager.hasPermission((User)o.getReceiver(), Permission.VIEW, e.getSpace())).forEach(o -> this.notificationManager.removeSpaceNotification(o.getReceiver(), e.getSpace()));
        }
    }
}

