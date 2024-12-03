/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.scheduler.SchedulerService
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.migration.agent;

import com.atlassian.migration.agent.MapiBeanConfiguration;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.mapi.executor.CloudExecutorService;
import com.atlassian.migration.agent.rest.ContainerTokenValidator;
import com.atlassian.migration.agent.service.catalogue.EnterpriseGatekeeperClient;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.prc.PollerConfigHandler;
import com.atlassian.migration.agent.service.prc.PrcClientService;
import com.atlassian.migration.agent.service.prc.PrcCommandExecutor;
import com.atlassian.migration.agent.service.prc.PrcCommandExecutorCallback;
import com.atlassian.migration.agent.service.prc.PrcOkHttpAdapter;
import com.atlassian.migration.agent.service.prc.PrcPollerExecutionService;
import com.atlassian.migration.agent.service.prc.PrcPollerMetadataCache;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.scheduler.SchedulerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(value={MapiBeanConfiguration.class})
@Configuration
public class PrcBeanConfiguration {
    @Bean
    public PrcOkHttpAdapter prcOkHttpAdapter(EnterpriseGatekeeperClient enterpriseGatekeeperClient) {
        return new PrcOkHttpAdapter(enterpriseGatekeeperClient);
    }

    @Bean
    public PrcCommandExecutor prcCommandExecutor(CloudExecutorService cloudExecutorService) {
        return new PrcCommandExecutor(cloudExecutorService);
    }

    @Bean
    public PrcCommandExecutorCallback prcCommandExecutorCallback(PrcCommandExecutor prcCommandExecutor) {
        return new PrcCommandExecutorCallback(prcCommandExecutor);
    }

    @Bean
    public PollerConfigHandler pollerConfigHandler(PrcOkHttpAdapter prcOkHttpAdapter, PrcCommandExecutorCallback prcCommandExecutorCallback, MigrationAgentConfiguration migrationAgentConfiguration, PrcPollerMetadataCache prcPollerMetadataCache) {
        return new PollerConfigHandler(prcOkHttpAdapter, prcCommandExecutorCallback, migrationAgentConfiguration, prcPollerMetadataCache);
    }

    @Bean
    public PrcClientService prcClientService(PollerConfigHandler pollerConfigHandler, LicenseHandler licenseHandler, CloudSiteService cloudSiteService, PrcPollerExecutionService prcPollerExecutionService, ContainerTokenValidator containerTokenValidator, PrcPollerMetadataCache prcPollerMetadataCache) {
        return new PrcClientService(pollerConfigHandler, licenseHandler, cloudSiteService, prcPollerExecutionService, containerTokenValidator, prcPollerMetadataCache);
    }

    @Bean
    public PrcPollerExecutionService prcPollerExecutionService(SchedulerService schedulerService, PollerConfigHandler pollerConfigHandler) {
        return new PrcPollerExecutionService(schedulerService, pollerConfigHandler);
    }
}

