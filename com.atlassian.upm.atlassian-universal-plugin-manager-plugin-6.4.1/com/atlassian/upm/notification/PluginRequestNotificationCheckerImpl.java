/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.notification;

import com.atlassian.upm.notification.NotificationCache;
import com.atlassian.upm.notification.NotificationType;
import com.atlassian.upm.notification.PluginRequestNotificationChecker;
import com.atlassian.upm.request.PluginRequest;
import com.atlassian.upm.request.PluginRequestStore;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginRequestNotificationCheckerImpl
implements PluginRequestNotificationChecker {
    private static final Logger log = LoggerFactory.getLogger(PluginRequestNotificationCheckerImpl.class);
    private final NotificationCache cache;
    private final PluginRequestStore pluginRequestStore;

    public PluginRequestNotificationCheckerImpl(NotificationCache cache, PluginRequestStore pluginRequestStore) {
        this.cache = Objects.requireNonNull(cache, "cache");
        this.pluginRequestStore = Objects.requireNonNull(pluginRequestStore, "pluginRequestStore");
    }

    @Override
    public void updatePluginRequestNotifications() {
        try {
            List<PluginRequest> pluginRequests = this.pluginRequestStore.getRequests();
            this.cache.setNotifications(NotificationType.PLUGIN_REQUEST, Collections.unmodifiableList(StreamSupport.stream(pluginRequests.spliterator(), false).map(PluginRequest.toPluginKey()).collect(Collectors.toList())));
        }
        catch (Exception e) {
            log.warn("Error getting plugin request notifications", (Throwable)e);
        }
    }
}

