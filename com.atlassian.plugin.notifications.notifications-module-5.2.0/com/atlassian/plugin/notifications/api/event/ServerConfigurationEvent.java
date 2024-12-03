/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.notifications.api.event;

import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;

public class ServerConfigurationEvent {
    private final ConfigEventType type;
    private final int id;
    private final ServerConfiguration config;

    public ServerConfigurationEvent(ConfigEventType type, int id, ServerConfiguration config) {
        this.type = type;
        this.id = id;
        this.config = config;
    }

    public int getId() {
        return this.id;
    }

    public ConfigEventType getType() {
        return this.type;
    }

    public ServerConfiguration getConfig() {
        return this.config;
    }

    public static enum ConfigEventType {
        CREATED,
        UPDATED,
        REMOVED;

    }
}

