/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nullable
 */
package com.atlassian.plugin.notifications.config;

import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.config.ServerConfigurationManager;
import com.atlassian.plugin.notifications.config.UserServerManager;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nullable;

public class UserServerManagerImpl
implements UserServerManager {
    private static final String SERVER_VISITED_PREFIX = "notifications.server.visited.";
    private final ServerConfigurationManager serverConfigManager;
    private final UserManager userManager;
    private final PluginSettingsFactory pluginSettingsFactory;

    public UserServerManagerImpl(ServerConfigurationManager serverConfigManager, UserManager userManager, PluginSettingsFactory pluginSettingsFactory) {
        this.serverConfigManager = serverConfigManager;
        this.userManager = userManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    @Override
    public boolean hasNewServers(UserKey remoteUser) {
        if (remoteUser == null) {
            return false;
        }
        Iterable<ServerConfiguration> serversForIndividual = this.getServers(remoteUser);
        PluginSettings preferences = this.pluginSettingsFactory.createGlobalSettings();
        boolean newServerAvailable = false;
        for (ServerConfiguration serverConfiguration : serversForIndividual) {
            String propertykey;
            boolean visited;
            if (!serverConfiguration.isConfigurable() || (visited = Boolean.parseBoolean((String)preferences.get(propertykey = this.getServerKey(remoteUser, serverConfiguration.getId()))))) continue;
            newServerAvailable = true;
            break;
        }
        return newServerAvailable;
    }

    @Override
    public void setVisited(UserKey remoteUser, Set<Integer> serverIds) {
        if (remoteUser == null) {
            return;
        }
        PluginSettings preferences = this.pluginSettingsFactory.createGlobalSettings();
        for (Integer serverId : serverIds) {
            String propertyKey = this.getServerKey(remoteUser, serverId);
            preferences.put(propertyKey, (Object)Boolean.TRUE.toString());
        }
    }

    @Override
    public Iterable<ServerConfiguration> getServers(final UserKey remoteUser) {
        if (remoteUser == null) {
            return Collections.emptySet();
        }
        return Iterables.filter(this.serverConfigManager.getServersForIndividual(), (Predicate)new Predicate<ServerConfiguration>(){

            public boolean apply(@Nullable ServerConfiguration server) {
                if (server == null) {
                    return false;
                }
                Iterable<String> groupsWithAccess = server.getGroupsWithAccess();
                return Iterables.isEmpty(groupsWithAccess) || Iterables.any(groupsWithAccess, (Predicate)new Predicate<String>(){

                    public boolean apply(@Nullable String groupWithAccess) {
                        return groupWithAccess != null && UserServerManagerImpl.this.userManager.isUserInGroup(remoteUser, groupWithAccess);
                    }
                });
            }
        });
    }

    private String getServerKey(UserKey remoteUser, int serverId) {
        return SERVER_VISITED_PREFIX + remoteUser.getStringValue() + "." + Integer.toString(serverId);
    }
}

