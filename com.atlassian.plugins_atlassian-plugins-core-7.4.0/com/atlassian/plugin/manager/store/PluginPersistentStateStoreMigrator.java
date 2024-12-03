/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.manager.PluginEnabledState
 *  io.atlassian.fugue.Effect
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.manager.store;

import com.atlassian.plugin.manager.PluginEnabledState;
import com.atlassian.plugin.manager.PluginPersistentState;
import com.atlassian.plugin.manager.PluginPersistentStateModifier;
import com.atlassian.plugin.manager.PluginPersistentStateStore;
import io.atlassian.fugue.Effect;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginPersistentStateStoreMigrator {
    private static final Logger log = LoggerFactory.getLogger(PluginPersistentStateStoreMigrator.class);

    private PluginPersistentStateStoreMigrator() {
    }

    public static void removeDirectives(PluginPersistentStateStore store) {
        new PluginPersistentStateModifier(store).apply((Effect<PluginPersistentState.Builder>)((Effect)builder -> {
            Map state = builder.toState().getStatesMap();
            HashMap<String, PluginEnabledState> newState = new HashMap<String, PluginEnabledState>(state.size());
            for (Map.Entry entry : state.entrySet()) {
                String key = (String)entry.getKey();
                String newKey = PluginPersistentStateStoreMigrator.removeDirectivesFromKey(key);
                if (newKey == null) continue;
                builder.removeState(key);
                if (state.containsKey(newKey)) {
                    log.warn("{} contains both {} and {}", new Object[]{store, key, newKey});
                }
                newState.put(newKey, (PluginEnabledState)entry.getValue());
            }
            builder.addPluginEnabledState(newState);
        }));
    }

    public static String removeDirectivesFromKey(String key) {
        int directiveBeg;
        if (key.contains("--")) {
            return null;
        }
        int versionBeg = key.indexOf(45);
        if (versionBeg == -1) {
            versionBeg = key.length();
        }
        if ((directiveBeg = key.indexOf(59)) > -1 && directiveBeg < versionBeg) {
            return key.substring(0, directiveBeg) + key.substring(versionBeg);
        }
        return null;
    }
}

