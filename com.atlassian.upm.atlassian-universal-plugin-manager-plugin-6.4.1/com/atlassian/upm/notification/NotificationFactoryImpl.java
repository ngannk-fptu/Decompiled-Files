/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserManager
 */
package com.atlassian.upm.notification;

import com.atlassian.sal.api.user.UserManager;
import com.atlassian.upm.api.util.Pair;
import com.atlassian.upm.notification.DismissedState;
import com.atlassian.upm.notification.Notification;
import com.atlassian.upm.notification.NotificationCollection;
import com.atlassian.upm.notification.NotificationFactory;
import com.atlassian.upm.notification.NotificationType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class NotificationFactoryImpl
implements NotificationFactory {
    private final UserManager userManager;

    public NotificationFactoryImpl(UserManager userManager) {
        this.userManager = Objects.requireNonNull(userManager, "userManager");
    }

    @Override
    public Notification getNotification(NotificationType type, String pluginKey, boolean dismissed) {
        return new Notification(type, pluginKey, this.getDismissedState(dismissed));
    }

    @Override
    public NotificationCollection getNotifications(NotificationType type, List<Pair<String, Boolean>> plugins, boolean typeDismissed) {
        ArrayList<Notification> notifications = new ArrayList<Notification>();
        for (Pair<String, Boolean> plugin : plugins) {
            notifications.add(this.getNotification(type, plugin.first(), plugin.second()));
        }
        return new NotificationCollection(type, this.getDismissedState(typeDismissed), Collections.unmodifiableList(notifications));
    }

    @Override
    public NotificationCollection getNotifications(NotificationType type, int count, boolean typeDismissed) {
        return new NotificationCollection(type, this.getDismissedState(typeDismissed), count);
    }

    private DismissedState getDismissedState(boolean dismissed) {
        return new DismissedState(this.userManager.getRemoteUserKey(), dismissed);
    }
}

