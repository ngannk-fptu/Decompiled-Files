/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.upm.notification.rest.representations;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.notification.Notification;
import com.atlassian.upm.notification.NotificationCollection;
import com.atlassian.upm.notification.rest.representations.NotificationGroupCollectionRepresentation;
import com.atlassian.upm.notification.rest.representations.NotificationGroupRepresentation;
import com.atlassian.upm.notification.rest.representations.NotificationRepresentation;

public interface NotificationRepresentationFactory {
    public NotificationGroupCollectionRepresentation getNotificationGroupCollection(Iterable<NotificationCollection> var1, Option<UserKey> var2);

    public NotificationGroupRepresentation getNotificationGroup(NotificationCollection var1, Option<UserKey> var2);

    public NotificationRepresentation getNotification(Notification var1, Option<UserKey> var2);
}

