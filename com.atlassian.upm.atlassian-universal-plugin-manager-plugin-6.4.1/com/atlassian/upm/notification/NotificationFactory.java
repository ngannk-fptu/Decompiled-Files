/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.notification;

import com.atlassian.upm.api.util.Pair;
import com.atlassian.upm.notification.Notification;
import com.atlassian.upm.notification.NotificationCollection;
import com.atlassian.upm.notification.NotificationType;
import java.util.List;

public interface NotificationFactory {
    public Notification getNotification(NotificationType var1, String var2, boolean var3);

    public NotificationCollection getNotifications(NotificationType var1, List<Pair<String, Boolean>> var2, boolean var3);

    public NotificationCollection getNotifications(NotificationType var1, int var2, boolean var3);
}

