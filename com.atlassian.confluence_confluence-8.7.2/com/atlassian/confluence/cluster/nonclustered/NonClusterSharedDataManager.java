/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.plugin.Plugin
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Maps
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cluster.nonclustered;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.cluster.shareddata.PluginSharedDataKey;
import com.atlassian.confluence.cluster.shareddata.PluginSharedDataStore;
import com.atlassian.confluence.cluster.shareddata.SharedData;
import com.atlassian.confluence.cluster.shareddata.SharedDataManager;
import com.atlassian.plugin.Plugin;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public final class NonClusterSharedDataManager
implements SharedDataManager,
PluginSharedDataStore {
    private static final Logger log = LoggerFactory.getLogger(NonClusterSharedDataManager.class);
    private final ConcurrentMap<String, Map<Serializable, Serializable>> sharedDataMaps = Maps.newConcurrentMap();

    private <K extends Serializable, V extends Serializable> ConcurrentMap<K, V> getSharedDataMap(String mapName) {
        Preconditions.checkNotNull((Object)mapName);
        ConcurrentMap existingMap = (ConcurrentMap)this.sharedDataMaps.get(mapName);
        if (existingMap == null) {
            this.sharedDataMaps.putIfAbsent(mapName, Maps.newConcurrentMap());
            return (ConcurrentMap)this.sharedDataMaps.get(mapName);
        }
        return existingMap;
    }

    @Override
    public SharedData getPluginSharedData(PluginSharedDataKey sharedDataKey, Plugin plugin) {
        String mapName = String.format("%s:%s:%s", sharedDataKey.getPluginKey(), plugin.getPluginInformation().getVersion(), sharedDataKey.getSharedDataKey());
        ConcurrentMap map = this.getSharedDataMap(mapName);
        log.debug("Returning shared data map [{}]", (Object)mapName);
        return NonClusterSharedDataManager.sharedData(map);
    }

    @Override
    public void unregisterPluginSharedData(Plugin plugin) {
        String pluginKey = plugin.getKey();
        String pluginVersion = plugin.getPluginInformation().getVersion();
        String mapNamePrefix = String.format("%s:%s:", pluginKey, pluginVersion);
        Collection mapNamesToClear = Collections2.filter(this.sharedDataMaps.keySet(), mapName -> mapName.startsWith(mapNamePrefix));
        if (mapNamesToClear.isEmpty()) {
            log.debug("No shared data to clear for plugin [{}] version [{}]", (Object)pluginKey, (Object)pluginVersion);
        } else {
            log.debug("Clearing shared data maps associated with unregistered plugin [{}] version [{}]: {}", new Object[]{pluginKey, pluginVersion, mapNamesToClear});
            for (String mapName2 : mapNamesToClear) {
                Map map = (Map)this.sharedDataMaps.remove(mapName2);
                if (map == null) continue;
                log.debug("Clearing shared data map [{}]", (Object)mapName2);
                map.clear();
            }
            log.debug("Cleared all shared data maps related to plugin [{}] version [{}]", (Object)pluginKey, (Object)pluginVersion);
        }
    }

    @Override
    public SharedData getSharedData(String name) {
        return NonClusterSharedDataManager.sharedData(this.getSharedDataMap(name));
    }

    private static SharedData sharedData(final Map map) {
        return new SharedData(){

            @Override
            public <K extends Serializable, V extends Serializable> @NonNull Map<K, V> getMap() {
                return map;
            }
        };
    }
}

