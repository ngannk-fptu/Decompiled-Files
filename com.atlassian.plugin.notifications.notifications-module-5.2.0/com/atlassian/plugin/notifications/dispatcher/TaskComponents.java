/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.tracker.DefaultPluginModuleTracker
 *  com.atlassian.plugin.tracker.PluginModuleTracker
 */
package com.atlassian.plugin.notifications.dispatcher;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.notifications.api.NotificationHandler;
import com.atlassian.plugin.notifications.api.macros.MacroResolver;
import com.atlassian.plugin.notifications.api.medium.RenderContextFactory;
import com.atlassian.plugin.notifications.api.medium.ServerFactory;
import com.atlassian.plugin.notifications.api.medium.ServerManager;
import com.atlassian.plugin.notifications.api.notification.UserNotificationPreferencesManager;
import com.atlassian.plugin.notifications.config.ServerConfigurationManager;
import com.atlassian.plugin.notifications.config.UserServerManager;
import com.atlassian.plugin.notifications.dispatcher.NotificationErrorRegistry;
import com.atlassian.plugin.notifications.dispatcher.NotificationHandlerModuleDescriptor;
import com.atlassian.plugin.notifications.spi.AnalyticsEventPublisher;
import com.atlassian.plugin.tracker.DefaultPluginModuleTracker;
import com.atlassian.plugin.tracker.PluginModuleTracker;

public class TaskComponents {
    private final ServerConfigurationManager serverConfigurationManager;
    private final ServerFactory serverFactory;
    private final UserNotificationPreferencesManager notificationPreferencesManager;
    private final MacroResolver macroResolver;
    private final NotificationErrorRegistry notificationErrorRegistry;
    private final UserServerManager userServerManager;
    private final PluginModuleTracker<NotificationHandler, NotificationHandlerModuleDescriptor> handlerTracker;
    private final ServerManager serverManager;
    private final RenderContextFactory renderContextFactory;
    private final AnalyticsEventPublisher eventPublisher;

    public TaskComponents(ServerConfigurationManager serverConfigurationManager, ServerFactory serverFactory, UserNotificationPreferencesManager notificationPreferencesManager, MacroResolver macroResolver, NotificationErrorRegistry notificationErrorRegistry, UserServerManager userServerManager, PluginAccessor pluginAccessor, PluginEventManager pluginEventManager, ServerManager serverManager, RenderContextFactory renderContextFactory, AnalyticsEventPublisher eventPublisher) {
        this.serverConfigurationManager = serverConfigurationManager;
        this.serverFactory = serverFactory;
        this.notificationPreferencesManager = notificationPreferencesManager;
        this.macroResolver = macroResolver;
        this.notificationErrorRegistry = notificationErrorRegistry;
        this.userServerManager = userServerManager;
        this.serverManager = serverManager;
        this.renderContextFactory = renderContextFactory;
        this.eventPublisher = eventPublisher;
        this.handlerTracker = DefaultPluginModuleTracker.create((PluginAccessor)pluginAccessor, (PluginEventManager)pluginEventManager, NotificationHandlerModuleDescriptor.class);
    }

    public ServerConfigurationManager getServerConfigurationManager() {
        return this.serverConfigurationManager;
    }

    public UserServerManager getUserServerManager() {
        return this.userServerManager;
    }

    public ServerFactory getServerFactory() {
        return this.serverFactory;
    }

    public UserNotificationPreferencesManager getNotificationPreferencesManager() {
        return this.notificationPreferencesManager;
    }

    public MacroResolver getMacroResolver() {
        return this.macroResolver;
    }

    public NotificationErrorRegistry getErrorRegistry() {
        return this.notificationErrorRegistry;
    }

    public PluginModuleTracker<NotificationHandler, NotificationHandlerModuleDescriptor> getHandlerTracker() {
        return this.handlerTracker;
    }

    public ServerManager getServerManager() {
        return this.serverManager;
    }

    public RenderContextFactory getRenderContextFactory() {
        return this.renderContextFactory;
    }

    public AnalyticsEventPublisher getAnalyticsPublisher() {
        return this.eventPublisher;
    }
}

