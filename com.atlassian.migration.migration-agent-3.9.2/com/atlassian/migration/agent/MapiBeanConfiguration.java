/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.scheduler.SchedulerService
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.migration.agent;

import com.atlassian.cache.CacheManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.migration.agent.PreflightCheckBeanConfiguration;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.mapi.executor.CloudExecutorService;
import com.atlassian.migration.agent.mapi.executor.CloudExecutorServiceImpl;
import com.atlassian.migration.agent.mapi.executor.MapiStatusConsumer;
import com.atlassian.migration.agent.mapi.executor.MapiStatusSenderService;
import com.atlassian.migration.agent.mapi.executor.MapiStatusTranslator;
import com.atlassian.migration.agent.mapi.external.MapiMigrationService;
import com.atlassian.migration.agent.mapi.job.JobValidationService;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.app.PluginManager;
import com.atlassian.migration.agent.service.catalogue.EnterpriseGatekeeperClient;
import com.atlassian.migration.agent.service.check.PreflightService;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.impl.AppAssessmentFacade;
import com.atlassian.migration.agent.service.impl.MapiPlanMappingService;
import com.atlassian.migration.agent.service.impl.MapiTaskMappingService;
import com.atlassian.migration.agent.service.impl.PlanDecoratorService;
import com.atlassian.migration.agent.service.prc.PrcPollerMetadataCache;
import com.atlassian.migration.agent.store.MapiPlanMappingStore;
import com.atlassian.migration.agent.store.MapiTaskMappingStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.scheduler.SchedulerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(value={PreflightCheckBeanConfiguration.class})
@Configuration
public class MapiBeanConfiguration {
    @Bean
    public MapiMigrationService mapiMigrationService(CloudSiteService cloudSiteService, EnterpriseGatekeeperClient enterpriseGatekeeperClient) {
        return new MapiMigrationService(cloudSiteService, enterpriseGatekeeperClient);
    }

    @Bean
    public JobValidationService jobValidationService(LicenseHandler licenseHandler) {
        return new JobValidationService(licenseHandler);
    }

    @Bean
    public MapiPlanMappingService mapiPlanMappingService(PluginTransactionTemplate pluginTransactionTemplate, MapiPlanMappingStore mapiPlanMappingStore) {
        return new MapiPlanMappingService(pluginTransactionTemplate, mapiPlanMappingStore);
    }

    @Bean
    PrcPollerMetadataCache prcPollerMetadataCache(CacheManager cacheManager, CloudSiteService cloudSiteService) {
        return new PrcPollerMetadataCache(cacheManager, cloudSiteService);
    }

    @Bean
    public MapiTaskMappingService mapiTaskMappingService(PluginTransactionTemplate pluginTransactionTemplate, MapiTaskMappingStore mapiTaskMappingStore) {
        return new MapiTaskMappingService(pluginTransactionTemplate, mapiTaskMappingStore);
    }

    @Bean
    public MapiStatusTranslator mapiStatusTranslator(PreflightService preflightService) {
        return new MapiStatusTranslator(preflightService);
    }

    @Bean
    public MapiStatusSenderService mapiStatusSenderService(MapiStatusTranslator mapiStatusTranslator, MapiMigrationService mapiMigrationService, MapiTaskMappingService mapiTaskMappingService) {
        return new MapiStatusSenderService(mapiStatusTranslator, mapiMigrationService, mapiTaskMappingService);
    }

    @Bean
    public MapiStatusConsumer mapiStatusConsumer(MapiTaskMappingService mapiTaskMappingService, SchedulerService schedulerService, MapiStatusSenderService mapiStatusSenderService, MigrationAgentConfiguration agentConfiguration) {
        return new MapiStatusConsumer(mapiTaskMappingService, schedulerService, mapiStatusSenderService, agentConfiguration);
    }

    @Bean
    public CloudExecutorService cloudExecutorService(PlanDecoratorService planDecoratorService, MapiMigrationService mapiMigrationService, JobValidationService jobValidationService, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, AppAssessmentFacade appAssessmentFacade, MapiPlanMappingService mapiPlanMappingService, MapiTaskMappingService mapiTaskMappingService, SpaceManager spaceManager, PluginManager pluginManager, PreflightService preflightService, PrcPollerMetadataCache prcPollerMetadataCache) {
        return new CloudExecutorServiceImpl(planDecoratorService, mapiMigrationService, jobValidationService, analyticsEventService, analyticsEventBuilder, appAssessmentFacade, mapiPlanMappingService, mapiTaskMappingService, spaceManager, pluginManager, preflightService, prcPollerMetadataCache);
    }
}

