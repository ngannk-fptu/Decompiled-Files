/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.plugin;

import com.atlassian.confluence.event.events.admin.ConfigurationEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;
import java.util.Objects;

public abstract class PluginEvent
extends ConfigurationEvent
implements ClusterEvent {
    private String pluginKey;

    public PluginEvent(Object src, String pluginKey) {
        super(src);
        this.pluginKey = pluginKey;
    }

    public String getPluginKey() {
        return this.pluginKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        PluginEvent event = (PluginEvent)o;
        return Objects.equals(this.pluginKey, event.pluginKey);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (this.pluginKey != null ? this.pluginKey.hashCode() : 0);
        return result;
    }

    public String toString() {
        return this.getClass().getName() + "(" + this.pluginKey + ")";
    }
}

