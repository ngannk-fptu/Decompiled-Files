/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.PostConstruct
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.diagnostics;

import com.atlassian.confluence.internal.diagnostics.JavaMemoryMonitor;
import com.sun.management.GarbageCollectionNotificationInfo;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GarbageCollectorListener
implements NotificationListener {
    private static final Logger logger = LoggerFactory.getLogger(GarbageCollectorListener.class);
    private final JavaMemoryMonitor javaMemoryMonitor;

    public GarbageCollectorListener(JavaMemoryMonitor javaMemoryMonitor) {
        this.javaMemoryMonitor = Objects.requireNonNull(javaMemoryMonitor);
    }

    @Override
    public void handleNotification(Notification notification, Object handback) {
        if (notification.getType().equals("com.sun.management.gc.notification")) {
            this.javaMemoryMonitor.updateGarbageCollectorDuration(this.getGarbageCollectorDuration(notification));
        }
    }

    private Duration getGarbageCollectorDuration(Notification notification) {
        CompositeData userData = (CompositeData)notification.getUserData();
        return Duration.ofMillis(GarbageCollectionNotificationInfo.from(userData).getGcInfo().getDuration());
    }

    @PostConstruct
    public void installListener() {
        ManagementFactory.getGarbageCollectorMXBeans().forEach(x -> {
            NotificationEmitter emitter = (NotificationEmitter)((Object)x);
            logger.debug("install listener for ", (Object)x.getName());
            emitter.addNotificationListener(this, null, null);
        });
    }
}

