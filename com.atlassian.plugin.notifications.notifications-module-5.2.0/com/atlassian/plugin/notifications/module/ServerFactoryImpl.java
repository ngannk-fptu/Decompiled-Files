/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.event.events.PluginModuleDisabledEvent
 *  org.apache.log4j.Logger
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.plugin.notifications.module;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.event.events.PluginModuleDisabledEvent;
import com.atlassian.plugin.notifications.api.medium.NotificationMedium;
import com.atlassian.plugin.notifications.api.medium.Server;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.ServerFactory;
import com.atlassian.plugin.notifications.module.NotificationMediumModuleDescriptor;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class ServerFactoryImpl
implements ServerFactory,
InitializingBean,
DisposableBean {
    private static final Logger log = Logger.getLogger(ServerFactoryImpl.class);
    private final ConcurrentMap<Integer, Server> servers = new ConcurrentHashMap<Integer, Server>();
    private final EventPublisher eventPublisher;

    public ServerFactoryImpl(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Server getServer(ServerConfiguration serverConfiguration) {
        Server server = (Server)this.servers.get(serverConfiguration.getId());
        if (server == null || !serverConfiguration.equals(server.getConfig())) {
            this.removeServer(serverConfiguration.getId());
            return this.createServer(serverConfiguration);
        }
        return server;
    }

    @EventListener
    public void onPluginDisabledEvent(PluginModuleDisabledEvent pluginModuleDisabledEvent) {
        ModuleDescriptor descriptor = pluginModuleDisabledEvent.getModule();
        if (descriptor instanceof NotificationMediumModuleDescriptor) {
            String disabledPluginKey = descriptor.getKey();
            for (Integer serverId : this.servers.keySet()) {
                String serverPluginKey;
                Server server = (Server)this.servers.get(serverId);
                if (server == null || !disabledPluginKey.equals(serverPluginKey = server.getConfig().getNotificationMedium().getKey())) continue;
                this.removeServer(serverId);
            }
        }
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() {
        this.clear();
        this.eventPublisher.unregister((Object)this);
    }

    private Server createServer(ServerConfiguration config) {
        NotificationMedium notificationMedium = config.getNotificationMedium();
        if (notificationMedium != null) {
            Server server = notificationMedium.createServer(config);
            this.servers.put(config.getId(), server);
            return server;
        }
        return null;
    }

    @Override
    public void clear() {
        for (Integer serverId : this.servers.keySet()) {
            this.removeServer(serverId);
        }
    }

    private void removeServer(int serverId) {
        Server server = (Server)this.servers.get(serverId);
        if (server != null) {
            this.servers.remove(serverId);
            try {
                server.terminate();
            }
            catch (RuntimeException e) {
                log.error((Object)("Error shutting down server '" + server.getConfig().getServerName() + "'."), (Throwable)e);
            }
        }
    }
}

