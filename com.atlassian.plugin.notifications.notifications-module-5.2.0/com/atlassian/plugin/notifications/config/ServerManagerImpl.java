/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.plugin.notifications.config;

import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.ServerManager;
import com.atlassian.plugin.notifications.config.ServerConfigurationManager;
import com.atlassian.plugin.notifications.config.UserServerManager;
import com.atlassian.sal.api.user.UserKey;

public class ServerManagerImpl
implements ServerManager {
    private final ServerConfigurationManager serverConfigurationManager;
    private final UserServerManager userServerManager;

    public ServerManagerImpl(ServerConfigurationManager serverConfigurationManager, UserServerManager userServerManager) {
        this.serverConfigurationManager = serverConfigurationManager;
        this.userServerManager = userServerManager;
    }

    @Override
    public ServerConfiguration getServer(int serverId) {
        return this.serverConfigurationManager.getServer(serverId);
    }

    @Override
    public Iterable<ServerConfiguration> getServers() {
        return this.serverConfigurationManager.getServers();
    }

    @Override
    public Iterable<ServerConfiguration> getServersForUser(UserKey userKey) {
        return this.userServerManager.getServers(userKey);
    }
}

