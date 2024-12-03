/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.notifications.api.medium;

import com.atlassian.plugin.notifications.api.medium.Server;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;

public interface ServerFactory {
    public Server getServer(ServerConfiguration var1);

    public void clear();
}

