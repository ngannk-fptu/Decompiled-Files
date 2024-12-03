/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.manager.PluginPersistentState
 *  com.atlassian.plugin.manager.PluginPersistentStateStore
 */
package com.atlassian.confluence.plugin;

import com.atlassian.confluence.plugin.EmptyPluginPersistentState;
import com.atlassian.plugin.manager.PluginPersistentState;
import com.atlassian.plugin.manager.PluginPersistentStateStore;

public class EmptyPluginStateStore
implements PluginPersistentStateStore {
    public void save(PluginPersistentState state) {
    }

    public PluginPersistentState load() {
        return new EmptyPluginPersistentState();
    }
}

