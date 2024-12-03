/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.plugin.notifications.config;

import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.sal.api.user.UserKey;
import java.util.Set;

public interface UserServerManager {
    public boolean hasNewServers(UserKey var1);

    public void setVisited(UserKey var1, Set<Integer> var2);

    public Iterable<ServerConfiguration> getServers(UserKey var1);
}

