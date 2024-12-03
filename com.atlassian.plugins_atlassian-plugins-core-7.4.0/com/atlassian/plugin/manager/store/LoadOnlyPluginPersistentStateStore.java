/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.plugin.manager.store;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.plugin.manager.PluginPersistentState;
import com.atlassian.plugin.manager.PluginPersistentStateStore;

@ExperimentalApi
public class LoadOnlyPluginPersistentStateStore
implements PluginPersistentStateStore {
    private final PluginPersistentState pluginPersistentState;

    public LoadOnlyPluginPersistentStateStore() {
        this(PluginPersistentState.Builder.create().toState());
    }

    public LoadOnlyPluginPersistentStateStore(PluginPersistentState pluginPersistentState) {
        this.pluginPersistentState = pluginPersistentState;
    }

    @Override
    public void save(PluginPersistentState state) {
        throw new IllegalStateException("Cannot save state to " + LoadOnlyPluginPersistentStateStore.class);
    }

    @Override
    public PluginPersistentState load() {
        return this.pluginPersistentState;
    }
}

