/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.cluster.ZduManager
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.zdu.confluence.impl;

import com.atlassian.audit.api.AuditService;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ZduManager;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.zdu.audit.ZduAuditListener;
import com.atlassian.zdu.confluence.impl.ConfluenceClusterManagerAdapter;
import com.atlassian.zdu.confluence.impl.ConfluenceRollingUpgradeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value={ZduAuditListener.class, ConfluenceClusterManagerAdapter.class, ConfluenceRollingUpgradeService.class})
public class ConfluenceSpringConfiguration {
    @Bean
    ClusterManager clusterManager() {
        return OsgiServices.importOsgiService(ClusterManager.class);
    }

    @Bean
    ZduManager zduManager() {
        return OsgiServices.importOsgiService(ZduManager.class);
    }

    @Bean
    AuditService auditService() {
        return OsgiServices.importOsgiService(AuditService.class);
    }
}

