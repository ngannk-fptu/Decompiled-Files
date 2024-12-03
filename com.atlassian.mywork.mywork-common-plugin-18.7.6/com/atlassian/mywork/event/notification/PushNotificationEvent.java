/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.AsynchronousPreferred
 *  javax.annotation.Nonnull
 */
package com.atlassian.mywork.event.notification;

import com.atlassian.event.api.AsynchronousPreferred;
import com.atlassian.mywork.model.Notification;
import java.io.Serializable;
import java.util.List;
import javax.annotation.Nonnull;

@AsynchronousPreferred
public class PushNotificationEvent
implements Serializable {
    private final List<Notification> notifications;

    public PushNotificationEvent(@Nonnull List<Notification> notifications) {
        this.notifications = notifications;
    }

    public List<Notification> getNotifications() {
        return this.notifications;
    }
}

