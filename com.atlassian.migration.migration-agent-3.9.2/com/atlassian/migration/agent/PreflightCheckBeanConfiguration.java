/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.scheduler.SchedulerService
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.migration.agent;

import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.ServiceBeanConfiguration;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.service.FeatureFlagService;
import com.atlassian.migration.agent.service.PlanService;
import com.atlassian.migration.agent.service.PluginInfoService;
import com.atlassian.migration.agent.service.TeamCalendarHelper;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.app.AppAssessmentInfoService;
import com.atlassian.migration.agent.service.catalogue.PlatformService;
import com.atlassian.migration.agent.service.check.AsyncCheckExecutor;
import com.atlassian.migration.agent.service.check.CheckOverrideService;
import com.atlassian.migration.agent.service.check.CheckRegistration;
import com.atlassian.migration.agent.service.check.CheckRegistry;
import com.atlassian.migration.agent.service.check.CheckResultFileManager;
import com.atlassian.migration.agent.service.check.CheckResultsService;
import com.atlassian.migration.agent.service.check.CheckTransformerService;
import com.atlassian.migration.agent.service.check.PreflightService;
import com.atlassian.migration.agent.service.check.StaleChecksCleaner;
import com.atlassian.migration.agent.service.check.app.teamcalendars.TcVersionCheckRegistration;
import com.atlassian.migration.agent.service.check.app.teamcalendars.TcVersionChecker;
import com.atlassian.migration.agent.service.check.app.teamcalendars.TcVersionContextProvider;
import com.atlassian.migration.agent.service.check.attachment.AttachmentPathService;
import com.atlassian.migration.agent.service.check.attachment.MissingAttachmentCheckRegistration;
import com.atlassian.migration.agent.service.check.attachment.MissingAttachmentChecker;
import com.atlassian.migration.agent.service.check.maintenance.MigrationOrchestratorMaintenanceCheckRegistration;
import com.atlassian.migration.agent.service.check.network.ConnectivityHttpUrlConnectionTester;
import com.atlassian.migration.agent.service.check.network.ConnectivityTester;
import com.atlassian.migration.agent.service.check.network.NetworkHealthCheckRegistration;
import com.atlassian.migration.agent.service.check.network.NetworkHealthChecker;
import com.atlassian.migration.agent.service.check.space.SpaceAnonymousPermissionCheckContextProvider;
import com.atlassian.migration.agent.service.check.space.SpaceAnonymousPermissionCheckRegistration;
import com.atlassian.migration.agent.service.check.space.SpaceAnonymousPermissionChecker;
import com.atlassian.migration.agent.service.check.space.SpaceConflictCheckContextProvider;
import com.atlassian.migration.agent.service.check.space.SpaceConflictCheckRegistration;
import com.atlassian.migration.agent.service.check.space.SpaceConflictChecker;
import com.atlassian.migration.agent.service.check.space.SpaceConflictMapper;
import com.atlassian.migration.agent.service.check.template.GlobalDataTemplateConflictCheckContextProvider;
import com.atlassian.migration.agent.service.check.template.GlobalDataTemplateConflictCheckRegistration;
import com.atlassian.migration.agent.service.check.template.GlobalDataTemplateConflictChecker;
import com.atlassian.migration.agent.service.check.template.GlobalDataTemplateConflictMapper;
import com.atlassian.migration.agent.service.check.token.ContainerTokenExpirationCheckRegistration;
import com.atlassian.migration.agent.service.check.version.AppOutdatedCheckRegistration;
import com.atlassian.migration.agent.service.check.version.ConfluenceSupportedVersionCheckContextProvider;
import com.atlassian.migration.agent.service.check.version.ConfluenceSupportedVersionCheckRegistration;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.confluence.ConfluenceCloudService;
import com.atlassian.migration.agent.service.email.GlobalEmailFixesConfigService;
import com.atlassian.migration.agent.service.email.InvalidEmailUserService;
import com.atlassian.migration.agent.service.email.UserBaseScanRunner;
import com.atlassian.migration.agent.service.impl.MigrationPlatformService;
import com.atlassian.migration.agent.service.impl.PlanDecoratorService;
import com.atlassian.migration.agent.service.impl.SpaceCatalogService;
import com.atlassian.migration.agent.service.mo.MigrationOrchestratorClient;
import com.atlassian.migration.agent.store.AttachmentStore;
import com.atlassian.migration.agent.store.impl.SpacePermissionStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.atlassian.migration.app.DefaultRegistrar;
import com.atlassian.scheduler.SchedulerService;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(value={ServiceBeanConfiguration.class})
@Configuration
public class PreflightCheckBeanConfiguration {
    @Bean
    public PlanDecoratorService planDecoratorService(PlanService planService, CheckTransformerService checkTransformerService, AsyncCheckExecutor checkExecutor, CheckRegistry checkRegistry, CheckOverrideService checkOverrideService, SpaceCatalogService spaceCatalogService, MigrationDarkFeaturesManager migrationDarkFeaturesManager) {
        return new PlanDecoratorService(planService, checkTransformerService, checkExecutor, checkRegistry, checkOverrideService, spaceCatalogService, migrationDarkFeaturesManager);
    }

