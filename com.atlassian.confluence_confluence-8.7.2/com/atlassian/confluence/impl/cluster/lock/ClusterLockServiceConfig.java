/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.plugin.spring.AvailableToPlugins
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.impl.cluster.lock;

import com.atlassian.beehive.ClusterLockService;
import com.atlassian.plugin.spring.AvailableToPlugins;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClusterLockServiceConfig {
    @Resource
    private ClusterLockService clusterManager;

    @Bean
    @AvailableToPlugins(interfaces={ClusterLockService.class})
    ClusterLockService clusterLockService() {
        return this.clusterManager;
    }
}

