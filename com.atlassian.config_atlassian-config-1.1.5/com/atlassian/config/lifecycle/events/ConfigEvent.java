/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.config.lifecycle.events;

import com.atlassian.config.lifecycle.events.LifecycleEvent;

abstract class ConfigEvent
implements LifecycleEvent {
    private final Object source;
    private long timestamp;

    public ConfigEvent(Object source) {
        this.source = source;
        this.timestamp = System.currentTimeMillis();
    }

    public Object getSource() {
        return this.source;
    }

    public long getTimestamp() {
        return this.timestamp;
    }
}

