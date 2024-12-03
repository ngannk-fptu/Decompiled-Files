/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.cluster.safety;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.safety.ClusterSafetyDao;
import com.atlassian.confluence.cluster.safety.ClusterSafetyManager;
import com.atlassian.confluence.cluster.safety.DefaultClusterSafetyManager;
import com.atlassian.confluence.cluster.shareddata.SharedDataManager;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.event.api.EventPublisher;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ClusterSafetyContextConfig {
    @Resource
    private ClusterManager clusterManager;
    @Resource
    private ClusterSafetyDao clusterSafetyDao;
    @Resource
    private EventPublisher eventPublisher;
    @Resource
    private SharedDataManager clusterSharedDataManager;
    @Resource
    private LicenseService licenseService;
    @Resource
    private ClusterSafetyManager hazelcastClusterSafetyManager;

    ClusterSafetyContextConfig() {
    }

    @Bean
    ClusterSafetyManager clusterSafetyManager() {
        if (this.clusterManager.isClustered()) {
            return this.hazelcastClusterSafetyManager;
        }
        return new DefaultClusterSafetyManager(this.clusterSafetyDao, this.eventPublisher, this.clusterSharedDataManager, this.clusterManager, this.licenseService);
    }
}

