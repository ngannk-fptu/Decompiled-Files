/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.manager;

import com.atlassian.plugin.manager.PluginPersistentState;

public interface PluginPersistentStateStore {
    public void save(PluginPersistentState var1);

    public PluginPersistentState load();
}

