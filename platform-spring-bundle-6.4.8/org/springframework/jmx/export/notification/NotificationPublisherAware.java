/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jmx.export.notification;

import org.springframework.beans.factory.Aware;
import org.springframework.jmx.export.notification.NotificationPublisher;

public interface NotificationPublisherAware
extends Aware {
    public void setNotificationPublisher(NotificationPublisher var1);
}

