/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.Lists
 */
package com.atlassian.plugin.notifications.dispatcher;

import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.ServerManager;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.plugin.notifications.dispatcher.IndividualRecipientPreferences;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.Lists;
import java.util.Collections;

public class SingleServerPreferences
implements IndividualRecipientPreferences {
    private final ServerManager serverManager;
    private final int serverId;

    public SingleServerPreferences(ServerManager serverManager, int serverId) {
        this.serverManager = serverManager;
        this.serverId = serverId;
    }

    @Override
    public Iterable<ServerConfiguration> getServers(UserKey userKey) {
        ServerConfiguration server = this.serverManager.getServer(this.serverId);
        if (server != null) {
            return Lists.newArrayList((Object[])new ServerConfiguration[]{server});
        }
        return Collections.emptyList();
    }

    @Override
    public boolean shouldSend(RoleRecipient recipient, UserKey eventAuthor, ServerConfiguration serverConfig) {
        return true;
    }
}

