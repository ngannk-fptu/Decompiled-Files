/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.manager.store;

import com.atlassian.plugin.manager.PluginPersistentState;
import com.atlassian.plugin.manager.PluginPersistentStateStore;

public abstract class DelegatingPluginPersistentStateStore
implements PluginPersistentStateStore {
    public abstract PluginPersistentStateStore getDelegate();

    @Override
    public void save(PluginPersistentState state) {
        this.getDelegate().save(state);
    }

    @Override
    public PluginPersistentState load() {
        return this.getDelegate().load();
    }
}