    @Bean
    public CheckRegistry checkerRegistry(List<CheckRegistration> checkRegistrations, AnalyticsEventBuilder analyticsEventBuilder) {
        return new CheckRegistry(checkRegistrations, analyticsEventBuilder);
    }

    @Bean
    public AsyncCheckExecutor asyncCheckExecutor(SchedulerService schedulerService, CheckResultsService checkResultService, CheckRegistry checkerRegistry, PluginTransactionTemplate ptx, AnalyticsEventService analyticsEventService, MigrationDarkFeaturesManager migrationDarkFeaturesManager, InvalidEmailUserService invalidEmailUserService, GlobalEmailFixesConfigService globalEmailFixesConfigService, FeatureFlagService featureFlagService, UserBaseScanRunner userBaseScanRunner) {
        return new AsyncCheckExecutor(schedulerService, checkResultService, checkerRegistry, ptx, analyticsEventService, migrationDarkFeaturesManager, invalidEmailUserService, globalEmailFixesConfigService, featureFlagService, userBaseScanRunner);
    }

    @Bean
    public CheckTransformerService checkTransformerService(CheckRegistry checkRegistry, MigrationDarkFeaturesManager darkFeaturesManager, AppAssessmentInfoService appAssessmentInfoService, SystemInformationService systemInformationService, MigrationAgentConfiguration configuration, DefaultRegistrar defaultRegistrar, TeamCalendarHelper teamCalendarHelper) {
        return new CheckTransformerService(checkRegistry, darkFeaturesManager, appAssessmentInfoService, systemInformationService, configuration, defaultRegistrar, teamCalendarHelper);
    }

    @Bean
    public PreflightService preflightService(AsyncCheckExecutor checkExecutor, CheckTransformerService checkTransformerService, CheckResultsService checkResultService, CheckOverrideService checkOverrideService, PlanService planService) {
        return new PreflightService(checkExecutor, checkTransformerService, checkResultService, checkOverrideService, planService);
    }

    @Bean
    public StaleChecksCleaner staleChecksCleaner(SchedulerService schedulerService, CheckResultsService checkResultsService) {
        return new StaleChecksCleaner(schedulerService, checkResultsService);
    }

    @Bean
    public MissingAttachmentChecker missingAttachmentChecker(AttachmentStore attachmentStore, AttachmentPathService attachmentPathService, SystemInformationService systemInformationService, CheckResultFileManager checkResultFileManager, AttachmentManager attachmentManager) {
        return new MissingAttachmentChecker(attachmentStore, attachmentPathService, systemInformationService, checkResultFileManager, attachmentManager);
    }

    @Bean
    public NetworkHealthChecker networkHealthChecker(PlatformService platformService, ConnectivityTester connectivityTester, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, MigrationAgentConfiguration migrationAgentConfiguration) {
        return new NetworkHealthChecker(platformService, connectivityTester, analyticsEventService, analyticsEventBuilder, migrationAgentConfiguration);
    }

    @Bean
    public MissingAttachmentCheckRegistration missingAttachmentCheckRegistration(AnalyticsEventBuilder analyticsEventBuilder, AttachmentStore attachmentStore, AttachmentPathService attachmentPathService, SystemInformationService systemInformationService, CheckResultFileManager checkResultFileManager, AttachmentManager attachmentManager) {
        return new MissingAttachmentCheckRegistration(analyticsEventBuilder, attachmentStore, attachmentPathService, systemInformationService, checkResultFileManager, attachmentManager);
    }

    @Bean
    public SpaceAnonymousPermissionCheckContextProvider spaceAnonymousPermissionCheckContextProvider() {
        return new SpaceAnonymousPermissionCheckContextProvider();
    }

    @Bean
    public SpaceAnonymousPermissionCheckRegistration spaceAnonymousPermissionCheckRegistration(SpaceAnonymousPermissionCheckContextProvider provider, AnalyticsEventBuilder analyticsEventBuilder, SystemInformationService systemInformationService, SpacePermissionStore spacePermissionStore, CheckResultFileManager checkResultFileManager) {
        return new SpaceAnonymousPermissionCheckRegistration(provider, analyticsEventBuilder, systemInformationService, spacePermissionStore, checkResultFileManager);
    }

    @Bean
    public SpaceConflictCheckContextProvider spaceConflictCheckContextProvider() {
        return new SpaceConflictCheckContextProvider();
    }

    @Bean
    public SpaceConflictChecker spaceConflictChecker(CloudSiteService cloudSiteService, ConfluenceCloudService confluenceCloudService) {
        return new SpaceConflictChecker(cloudSiteService, confluenceCloudService);
    }

    @Bean
    public SpaceConflictCheckRegistration spaceConflictCheckRegistration(SpaceConflictCheckContextProvider provider, SpaceConflictChecker checker, SpaceConflictMapper spaceConflictMapper, AnalyticsEventBuilder analyticsEventBuilder) {
        return new SpaceConflictCheckRegistration(provider, checker, spaceConflictMapper, analyticsEventBuilder);
    }

