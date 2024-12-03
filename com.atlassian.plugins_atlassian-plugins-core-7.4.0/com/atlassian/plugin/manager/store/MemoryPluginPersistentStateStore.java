/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.manager.store;

import com.atlassian.plugin.manager.PluginPersistentState;
import com.atlassian.plugin.manager.PluginPersistentStateStore;

public class MemoryPluginPersistentStateStore
implements PluginPersistentStateStore {
    private volatile PluginPersistentState state = PluginPersistentState.builder().toState();

    @Override
    public void save(PluginPersistentState state) {
        this.state = state;
    }

    @Override
    public PluginPersistentState load() {
        return this.state;
    }
}

