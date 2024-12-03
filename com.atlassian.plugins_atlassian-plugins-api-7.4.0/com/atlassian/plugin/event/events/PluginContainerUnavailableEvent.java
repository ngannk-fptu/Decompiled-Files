/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.event.events;

import java.util.Objects;

public class PluginContainerUnavailableEvent {
    private final String key;

    public PluginContainerUnavailableEvent(String key) {
        this.key = Objects.requireNonNull(key, "The plugin key must be available");
    }

    public String getPluginKey() {
        return this.key;
    }
}

