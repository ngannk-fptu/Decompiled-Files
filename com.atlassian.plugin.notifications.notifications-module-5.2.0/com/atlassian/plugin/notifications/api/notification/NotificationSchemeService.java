/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Either
 */
package com.atlassian.plugin.notifications.api.notification;

import com.atlassian.fugue.Either;
import com.atlassian.plugin.notifications.api.ErrorCollection;
import com.atlassian.plugin.notifications.api.notification.NotificationRepresentation;
import com.atlassian.plugin.notifications.api.notification.NotificationSchemeRepresentation;

public interface NotificationSchemeService {
    public Either<ErrorCollection, NotificationSchemeRepresentation> getScheme(String var1);

    public Either<ErrorCollection, NotificationRepresentation> getSchemeNotification(String var1, int var2);

    public Either<ErrorCollection, NotificationRepresentation> validateAddNotification(String var1, NotificationRepresentation var2);

    public NotificationRepresentation addNotification(String var1, NotificationRepresentation var2);

    public Either<ErrorCollection, NotificationRepresentation> validateUpdateNotification(String var1, int var2, NotificationRepresentation var3);

    public NotificationRepresentation updateNotification(String var1, int var2, NotificationRepresentation var3);

    public ErrorCollection validateRemoveNotification(String var1, int var2);

    public void removeNotification(int var1);

    public Iterable<NotificationRepresentation> getNotificationsForEvent(Object var1);
}

