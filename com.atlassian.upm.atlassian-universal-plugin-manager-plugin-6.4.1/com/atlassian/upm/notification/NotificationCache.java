/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.upm.notification;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.notification.Notification;
import com.atlassian.upm.notification.NotificationCollection;
import com.atlassian.upm.notification.NotificationType;
import java.util.Collection;
import java.util.List;

public interface NotificationCache {
    public List<NotificationCollection> getNotifications();

    public List<NotificationCollection> getNotifications(Option<UserKey> var1, boolean var2);

    public NotificationCollection getNotifications(NotificationType var1, Option<UserKey> var2, boolean var3);

    public Option<Notification> getNotification(NotificationType var1, Option<UserKey> var2, String var3);

    public boolean isNotificationTypeDismissed(NotificationType var1, Option<UserKey> var2);

    public boolean isNotificationDismissed(NotificationType var1, Option<UserKey> var2, String var3);

    public void setNotifications(NotificationType var1, Collection<String> var2);

    public void addNotificationForPlugin(NotificationType var1, String var2);

    public void removeNotificationForPlugin(NotificationType var1, String var2);

    public void setNotificationCount(NotificationType var1, int var2);

    public void setNotificationTypeDismissal(NotificationType var1, UserKey var2, boolean var3);

    public void setNotificationDismissal(NotificationType var1, UserKey var2, String var3, boolean var4);

    public void resetNotificationTypeDismissal(NotificationType var1);

    public void resetNotificationDismissal(NotificationType var1, String var2);
}

