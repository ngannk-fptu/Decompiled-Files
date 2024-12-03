/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.AvailableToPlugins
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.cluster.shareddata;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.nonclustered.NonClusterSharedDataManager;
import com.atlassian.confluence.cluster.shareddata.DefaultPluginSharedDataRegistry;
import com.atlassian.confluence.cluster.shareddata.PluginSharedDataRegistry;
import com.atlassian.confluence.cluster.shareddata.PluginSharedDataStore;
import com.atlassian.confluence.cluster.shareddata.SharedDataManager;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.AvailableToPlugins;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SharedDataContextConfig {
    @Resource
    private ClusterManager clusterManager;
    @Resource
    private SharedDataManager hazelcastSharedDataManager;
    @Resource
    private PluginSharedDataStore hazelcastPluginSharedDataStore;
    @Resource
    private PluginAccessor pluginAccessor;
    @Resource
    private EventListenerRegistrar eventPublisher;

    @Bean
    SharedDataManager clusterSharedDataManager() {
        return this.clusterManager.isClustered() ? this.hazelcastSharedDataManager : new NonClusterSharedDataManager();
    }

    @Bean
    PluginSharedDataStore pluginSharedDataStore() {
        return this.clusterManager.isClustered() ? this.hazelcastPluginSharedDataStore : new NonClusterSharedDataManager();
    }

    @Bean
    @AvailableToPlugins(interfaces={PluginSharedDataRegistry.class})
    PluginSharedDataRegistry pluginSharedDataRegistry() {
        return new DefaultPluginSharedDataRegistry(this.pluginAccessor, this.eventPublisher, this.pluginSharedDataStore());
    }
}

