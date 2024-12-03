/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.Aware
 */
package org.springframework.jmx.export.notification;

import org.springframework.beans.factory.Aware;
import org.springframework.jmx.export.notification.NotificationPublisher;

public interface NotificationPublisherAware
extends Aware {
    public void setNotificationPublisher(NotificationPublisher var1);
}

