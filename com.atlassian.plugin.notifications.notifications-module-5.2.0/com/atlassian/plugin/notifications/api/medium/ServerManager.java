/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.plugin.notifications.api.medium;

import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.sal.api.user.UserKey;

public interface ServerManager {
    public ServerConfiguration getServer(int var1);

    public Iterable<ServerConfiguration> getServers();

    public Iterable<ServerConfiguration> getServersForUser(UserKey var1);
}

