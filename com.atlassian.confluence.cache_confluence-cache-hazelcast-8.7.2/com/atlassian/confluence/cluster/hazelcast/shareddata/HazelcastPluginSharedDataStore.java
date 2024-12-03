/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.cluster.shareddata.PluginSharedDataKey
 *  com.atlassian.confluence.cluster.shareddata.PluginSharedDataStore
 *  com.atlassian.confluence.cluster.shareddata.SharedData
 *  com.atlassian.plugin.Plugin
 *  com.google.common.base.Preconditions
 *  com.hazelcast.core.HazelcastInstance
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cluster.hazelcast.shareddata;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.cluster.hazelcast.shareddata.HazelcastSharedDataSupport;
import com.atlassian.confluence.cluster.shareddata.PluginSharedDataKey;
import com.atlassian.confluence.cluster.shareddata.PluginSharedDataStore;
import com.atlassian.confluence.cluster.shareddata.SharedData;
import com.atlassian.plugin.Plugin;
import com.google.common.base.Preconditions;
import com.hazelcast.core.HazelcastInstance;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated(since="8.2", forRemoval=true)
@Internal
public class HazelcastPluginSharedDataStore
implements PluginSharedDataStore {
    private static final Logger log = LoggerFactory.getLogger(HazelcastPluginSharedDataStore.class);
    private static final String PREFIX = HazelcastPluginSharedDataStore.class.getSimpleName();
    private final HazelcastInstance hazelcastInstance;
    private final HazelcastSharedDataSupport support;

    public HazelcastPluginSharedDataStore(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = (HazelcastInstance)Preconditions.checkNotNull((Object)hazelcastInstance);
        this.support = new HazelcastSharedDataSupport(PREFIX, hazelcastInstance);
    }

    private String localNodeId() {
        return this.hazelcastInstance.getCluster().getLocalMember().getUuid();
    }

    public SharedData getPluginSharedData(PluginSharedDataKey sharedDataKey, Plugin plugin) {
        String pluginKey = sharedDataKey.getPluginKey();
        String pluginVersion = plugin.getPluginInformation().getVersion();
        if (this.participatingPluginKeys().add(pluginKey)) {
            log.debug("Plugin [{}] is now participating in the shared data mechanism", (Object)pluginKey);
        }
        if (this.sharedDataKeysForPluginVersion(pluginKey, pluginVersion).add(sharedDataKey)) {
            log.debug("Registered new shared data key [{}] for plugin [{} {}]", new Object[]{sharedDataKey, pluginKey, pluginVersion});
        }
        if (this.getRegisteredClusterNodes(pluginKey, pluginVersion).add(this.localNodeId())) {
            log.debug("Registered cluster node [{}] for shared data for plugin [{} {}]", new Object[]{this.localNodeId(), pluginKey, pluginVersion});
        }
        log.debug("Returning shared data [{}] for plugin version [{}]", (Object)sharedDataKey, (Object)pluginVersion);
        return this.getSharedData(sharedDataKey, pluginVersion);
    }

    private SharedData getSharedData(PluginSharedDataKey sharedDataKey, String pluginVersion) {
        return this.support.getSharedData(String.format("%s:%s:%s.sharedData", sharedDataKey.getPluginKey(), pluginVersion, sharedDataKey.getSharedDataKey()));
    }

    public void unregisterPluginSharedData(Plugin plugin) {
        String pluginKey = plugin.getKey();
        String pluginVersion = plugin.getPluginInformation().getVersion();
        if (!this.participatingPluginKeys().contains(pluginKey)) {
            log.debug("Plugin [{}] is not participating in the shared data mechanism", (Object)pluginKey);
            return;
        }
        Set<String> registeredNodes = this.getRegisteredClusterNodes(pluginKey, pluginVersion);
        if (registeredNodes.remove(this.localNodeId())) {
            log.debug("Unregistering cluster node [{}] for shared data for plugin [{} {}]", new Object[]{this.localNodeId(), pluginKey, pluginVersion});
        }
        if (registeredNodes.isEmpty()) {
            Set<PluginSharedDataKey> sharedDataKeys = this.sharedDataKeysForPluginVersion(pluginKey, pluginVersion);
            log.debug("No remaining cluster nodes registered for plugin [{} {}], cleaning up shared data {}", new Object[]{pluginKey, pluginVersion, sharedDataKeys});
            for (PluginSharedDataKey sharedDataKey : sharedDataKeys) {
                this.getSharedData(sharedDataKey, pluginVersion).getMap().clear();
            }
            log.debug("Shared data cleanup complete for {}", sharedDataKeys);
        }
    }

    private Set<String> getRegisteredClusterNodes(String pluginKey, String pluginVersion) {
        return this.support.getSharedSet(String.format("%s:%s.registeredNodes", pluginKey, pluginVersion));
    }

    private Set<PluginSharedDataKey> sharedDataKeysForPluginVersion(String pluginKey, String pluginVersion) {
        return this.support.getSharedSet(String.format("%s:%s.sharedDataKeys", pluginKey, pluginVersion));
    }

    private Set<String> participatingPluginKeys() {
        return this.support.getSharedSet("participatingPluginKeys");
    }
}

