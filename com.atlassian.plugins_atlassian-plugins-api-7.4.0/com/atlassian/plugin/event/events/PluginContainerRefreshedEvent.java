/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.event.events;

import java.util.Objects;

public class PluginContainerRefreshedEvent {
    private final Object container;
    private final String key;

    public PluginContainerRefreshedEvent(Object container, String key) {
        this.container = Objects.requireNonNull(container, "The container cannot be null");
        this.key = Objects.requireNonNull(key, "The plugin key must be available");
    }

    public Object getContainer() {
        return this.container;
    }

    public String getPluginKey() {
        return this.key;
    }
}

