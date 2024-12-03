/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.tracker.DefaultPluginModuleTracker
 *  com.atlassian.plugin.tracker.PluginModuleTracker
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.plugin.notifications.dispatcher;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.notifications.api.NotificationHandler;
import com.atlassian.plugin.notifications.api.queue.NotificationQueueManager;
import com.atlassian.plugin.notifications.dispatcher.NotificationHandlerModuleDescriptor;
import com.atlassian.plugin.notifications.dispatcher.util.MatchingHandlerPredicate;
import com.atlassian.plugin.tracker.DefaultPluginModuleTracker;
import com.atlassian.plugin.tracker.PluginModuleTracker;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class NotificationsEventListener
implements InitializingBean,
DisposableBean {
    private final EventPublisher eventPublisher;
    private final NotificationQueueManager notificationQueueManager;
    private PluginModuleTracker<NotificationHandler, NotificationHandlerModuleDescriptor> handlerTracker;

    public NotificationsEventListener(EventPublisher eventPublisher, PluginAccessor pluginAccessor, PluginEventManager pluginEventManager, NotificationQueueManager notificationQueueManager) {
        this.eventPublisher = eventPublisher;
        this.notificationQueueManager = notificationQueueManager;
        this.handlerTracker = DefaultPluginModuleTracker.create((PluginAccessor)pluginAccessor, (PluginEventManager)pluginEventManager, NotificationHandlerModuleDescriptor.class);
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void handleEvent(Object event) {
        Iterable handlers = this.handlerTracker.getModuleDescriptors();
        boolean matchingHandlers = Iterables.any((Iterable)handlers, (Predicate)new MatchingHandlerPredicate(event));
        if (matchingHandlers) {
            this.notificationQueueManager.processEvent(event);
        }
    }
}

