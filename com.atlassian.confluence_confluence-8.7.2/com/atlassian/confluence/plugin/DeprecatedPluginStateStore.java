/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.manager.PluginEnabledState
 *  com.atlassian.plugin.manager.PluginPersistentState
 *  com.atlassian.plugin.manager.PluginPersistentState$Builder
 *  com.atlassian.plugin.manager.PluginPersistentStateStore
 *  com.atlassian.plugin.manager.store.DelegatingPluginPersistentStateStore
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin;

import com.atlassian.plugin.manager.PluginEnabledState;
import com.atlassian.plugin.manager.PluginPersistentState;
import com.atlassian.plugin.manager.PluginPersistentStateStore;
import com.atlassian.plugin.manager.store.DelegatingPluginPersistentStateStore;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeprecatedPluginStateStore
extends DelegatingPluginPersistentStateStore {
    private static final Logger LOG = LoggerFactory.getLogger(DeprecatedPluginStateStore.class);
    private final PluginPersistentStateStore delegatingStore;
    private final Collection<String> deprecatedPluginKeys;

    public DeprecatedPluginStateStore(PluginPersistentStateStore delegatingStore, Collection<String> deprecatedPluginKeys) {
        Objects.requireNonNull(delegatingStore);
        Objects.requireNonNull(deprecatedPluginKeys);
        LOG.debug("List of deprecated plugin key: {}", deprecatedPluginKeys);
        this.delegatingStore = delegatingStore;
        this.deprecatedPluginKeys = deprecatedPluginKeys;
    }

    public PluginPersistentStateStore getDelegate() {
        return this.delegatingStore;
    }

    public PluginPersistentState load() {
        PluginPersistentState pluginPersistentState = super.load();
        Map stateMap = pluginPersistentState.getStatesMap();
        HashMap decoratedStateMap = new HashMap(stateMap);
        if (decoratedStateMap.size() == 0) {
            this.deprecatedPluginKeys.stream().forEach(deprecatedPluginKey -> {
                LOG.debug("Set the state of plugin {} to disble", deprecatedPluginKey);
                decoratedStateMap.put(deprecatedPluginKey, new PluginEnabledState(false, 0L));
            });
        }
        return PluginPersistentState.Builder.create().addPluginEnabledState(decoratedStateMap).toState();
    }
}

