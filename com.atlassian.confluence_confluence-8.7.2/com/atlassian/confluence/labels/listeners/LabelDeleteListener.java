/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 */
package com.atlassian.confluence.labels.listeners;

import com.atlassian.confluence.event.events.label.LabelDeleteEvent;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.event.api.EventListener;

public class LabelDeleteListener {
    private final NotificationManager notificationManager;

    public LabelDeleteListener(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    @EventListener
    public void onLabelDelete(LabelDeleteEvent labelDeleteEvent) {
        for (Notification notification : this.notificationManager.getNotificationsByLabel(labelDeleteEvent.getLabel())) {
            this.notificationManager.removeNotification(notification);
        }
    }
}

