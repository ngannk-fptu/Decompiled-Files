/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.notifications.api.notification;

import com.atlassian.plugin.notifications.api.notification.NotificationRepresentation;
import com.atlassian.plugin.notifications.api.notification.NotificationSchemeRepresentation;

public interface NotificationSchemeStore {
    public Iterable<NotificationRepresentation> getNotificationsForEvent(String var1);

    public NotificationSchemeRepresentation getScheme();

    public NotificationRepresentation addNotification(NotificationRepresentation var1);

    public NotificationRepresentation updateNotification(NotificationRepresentation var1);

    public void removeNotification(int var1);
}

