/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.manager;

import java.io.Serializable;

public class PluginEnabledState
implements Serializable {
    public static final long UNKNOWN_ENABLED_TIME = 0L;
    private final boolean enabled;
    private final long timestamp;

    public PluginEnabledState(boolean enabled, long timestamp) {
        this.enabled = enabled;
        this.timestamp = timestamp;
    }

    @Deprecated
    public PluginEnabledState(boolean enabled) {
        this(enabled, 0L);
    }

    public Boolean isEnabled() {
        return this.enabled;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PluginEnabledState that = (PluginEnabledState)o;
        return this.enabled == that.enabled;
    }

    public int hashCode() {
        return this.enabled ? 1 : 0;
    }

    public static PluginEnabledState getPluginEnabledStateWithCurrentTime(boolean enabled) {
        return new PluginEnabledState(enabled, System.currentTimeMillis());
    }
}

