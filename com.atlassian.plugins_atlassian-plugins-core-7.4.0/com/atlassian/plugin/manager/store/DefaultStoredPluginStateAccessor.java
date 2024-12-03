/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.StoredPluginState
 *  com.atlassian.plugin.StoredPluginStateAccessor
 */
package com.atlassian.plugin.manager.store;

import com.atlassian.plugin.StoredPluginState;
import com.atlassian.plugin.StoredPluginStateAccessor;
import com.atlassian.plugin.manager.PluginPersistentStateStore;
import java.util.Objects;

public class DefaultStoredPluginStateAccessor
implements StoredPluginStateAccessor {
    private final PluginPersistentStateStore pluginPersistentStateStore;

    public DefaultStoredPluginStateAccessor(PluginPersistentStateStore pluginPersistentStateStore) {
        this.pluginPersistentStateStore = Objects.requireNonNull(pluginPersistentStateStore);
    }

    public StoredPluginState get() {
        return this.pluginPersistentStateStore.load();
    }
}

