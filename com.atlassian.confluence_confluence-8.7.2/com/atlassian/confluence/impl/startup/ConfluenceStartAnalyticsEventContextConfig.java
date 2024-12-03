/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.parsers.DefaultSafeModeCommandLineArgumentsFactory
 *  com.atlassian.plugin.parsers.SafeModeCommandLineArgumentsFactory
 *  io.atlassian.util.concurrent.ThreadFactories
 *  io.atlassian.util.concurrent.ThreadFactories$Type
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.transaction.PlatformTransactionManager
 */
package com.atlassian.confluence.impl.startup;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.impl.startup.ConfluenceStartAnalyticsEventFactory;
import com.atlassian.confluence.impl.startup.ConfluenceStartAnalyticsEventPublisher;
import com.atlassian.confluence.impl.util.sandbox.SandboxPoolConfiguration;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.tenant.TenantRegistry;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.parsers.DefaultSafeModeCommandLineArgumentsFactory;
import com.atlassian.plugin.parsers.SafeModeCommandLineArgumentsFactory;
import io.atlassian.util.concurrent.ThreadFactories;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ConfluenceStartAnalyticsEventContextConfig {
    @Resource
    private EventPublisher eventPublisher;
    @Resource
    private TenantRegistry tenantRegistry;
    @Resource
    private PlatformTransactionManager transactionManager;
    @Resource
    private ClusterManager clusterManager;
    @Resource
    private LicenseService licenseService;
    @Resource
    private SystemInformationService systemInformationService;
    @Resource
    private SandboxPoolConfiguration conversionSandboxConfiguration;

    @Bean
    ConfluenceStartAnalyticsEventPublisher confluenceStartEventPublisher() {
        return new ConfluenceStartAnalyticsEventPublisher(this.eventPublisher, this.tenantRegistry, this.eventFactory(), this.confluenceStartEventScheduler());
    }

    private ConfluenceStartAnalyticsEventFactory eventFactory() {
        return new ConfluenceStartAnalyticsEventFactory(this.transactionManager, this.clusterManager, this.licenseService, (SafeModeCommandLineArgumentsFactory)new DefaultSafeModeCommandLineArgumentsFactory(), this.systemInformationService, this.conversionSandboxConfiguration);
    }

    @Bean(destroyMethod="shutdownNow")
    ExecutorService confluenceStartEventScheduler() {
        return Executors.newSingleThreadExecutor(ThreadFactories.namedThreadFactory((String)"StartEventPublisher", (ThreadFactories.Type)ThreadFactories.Type.DAEMON));
    }
}

