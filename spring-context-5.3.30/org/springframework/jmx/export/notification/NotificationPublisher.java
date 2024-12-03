/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jmx.export.notification;

import javax.management.Notification;
import org.springframework.jmx.export.notification.UnableToSendNotificationException;

@FunctionalInterface
public interface NotificationPublisher {
    public void sendNotification(Notification var1) throws UnableToSendNotificationException;
}

