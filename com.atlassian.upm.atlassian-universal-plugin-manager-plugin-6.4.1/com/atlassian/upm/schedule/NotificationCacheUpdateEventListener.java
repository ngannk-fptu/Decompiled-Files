/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.event.events.PluginUninstalledEvent
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.upm.schedule;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.event.events.PluginUninstalledEvent;
import com.atlassian.upm.lifecycle.UpmProductDataStartupComponent;
import com.atlassian.upm.notification.NotificationCache;
import com.atlassian.upm.notification.NotificationType;
import java.util.Objects;
import org.springframework.beans.factory.DisposableBean;

public class NotificationCacheUpdateEventListener
implements UpmProductDataStartupComponent,
DisposableBean {
    private final EventPublisher eventPublisher;
    private final NotificationCache cache;

    public NotificationCacheUpdateEventListener(EventPublisher eventPublisher, NotificationCache cache) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher");
        this.cache = Objects.requireNonNull(cache, "cache");
    }

    @Override
    public void onStartupWithProductData() {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onPluginUninstalled(PluginUninstalledEvent event) {
        String pluginKey = event.getPlugin().getKey();
        for (NotificationType type : NotificationType.values()) {
            if (!type.isForInstalledPluginsOnly()) continue;
            this.cache.removeNotificationForPlugin(type, pluginKey);
        }
    }
}