    @Bean
    public SpaceConflictMapper spaceConflictMapper(SpaceManager spaceManager, SystemInformationService sysInfoService) {
        return new SpaceConflictMapper(spaceManager, sysInfoService);
    }

    @Bean
    public AppOutdatedCheckRegistration appOutdatedCheckRegistration(PluginInfoService pluginInfoService, SystemInformationService sysInfoService, AnalyticsEventBuilder analyticsEventBuilder) {
        return new AppOutdatedCheckRegistration(pluginInfoService, sysInfoService, analyticsEventBuilder);
    }

    @Bean
    public SpaceAnonymousPermissionChecker spaceAnonymousPermissionChecker(SpacePermissionStore spacePermissionStore, CheckResultFileManager checkResultFileManager) {
        return new SpaceAnonymousPermissionChecker(spacePermissionStore, checkResultFileManager);
    }

    @Bean
    public ConfluenceSupportedVersionCheckRegistration confluenceSupportedVersionCheckRegistration(AnalyticsEventBuilder analyticsEventBuilder, SystemInformationService systemInformationService, MigrationAgentConfiguration migrationAgentConfiguration) {
        return new ConfluenceSupportedVersionCheckRegistration(analyticsEventBuilder, systemInformationService, migrationAgentConfiguration);
    }

    @Bean
    public ConfluenceSupportedVersionCheckContextProvider confluenceSupportedVersionCheckContextProvider(SystemInformationService systemInformationService) {
        return new ConfluenceSupportedVersionCheckContextProvider(systemInformationService);
    }

    @Bean
    TcVersionContextProvider tcVersionContextProvider(SystemInformationService systemInformationService) {
        return new TcVersionContextProvider(systemInformationService);
    }

    @Bean
    public TcVersionChecker tcVersionChecker(MigrationAgentConfiguration configuration) {
        return new TcVersionChecker(configuration);
    }

    @Bean
    public TcVersionCheckRegistration tcVersionCheckRegistration(AnalyticsEventBuilder analyticsEventBuilder, TcVersionChecker checker, TcVersionContextProvider contextProvider, SystemInformationService sysInfoService) {
        return new TcVersionCheckRegistration(analyticsEventBuilder, checker, contextProvider, sysInfoService);
    }

    @Bean
    public ContainerTokenExpirationCheckRegistration containerTokenExpirationCheckRegistration(CloudSiteService cloudSiteService, MigrationPlatformService migrationPlatformService, AnalyticsEventBuilder analyticsEventBuilder) {
        return new ContainerTokenExpirationCheckRegistration(cloudSiteService, migrationPlatformService, analyticsEventBuilder);
    }

    @Bean
    public MigrationOrchestratorMaintenanceCheckRegistration migrationOrchestratorMaintenanceCheckRegistration(CloudSiteService cloudSiteService, MigrationOrchestratorClient migrationOrchestratorClient, AnalyticsEventBuilder analyticsEventBuilder) {
        return new MigrationOrchestratorMaintenanceCheckRegistration(cloudSiteService, migrationOrchestratorClient, analyticsEventBuilder);
    }

    @Bean
    public ConnectivityTester connectivityTester() {
        return new ConnectivityHttpUrlConnectionTester();
    }

    @Bean
    public NetworkHealthCheckRegistration networkHealthCheckRegistration(PlatformService platformService, AnalyticsEventBuilder analyticsEventBuilder, ConnectivityTester connectivityTester, AnalyticsEventService analyticsEventService, MigrationAgentConfiguration migrationAgentConfiguration) {
        return new NetworkHealthCheckRegistration(platformService, analyticsEventBuilder, connectivityTester, analyticsEventService, migrationAgentConfiguration);
    }

    @Bean
    public GlobalDataTemplateConflictChecker globalDataTemplateConflictChecker(ConfluenceCloudService confluenceCloudService, CheckResultFileManager checkResultFileManager) {
        return new GlobalDataTemplateConflictChecker(confluenceCloudService, checkResultFileManager);
    }

    @Bean
    public GlobalDataTemplateConflictCheckContextProvider globalDataTemplateConflictCheckContextProvider() {
        return new GlobalDataTemplateConflictCheckContextProvider();
    }

    @Bean
    public GlobalDataTemplateConflictMapper globalDataTemplateConflictMapper() {
        return new GlobalDataTemplateConflictMapper();
    }

    @Bean
    public GlobalDataTemplateConflictCheckRegistration globalDataTemplateConflictCheckRegistration(GlobalDataTemplateConflictCheckContextProvider checkContextProvider, GlobalDataTemplateConflictChecker checker, GlobalDataTemplateConflictMapper checkMapper, AnalyticsEventBuilder analyticsEventBuilder) {
        return new GlobalDataTemplateConflictCheckRegistration(checkContextProvider, checker, checkMapper, analyticsEventBuilder);
    }
}

