/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.event.events;

import java.util.Objects;

public class PluginContainerFailedEvent {
    private final Object container;
    private final String key;
    private final Throwable cause;

    public PluginContainerFailedEvent(Object container, String key, Throwable cause) {
        this.key = Objects.requireNonNull(key, "The bundle symbolic name must be available");
        this.container = container;
        this.cause = cause;
    }

    public Object getContainer() {
        return this.container;
    }

    public String getPluginKey() {
        return this.key;
    }

    public Throwable getCause() {
        return this.cause;
    }
}

