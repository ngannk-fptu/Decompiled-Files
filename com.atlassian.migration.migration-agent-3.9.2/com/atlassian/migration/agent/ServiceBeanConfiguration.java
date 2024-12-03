/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.services.AnalyticsConfigService
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.confluence.api.service.search.CQLSearchService
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.content.service.SpaceService
 *  com.atlassian.confluence.macro.browser.MacroMetadataManager
 *  com.atlassian.confluence.pages.templates.PageTemplateManager
 *  com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewedManager
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.migration.app.dto.check.AppVendorCheckProperties
 *  com.atlassian.plugin.metadata.PluginMetadataManager
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.user.GroupManager
 *  org.osgi.framework.BundleContext
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.migration.agent;

import com.atlassian.analytics.api.services.AnalyticsConfigService;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.cache.CacheManager;
import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.api.service.search.CQLSearchService;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.content.service.SpaceService;
import com.atlassian.confluence.macro.browser.MacroMetadataManager;
import com.atlassian.confluence.pages.templates.PageTemplateManager;
import com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewedManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.GuardrailsBeanConfiguration;
import com.atlassian.migration.agent.MediaBeanConfiguration;
import com.atlassian.migration.agent.PlanningEngineBeanConfiguration;
import com.atlassian.migration.agent.StoreBeanConfiguration;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.media.MediaFileUploaderFactory;
import com.atlassian.migration.agent.mma.service.MigrationMetadataAggregatorService;
import com.atlassian.migration.agent.mma.service.SpaceMetadataIntervalEmitter;
import com.atlassian.migration.agent.newexport.store.JdbcConfluenceStore;
import com.atlassian.migration.agent.okhttp.OKHttpProxyBuilder;
import com.atlassian.migration.agent.rest.ContainerTokenValidator;
import com.atlassian.migration.agent.service.ClusterInformationService;
import com.atlassian.migration.agent.service.ClusterLimits;
import com.atlassian.migration.agent.service.FeatureFlagService;
import com.atlassian.migration.agent.service.FrontEndService;
import com.atlassian.migration.agent.service.LoggingContextProvider;
import com.atlassian.migration.agent.service.MigrationMappingService;
import com.atlassian.migration.agent.service.NetworkStatisticsService;
import com.atlassian.migration.agent.service.ObjectStorageService;
import com.atlassian.migration.agent.service.PlanService;
import com.atlassian.migration.agent.service.S3ObjectStorageService;
import com.atlassian.migration.agent.service.StatisticsService;
import com.atlassian.migration.agent.service.StatsStoringService;
import com.atlassian.migration.agent.service.TeamCalendarHelper;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventConsumer;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.analytics.AnalyticsSenderService;
import com.atlassian.migration.agent.service.analytics.AppAssessmentAnalyticsEventService;
import com.atlassian.migration.agent.service.analytics.DefaultAppAnalyticsEventService;
import com.atlassian.migration.agent.service.app.AppAccessScopeService;
import com.atlassian.migration.agent.service.app.AppAssessmentInfoService;
import com.atlassian.migration.agent.service.app.PluginManager;
import com.atlassian.migration.agent.service.catalogue.EnterpriseGatekeeperClient;
import com.atlassian.migration.agent.service.catalogue.MigrationCatalogueStorageService;
import com.atlassian.migration.agent.service.catalogue.PlatformService;
import com.atlassian.migration.agent.service.check.CheckOverrideService;
import com.atlassian.migration.agent.service.check.CheckResultFileManager;
import com.atlassian.migration.agent.service.check.CheckResultsService;
import com.atlassian.migration.agent.service.check.attachment.AttachmentPathService;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.cloud.CloudSiteSetupService;
import com.atlassian.migration.agent.service.cloud.LegalService;
import com.atlassian.migration.agent.service.cloud.NonceService;
import com.atlassian.migration.agent.service.confluence.ConfluenceCloudService;
import com.atlassian.migration.agent.service.email.BlockedDomainUmsService;
import com.atlassian.migration.agent.service.email.InvalidEmailUserService;
import com.atlassian.migration.agent.service.email.InvalidEmailValidator;
import com.atlassian.migration.agent.service.email.MostFrequentDomainService;
import com.atlassian.migration.agent.service.email.NewEmailSuggestingService;
import com.atlassian.migration.agent.service.encryption.EncryptionConfigHandler;
import com.atlassian.migration.agent.service.encryption.EncryptionSecretManager;
import com.atlassian.migration.agent.service.encryption.EncryptionService;
import com.atlassian.migration.agent.service.execution.PlanExecutionService;
import com.atlassian.migration.agent.service.execution.StepExecutionService;
import com.atlassian.migration.agent.service.extract.ExtractionAnalyticsService;
import com.atlassian.migration.agent.service.extract.GlobalEntityExtractionService;
import com.atlassian.migration.agent.service.extract.GlobalEntityExtractionServiceImpl;
import com.atlassian.migration.agent.service.extract.GroupExtractionService;
import com.atlassian.migration.agent.service.extract.GroupExtractionServiceImpl;
import com.atlassian.migration.agent.service.extract.SpacePermissionExtractionService;
import com.atlassian.migration.agent.service.extract.UserExtractionService;
import com.atlassian.migration.agent.service.extract.UserExtractionServiceImpl;
import com.atlassian.migration.agent.service.extract.UserGroupExtractFacade;
import com.atlassian.migration.agent.service.guardrails.AssessmentJobProgressService;
import com.atlassian.migration.agent.service.guardrails.BrowserMetricsService;
import com.atlassian.migration.agent.service.guardrails.InstanceAnalysisControlService;
import com.atlassian.migration.agent.service.guardrails.InstanceAssessmentCSVService;
import com.atlassian.migration.agent.service.guardrails.InstanceAssessmentService;
import com.atlassian.migration.agent.service.guardrails.InstanceMetadataCollector;
import com.atlassian.migration.agent.service.guardrails.logs.PageTypeProvider;
import com.atlassian.migration.agent.service.guardrails.logs.TomcatAccessLogParser;
import com.atlassian.migration.agent.service.guardrails.logs.TomcatAccessLogsFinder;
import com.atlassian.migration.agent.service.guardrails.macro.MacroAssessmentService;
import com.atlassian.migration.agent.service.guardrails.usage.AccessLogProcessingJobRunner;
import com.atlassian.migration.agent.service.guardrails.usage.DailyUsageMetricsStore;
import com.atlassian.migration.agent.service.impl.AppAssessmentFacade;
import com.atlassian.migration.agent.service.impl.AppMigrationDarkFeaturesImpl;
import com.atlassian.migration.agent.service.impl.AppUsageService;
import com.atlassian.migration.agent.service.impl.BlockedDomainService;
import com.atlassian.migration.agent.service.impl.CloudTypeSettingsService;
import com.atlassian.migration.agent.service.impl.ConcurrencySettingsService;
import com.atlassian.migration.agent.service.impl.CsvWriterService;
import com.atlassian.migration.agent.service.impl.DefaultAttachmentService;
import com.atlassian.migration.agent.service.impl.DefaultExportDirManager;
import com.atlassian.migration.agent.service.impl.DefaultFrontEndService;
import com.atlassian.migration.agent.service.impl.DefaultInitialStateService;
import com.atlassian.migration.agent.service.impl.DefaultPlanService;
import com.atlassian.migration.agent.service.impl.DefaultPluginInfoService;
import com.atlassian.migration.agent.service.impl.DefaultStatisticsService;
import com.atlassian.migration.agent.service.impl.DefaultStatsStoringService;
import com.atlassian.migration.agent.service.impl.MapiPlanMappingService;
import com.atlassian.migration.agent.service.impl.MapiTaskMappingService;
import com.atlassian.migration.agent.service.impl.MigrationAppAggregatorService;
import com.atlassian.migration.agent.service.impl.MigrationPlatformService;
import com.atlassian.migration.agent.service.impl.MigrationSettingsService;
import com.atlassian.migration.agent.service.impl.MigrationTimeEstimationUtils;
import com.atlassian.migration.agent.service.impl.PlanConverter;
import com.atlassian.migration.agent.service.impl.PlanDecoratorService;
import com.atlassian.migration.agent.service.impl.SENSupplier;
import com.atlassian.migration.agent.service.impl.SpaceCatalogService;
import com.atlassian.migration.agent.service.impl.SpaceStatisticCalculationAnalyticService;
import com.atlassian.migration.agent.service.impl.SpaceStatisticCalculationInitialExecutor;
import com.atlassian.migration.agent.service.impl.SpaceStatisticCalculationIntervalExecutor;
import com.atlassian.migration.agent.service.impl.SpaceStatisticCalculationService;
import com.atlassian.migration.agent.service.impl.TrustedDomainCsvReaderService;
import com.atlassian.migration.agent.service.impl.TrustedDomainCsvWriterService;
import com.atlassian.migration.agent.service.impl.UserAgentInterceptor;
import com.atlassian.migration.agent.service.impl.UserDomainService;
import com.atlassian.migration.agent.service.impl.UserService;
import com.atlassian.migration.agent.service.log.MigrationLogDirManager;
import com.atlassian.migration.agent.service.log.MigrationLogService;
import com.atlassian.migration.agent.service.planning.StepPlanningEngine;
import com.atlassian.migration.agent.service.planning.TaskPlanningEngine;
import com.atlassian.migration.agent.service.portfolioanalyzer.service.RelationsAnalyzerRunner;
import com.atlassian.migration.agent.service.portfolioanalyzer.service.RelationsAnalyzerService;
import com.atlassian.migration.agent.service.portfolioanalyzer.service.SpaceKeyResolver;
import com.atlassian.migration.agent.service.portfolioanalyzer.service.WarnLogFileWriter;
import com.atlassian.migration.agent.service.recentlyviewed.LegacyRecentlyViewedService;
import com.atlassian.migration.agent.service.recentlyviewed.RecentlyViewedManagerWrapper;
import com.atlassian.migration.agent.service.recentlyviewed.RecentlyViewedServiceLocator;
import com.atlassian.migration.agent.service.status.MigrationStatusService;
import com.atlassian.migration.agent.service.stepexecutor.ProgressTracker;
import com.atlassian.migration.agent.service.stepexecutor.attachment.AttachmentMigrationExecutor;
import com.atlassian.migration.agent.service.stepexecutor.globalentities.GlobalEntitiesExportExecutor;
import com.atlassian.migration.agent.service.stepexecutor.globalentities.GlobalEntitiesImportExecutor;
import com.atlassian.migration.agent.service.stepexecutor.globalentities.GlobalEntitiesUploadExecutor;
import com.atlassian.migration.agent.service.stepexecutor.space.SpaceExportExecutor;
import com.atlassian.migration.agent.service.stepexecutor.space.SpaceImportExecutor;
import com.atlassian.migration.agent.service.stepexecutor.space.SpaceUploadExecutor;
import com.atlassian.migration.agent.service.stepexecutor.space.SpaceUsersMigrationExecutor;
import com.atlassian.migration.agent.service.stepexecutor.space.helper.SpaceImportConfigFileManager;
import com.atlassian.migration.agent.service.stepexecutor.user.UsersMigrationExecutor;
import com.atlassian.migration.agent.service.user.UserMappingsFileManager;
import com.atlassian.migration.agent.service.user.UsersGroupsMigrationFileManager;
import com.atlassian.migration.agent.service.user.UsersToTombstoneFileManager;
import com.atlassian.migration.agent.service.version.PluginVersionManager;
import com.atlassian.migration.agent.store.AttachmentMigrationStore;
import com.atlassian.migration.agent.store.AttachmentStore;
import com.atlassian.migration.agent.store.CloudSiteStore;
import com.atlassian.migration.agent.store.ConfluenceSpaceTaskStore;
import com.atlassian.migration.agent.store.ContentStatisticsStore;
import com.atlassian.migration.agent.store.InvalidEmailUserStore;
import com.atlassian.migration.agent.store.PlanStore;
import com.atlassian.migration.agent.store.SpaceStatisticStore;
import com.atlassian.migration.agent.store.StatsStore;
import com.atlassian.migration.agent.store.StepProgressPropertiesStore;
import com.atlassian.migration.agent.store.StepStore;
import com.atlassian.migration.agent.store.TaskStore;
import com.atlassian.migration.agent.store.UserDomainRuleStore;
import com.atlassian.migration.agent.store.UserMappingStore;
import com.atlassian.migration.agent.store.guardrails.AssessmentQuery;
import com.atlassian.migration.agent.store.guardrails.GuardrailsBrowserMetricsStore;
import com.atlassian.migration.agent.store.guardrails.GuardrailsResponseGroupStore;
import com.atlassian.migration.agent.store.guardrails.GuardrailsResponseStore;
import com.atlassian.migration.agent.store.guardrails.InstanceAnalysisControlStore;
import com.atlassian.migration.agent.store.impl.AnalyticsEventStore;
import com.atlassian.migration.agent.store.impl.AppAccessScopeStore;
import com.atlassian.migration.agent.store.impl.AppAssessmentInfoStore;
import com.atlassian.migration.agent.store.impl.CheckOverrideStore;
import com.atlassian.migration.agent.store.impl.CheckResultStore;
import com.atlassian.migration.agent.store.impl.RecentlyViewedStore;
import com.atlassian.migration.agent.store.impl.SpacePermissionStore;
import com.atlassian.migration.agent.store.impl.SpaceStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import com.atlassian.migration.agent.store.jpa.impl.ConfluenceWrapperDataSource;
import com.atlassian.migration.agent.store.jpa.impl.DialectResolver;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.atlassian.migration.app.AppAssessmentClient;
import com.atlassian.migration.app.AppMigrationDarkFeatures;
import com.atlassian.migration.app.AppPreflightExecutorImpl;
import com.atlassian.migration.app.DefaultAppMigrationServiceClient;
import com.atlassian.migration.app.DefaultRegistrar;
import com.atlassian.migration.app.MigrationAppAggregatorClient;
import com.atlassian.migration.app.OsgiBundleHelper;
import com.atlassian.migration.app.dto.check.AppVendorCheckProperties;
import com.atlassian.plugin.metadata.PluginMetadataManager;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.user.GroupManager;
import java.util.List;
import org.osgi.framework.BundleContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(value={StoreBeanConfiguration.class, MediaBeanConfiguration.class, PlanningEngineBeanConfiguration.class, GuardrailsBeanConfiguration.class})
@Configuration
public class ServiceBeanConfiguration {
    @Bean
    public EnterpriseGatekeeperClient enterpriseGatekeeperClient(MigrationAgentConfiguration configuration, UserAgentInterceptor userAgentInterceptor, CloudSiteService cloudSiteService, OKHttpProxyBuilder okHttpProxyBuilder, MigrationDarkFeaturesManager darkFeaturesManager) {
        return new EnterpriseGatekeeperClient(configuration, userAgentInterceptor, cloudSiteService, okHttpProxyBuilder, darkFeaturesManager);
    }

    @Bean
    public CloudSiteService cloudSiteService(PluginTransactionTemplate ptx, CloudSiteStore cloudSiteStore, AttachmentMigrationStore attachmentMigrationStore, PlanStore planStore, MigrationDarkFeaturesManager migrationDarkFeaturesManager) {
        return new CloudSiteService(ptx, cloudSiteStore, attachmentMigrationStore, planStore, migrationDarkFeaturesManager);
    }

    @Bean
    public DefaultAppMigrationServiceClient appAssessmentClient(MigrationAgentConfiguration configuration, UserAgentInterceptor userAgentInterceptor, CloudSiteService cloudSiteService, OKHttpProxyBuilder okHttpProxyBuilder) {
        return new DefaultAppMigrationServiceClient(configuration, userAgentInterceptor, cloudSiteService, okHttpProxyBuilder);
    }

    @Bean
    public UsersGroupsMigrationFileManager usersGroupsMigrationFileManager(BootstrapManager bootstrapManager) {
        return new UsersGroupsMigrationFileManager(bootstrapManager);
    }

    @Bean
    public DefaultRegistrar defaultRegistrar(DefaultAppMigrationServiceClient appMigrationServiceClient, SchedulerService schedulerService, BundleContext bundleContext, DefaultAppAnalyticsEventService defaultAppAnalyticsEventService, AppMigrationDarkFeatures appMigrationDarkFeatures) {
        return new DefaultRegistrar(appMigrationServiceClient, schedulerService, bundleContext, defaultAppAnalyticsEventService, appMigrationDarkFeatures);
    }

    @Bean
    public OsgiBundleHelper osgiBundleHelper(BundleContext bundleContext, AppMigrationDarkFeaturesImpl appMigrationDarkFeatures) {
        return new OsgiBundleHelper(bundleContext, appMigrationDarkFeatures);
    }

    @Bean
    public AppVendorCheckProperties appVendorCheckProperties() {
        return new AppVendorCheckProperties(0, 0, 0L);
    }

    @Bean
    public AppPreflightExecutorImpl appPreflightExecutor(OsgiBundleHelper osgiBundleHelper, MigrationAppAggregatorClient migrationAppAggregatorService, AppMigrationDarkFeatures appMigrationDarkFeatures, AppVendorCheckProperties appVendorCheckProperties) {
        return new AppPreflightExecutorImpl(osgiBundleHelper, migrationAppAggregatorService, appMigrationDarkFeatures, appVendorCheckProperties);
    }

    @Bean
    public AppMigrationDarkFeaturesImpl appMigrationFeatures(MigrationDarkFeaturesManager migrationDarkFeaturesManager) {
        return new AppMigrationDarkFeaturesImpl(migrationDarkFeaturesManager);
    }

    @Bean
    public NetworkStatisticsService networkStatisticsService(StatsStoringService statsStoringService, MediaFileUploaderFactory mediaFileUploaderFactory, CloudSiteService cloudSiteService) {
        return new NetworkStatisticsService(statsStoringService, mediaFileUploaderFactory, cloudSiteService);
    }

    @Bean
    public LegalService legalService(MigrationAgentConfiguration migrationAgentConfiguration, PluginSettingsFactory pluginSettingsFactory) {
        return new LegalService(migrationAgentConfiguration, pluginSettingsFactory);
    }

    @Bean
    public DefaultAttachmentService defaultAttachmentService(PluginTransactionTemplate ptx, AttachmentMigrationStore attachmentMigrationStore, AttachmentStore attachmentStore) {
        return new DefaultAttachmentService(ptx, attachmentMigrationStore, attachmentStore);
    }

    @Bean
    public FrontEndService defaultFrontEndService(ApplicationProperties applicationProperties, MigrationAgentConfiguration configuration) {
        return new DefaultFrontEndService(applicationProperties, configuration);
    }

    @Bean
    public DefaultInitialStateService defaultInitialStateService(PlanService planService) {
        return new DefaultInitialStateService(planService);
    }

    @Bean
    public SpaceCatalogService spaceCatalogService(PluginTransactionTemplate ptx, SpaceStore spaceStore, ConfluenceSpaceTaskStore confluenceSpaceTaskStore, StatisticsService statisticsService, MigrationDarkFeaturesManager migrationDarkFeaturesManager, MigrationTimeEstimationUtils migrationTimeEstimationUtils) {
        return new SpaceCatalogService(ptx, spaceStore, confluenceSpaceTaskStore, statisticsService, migrationDarkFeaturesManager, migrationTimeEstimationUtils);
    }

    @Bean
    public UserService userService(UserAccessor userAccessor, InvalidEmailUserService invalidEmailUserService, UsersGroupsMigrationFileManager usersGroupsMigrationFileManager, MigrationCatalogueStorageService migrationCatalogueStorageService, UserMappingStore userMappingStore) {
        return new UserService(userAccessor, invalidEmailUserService, usersGroupsMigrationFileManager, migrationCatalogueStorageService, userMappingStore);
    }

    @Bean
    public UserExtractionService userExtractionService(ConfluenceWrapperDataSource dataSource, ExtractionAnalyticsService extractionAnalyticsService, SpacePermissionStore spacePermissionStore) {
        return new UserExtractionServiceImpl(dataSource, extractionAnalyticsService, spacePermissionStore);
    }

    @Bean
    public GroupExtractionService groupExtractionService(SpaceManager spaceManager, SpacePermissionManager spacePermissionsManager, TransactionTemplate transactionTemplate, ExtractionAnalyticsService extractionAnalyticsService) {
        return new GroupExtractionServiceImpl(spaceManager, spacePermissionsManager, transactionTemplate, extractionAnalyticsService);
    }

    @Bean
    public UserGroupExtractFacade userGroupExtractFacade(UserService userService, GroupManager groupManager, UserExtractionService userExtractionService, GroupExtractionService groupExtractionService) {
        return new UserGroupExtractFacade(userService, groupManager, userExtractionService, groupExtractionService);
    }

    @Bean
    public GlobalEntityExtractionService globalEntityExtractionService(ConfluenceWrapperDataSource dataSource) {
        return new GlobalEntityExtractionServiceImpl(dataSource);
    }

    @Bean
    public StatisticsService statisticsService(SystemInformationService systemInformationService, StatsStoringService statsStoringService, ContentStatisticsStore contentStatisticsStore, PluginTransactionTemplate ptx, SpaceManager spaceManager, NetworkStatisticsService networkStatisticsService, LegalService legalService, UserGroupExtractFacade userGroupExtractFacade, StepStore stepStore, GlobalEntityExtractionService globalEntityExtractionService, MigrationTimeEstimationUtils migrationTimeEstimationUtils, EventPublisher eventPublisher, MigrationAgentConfiguration migrationAgentConfiguration) {
        return new DefaultStatisticsService(systemInformationService, statsStoringService, contentStatisticsStore, ptx, spaceManager, networkStatisticsService, legalService, userGroupExtractFacade, stepStore, globalEntityExtractionService, migrationTimeEstimationUtils, eventPublisher, migrationAgentConfiguration);
    }

    @Bean
    public StatsStoringService statsStoringService(PluginTransactionTemplate ptx, StatsStore statsStore) {
        return new DefaultStatsStoringService(ptx, statsStore);
    }

    @Bean
    public InvalidEmailUserService invalidEmailUserService(InvalidEmailUserStore invalidEmailUserStore, PluginTransactionTemplate ptx, EventPublisher eventPublisher) {
        return new InvalidEmailUserService(invalidEmailUserStore, ptx, eventPublisher);
    }

    @Bean
    public PlatformService platformService(SENSupplier senSupplier, LicenseHandler licenseHandler, SystemInformationService systemInformationService, SpaceManager spaceManager, EnterpriseGatekeeperClient enterpriseGatekeeperClient, DefaultRegistrar defaultRegistrar, MigrationAppAggregatorService appAggregatorService, PluginManager pluginManager, PluginVersionManager pluginVersionManager, MigrationDarkFeaturesManager darkFeaturesManager, FeatureFlagService featureFlagService, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, MapiTaskMappingService mapiTaskMappingService, MapiPlanMappingService mapiPlanMappingService) {
        return new PlatformService(senSupplier, licenseHandler, systemInformationService, spaceManager, enterpriseGatekeeperClient, defaultRegistrar, appAggregatorService, pluginManager, pluginVersionManager, darkFeaturesManager, featureFlagService, analyticsEventService, analyticsEventBuilder, mapiTaskMappingService, mapiPlanMappingService);
    }

    @Bean
    public CheckResultFileManager checkResultFileManager(BootstrapManager bootstrapManager) {
        return new CheckResultFileManager(bootstrapManager);
    }

    @Bean
    public CheckResultsService checkResultService(CheckResultStore checkResultStore, CheckResultFileManager checkResultFileManager, PluginTransactionTemplate ptx) {
        return new CheckResultsService(checkResultStore, checkResultFileManager, ptx);
    }

    @Bean
    public RecentlyViewedManagerWrapper recentlyViewedManagerWrapper(RecentlyViewedManager recentlyViewedManager) {
        return new RecentlyViewedManagerWrapper(recentlyViewedManager);
    }

    @Bean
    public LegacyRecentlyViewedService legacyRecentlyViewedService(RecentlyViewedStore recentlyViewedStore) {
        return new LegacyRecentlyViewedService(recentlyViewedStore);
    }

    @Bean
    public RecentlyViewedServiceLocator recentlyViewedServiceLocator(SystemInformationService systemInformationService, RecentlyViewedManagerWrapper recentlyViewedManagerWrapper, LegacyRecentlyViewedService legacyRecentlyViewedService) {
        return new RecentlyViewedServiceLocator(systemInformationService, recentlyViewedManagerWrapper, legacyRecentlyViewedService);
    }

    @Bean
    public AppUsageService appUsageService(MacroMetadataManager macroMetadataManager, CQLSearchService cqlSearchService, PluginManager pluginManager, CacheManager cacheManager, RecentlyViewedServiceLocator recentlyViewedServiceLocator, MigrationAgentConfiguration migrationAgentConfiguration, AppAssessmentAnalyticsEventService appAssessmentAnalyticsEventService, ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory) {
        return new AppUsageService(macroMetadataManager, cqlSearchService, pluginManager, cacheManager, recentlyViewedServiceLocator, migrationAgentConfiguration, appAssessmentAnalyticsEventService, threadLocalDelegateExecutorFactory);
    }

    @Bean
    public AppAccessScopeService appAccessScopeService(DefaultRegistrar cloudMigrationRegistrar, AppAccessScopeStore appAccessScopeStore, PluginTransactionTemplate ptx) {
        return new AppAccessScopeService(cloudMigrationRegistrar, appAccessScopeStore, ptx);
    }

    @Bean
    public AppAssessmentInfoService appAssessmentInfoService(AppAssessmentInfoStore appAssessmentInfoStore, PluginManager pluginManager, PluginTransactionTemplate ptx) {
        return new AppAssessmentInfoService(appAssessmentInfoStore, pluginManager, ptx);
    }

    @Bean
    public AppAssessmentFacade appsAssessmentService(MigrationAppAggregatorService appAggregatorService, PluginManager pluginManager, AppAssessmentAnalyticsEventService appAssessmentAnalyticsEventService, AppUsageService appUsageService, AppAssessmentClient appAssessmentClient, CloudSiteService cloudSiteService, AppAccessScopeService appAccessScopeService, AppAssessmentInfoService appAssessmentInfoService, SystemInformationService systemInformationService) {
        return new AppAssessmentFacade(appAggregatorService, pluginManager, appAssessmentAnalyticsEventService, appUsageService, appAssessmentClient, cloudSiteService, appAccessScopeService, appAssessmentInfoService, systemInformationService);
    }

    @Bean
    public PlanService planService(PluginTransactionTemplate ptx, PlanStore planStore, TaskStore taskStore, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, PlanConverter planConverter, CheckResultsService checkResultsService, AppAssessmentAnalyticsEventService appAssessmentAnalyticsEventService, PlatformService platformService, CheckOverrideService checkOverrideService, MigrationDarkFeaturesManager darkFeaturesManager, AppAssessmentFacade appAssessmentFacade, ProgressTracker progressTracker, PlanExecutionService planExecutionService) {
        return new DefaultPlanService(ptx, planStore, taskStore, analyticsEventService, analyticsEventBuilder, planConverter, checkResultsService, appAssessmentAnalyticsEventService, platformService, checkOverrideService, darkFeaturesManager, appAssessmentFacade, progressTracker, planExecutionService);
    }

    @Bean
    public DefaultPluginInfoService defaultPluginInfoService(PluginVersionManager pluginVersionManager, PlanService planService) {
        return new DefaultPluginInfoService(pluginVersionManager, planService);
    }

    @Bean
    public MigrationCatalogueStorageService migrationCatalogueStorageService(EnterpriseGatekeeperClient enterpriseGatekeeperClient) {
        return new MigrationCatalogueStorageService(enterpriseGatekeeperClient);
    }

    @Bean
    public MigrationMappingService migrationMappingService(EnterpriseGatekeeperClient enterpriseGatekeeperClient) {
        return new MigrationMappingService(enterpriseGatekeeperClient);
    }

    @Bean
    public PlanConverter planConverter(CloudSiteService cloudSiteService, CheckOverrideService checkOverrideService, StatisticsService statisticsService, SpaceStore spaceStore, TaskStore taskStore, StepStore stepStore, MigrationTimeEstimationUtils migrationTimeEstimationUtils) {
        return new PlanConverter(cloudSiteService, checkOverrideService, statisticsService, spaceStore, taskStore, stepStore, migrationTimeEstimationUtils);
    }

    @Bean
    public CheckOverrideService checkOverrideService(CheckOverrideStore checkOverrideStore, PluginTransactionTemplate ptx) {
        return new CheckOverrideService(checkOverrideStore, ptx);
    }

    @Bean
    public ProgressTracker progressTracker(PluginTransactionTemplate ptx, List<StepPlanningEngine<?>> planningEngines, StepStore stepStore, TaskStore taskStore, PlanStore planStore, TaskPlanningEngine taskPlanningEngine, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, PlanConverter planConverter, MigrationLogService migrationLogService, MapiTaskMappingService mapiTaskMappingService, PlatformService platformService, StepProgressPropertiesStore stepProgressPropertiesStore) {
        return new ProgressTracker(ptx, planningEngines, stepStore, taskStore, planStore, taskPlanningEngine, analyticsEventService, analyticsEventBuilder, planConverter, migrationLogService, mapiTaskMappingService, platformService, stepProgressPropertiesStore);
    }

    @Bean
    public MigrationLogService migrationLogService(MigrationLogDirManager migrationLogDirManager, MigrationCatalogueStorageService migrationCatalogueStorageService, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, EventPublisher eventPublisher) {
        return new MigrationLogService(migrationLogDirManager, migrationCatalogueStorageService, analyticsEventService, analyticsEventBuilder, eventPublisher);
    }

    @Bean
    public AnalyticsSenderService analyticsSenderService(MigrationAgentConfiguration configuration, UserAgentInterceptor userAgentInterceptor, OKHttpProxyBuilder okHttpProxyBuilder) {
        return new AnalyticsSenderService(configuration, userAgentInterceptor, okHttpProxyBuilder);
    }

    @Bean
    public AnalyticsEventService analyticsEventService(PluginTransactionTemplate ptx, AnalyticsEventStore analyticsEventStore, CloudSiteService cloudSiteService, AnalyticsSenderService analyticsSenderService) {
        return new AnalyticsEventService(ptx, analyticsEventStore, cloudSiteService, analyticsSenderService);
    }

    @Bean
    public AppAssessmentAnalyticsEventService appAssessmentAnalyticsEventService(AppAssessmentInfoStore appAssessmentInfoStore, AnalyticsEventService analyticsEventService, SENSupplier senSupplier, MigrationAppAggregatorService aggregatorService, PluginManager pluginManager, DefaultRegistrar cloudMigrationRegistrar) {
        return new AppAssessmentAnalyticsEventService(appAssessmentInfoStore, analyticsEventService, senSupplier, aggregatorService, pluginManager, cloudMigrationRegistrar);
    }

    @Bean
    public AnalyticsEventBuilder analyticsEventBuilder(SENSupplier senSupplier, StatisticsService statisticsService, SystemInformationService systemInformationService, PluginMetadataManager pluginMetadataManager, CrowdDirectoryService crowdDirectoryService, SpaceManager spaceManager, PluginVersionManager pluginVersionManager, ClusterManager clusterManager, LicenseHandler licenseHandler, MigrationAgentConfiguration migrationAgentConfiguration, MigrationTimeEstimationUtils migrationTimeEstimationUtils, PluginManager pluginManager, EncryptionConfigHandler encryptionConfigHandler) {
        return new AnalyticsEventBuilder(senSupplier, statisticsService, systemInformationService, pluginMetadataManager, crowdDirectoryService, spaceManager, pluginVersionManager, clusterManager, licenseHandler, migrationAgentConfiguration, migrationTimeEstimationUtils, pluginManager, encryptionConfigHandler);
    }

    @Bean
    public AnalyticsEventConsumer analyticsEventConsumer(PluginTransactionTemplate ptx, AnalyticsEventStore analyticsEventStore, SchedulerService schedulerService, AnalyticsSenderService analyticsSenderService, MigrationAgentConfiguration agentConfiguration, CloudSiteService cloudSiteService, LegalService legalService, AnalyticsConfigService analyticsConfigService) {
        return new AnalyticsEventConsumer(ptx, analyticsEventStore, schedulerService, analyticsSenderService, agentConfiguration, cloudSiteService, legalService, analyticsConfigService);
    }

    @Bean
    public ExtractionAnalyticsService extractionAnalyticsService(AnalyticsEventService analyticsEventService) {
        return new ExtractionAnalyticsService(analyticsEventService);
    }

    @Bean
    public LoggingContextProvider loggingContextProvider(PluginTransactionTemplate ptx, PlanStore planStore, StepStore stepStore) {
        return new LoggingContextProvider(ptx, planStore, stepStore);
    }

    @Bean
    public MigrationStatusService migrationStatusService(PlanStore planStore, TaskStore taskStore, StepStore stepStore, PluginTransactionTemplate ptx) {
        return new MigrationStatusService(planStore, taskStore, stepStore, ptx);
    }

    @Bean
    public AttachmentPathService attachmentPathService(BootstrapManager bootstrapManager) {
        return new AttachmentPathService(bootstrapManager);
    }

    @Bean
    public SpacePermissionExtractionService spacePermissionExtractionService(SpaceManager spaceManager, SpacePermissionManager spacePermissionsManager, GroupManager groupManager, TransactionTemplate transactionTemplate) {
        return new SpacePermissionExtractionService(spaceManager, spacePermissionsManager, groupManager, transactionTemplate);
    }

    @Bean
    public CloudSiteSetupService cloudSiteSetupService(NonceService nonceService, SENSupplier senSupplier, MigrationAgentConfiguration migrationAgentConfiguration, ApplicationProperties applicationProperties, LegalService legalService, AnalyticsConfigService analyticsConfigService) {
        return new CloudSiteSetupService(nonceService, senSupplier, migrationAgentConfiguration, applicationProperties, legalService, analyticsConfigService);
    }

    @Bean
    public UserDomainService userDomainService(UserAccessor userAccessor, UserDomainRuleStore userDomainRuleStore, PluginTransactionTemplate ptx, AnalyticsEventBuilder analyticsEventBuilder, AnalyticsEventService analyticsEventService) {
        return new UserDomainService(userAccessor, userDomainRuleStore, ptx, analyticsEventBuilder, analyticsEventService);
    }

    @Bean
    public BlockedDomainService blockedDomainService(UserGroupExtractFacade userGroupExtractFacade, InvalidEmailValidator invalidEmailValidator, UserDomainService userDomainService, MigrationDarkFeaturesManager migrationDarkFeaturesManager) {
        return new BlockedDomainService(userGroupExtractFacade, invalidEmailValidator, userDomainService, migrationDarkFeaturesManager);
    }

    @Bean
    TrustedDomainCsvReaderService trustedDomainCsvReaderService(UserDomainService userDomainService) {
        return new TrustedDomainCsvReaderService(userDomainService);
    }

    @Bean
    public FeatureFlagService featureFlagService(MigrationDarkFeaturesManager migrationDarkFeaturesManager, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder) {
        return new FeatureFlagService(migrationDarkFeaturesManager, analyticsEventService, analyticsEventBuilder);
    }

    @Bean
    public ClusterLimits clusterLimits(MigrationAgentConfiguration migrationAgentConfiguration, SchedulerService schedulerService, JdbcConfluenceStore jdbcConfluenceStore, MigrationDarkFeaturesManager darkFeaturesManager, ConcurrencySettingsService concurrencySettingsService) {
        return new ClusterLimits(migrationAgentConfiguration, schedulerService, jdbcConfluenceStore, darkFeaturesManager, concurrencySettingsService);
    }

    @Bean
    public ClusterInformationService clusterInformationService(ClusterManager clusterManager, ClusterLimits clusterLimits) {
        return new ClusterInformationService(clusterManager, clusterLimits);
    }

    @Bean
    public PlanExecutionService planExecutionService(PluginTransactionTemplate ptx, TaskPlanningEngine taskPlanningEngine, List<StepPlanningEngine<?>> planningEngines, PlanStore planStore, TaskStore taskStore, PlatformService platformService, UserMappingsFileManager userMappingsFileManager, UsersToTombstoneFileManager usersToTombstoneFileManager, MigrationLogService migrationLogService, DefaultRegistrar cloudMigrationRegistrar, EventPublisher eventPublisher, ClusterInformationService clusterInformationService, SchedulerService schedulerService, StepStore stepStore, ProgressTracker progressTracker, ClusterLockService lockService, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder) {
        return new PlanExecutionService(ptx, taskPlanningEngine, planningEngines, planStore, taskStore, platformService, userMappingsFileManager, usersToTombstoneFileManager, migrationLogService, cloudMigrationRegistrar, eventPublisher, clusterInformationService, schedulerService, stepStore, progressTracker, lockService, analyticsEventService, analyticsEventBuilder);
    }

    @Bean
    public StepExecutionService stepExecutionService(PluginTransactionTemplate ptx, StepStore stepStore, LoggingContextProvider loggingContextProvider, UsersMigrationExecutor usersMigrationExecutor, GlobalEntitiesExportExecutor globalEntitiesExportExecutor, GlobalEntitiesUploadExecutor globalEntitiesUploadExecutor, GlobalEntitiesImportExecutor globalEntitiesImportExecutor, AttachmentMigrationExecutor attachmentMigrationExecutor, SpaceExportExecutor spaceExportExecutor, SpaceUsersMigrationExecutor spaceUsersMigrationExecutor, SpaceUploadExecutor spaceUploadExecutor, SpaceImportExecutor spaceImportExecutor, PlanExecutionService planExecutionService, EventPublisher eventPublisher, ClusterInformationService clusterInformationService, MigrationDarkFeaturesManager darkFeaturesManager, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, MigrationAgentConfiguration migrationAgentConfiguration) {
        return new StepExecutionService(ptx, stepStore, loggingContextProvider, usersMigrationExecutor, globalEntitiesExportExecutor, globalEntitiesUploadExecutor, globalEntitiesImportExecutor, attachmentMigrationExecutor, spaceExportExecutor, spaceUsersMigrationExecutor, spaceUploadExecutor, spaceImportExecutor, planExecutionService, eventPublisher, clusterInformationService, darkFeaturesManager, analyticsEventService, analyticsEventBuilder, migrationAgentConfiguration);
    }

    @Bean
    public ConfluenceCloudService confluenceCloudService(EnterpriseGatekeeperClient enterpriseGatekeeperClient, CloudSiteService cloudSiteService, PageTemplateManager pageTemplateManager) {
        return new ConfluenceCloudService(enterpriseGatekeeperClient, cloudSiteService, pageTemplateManager);
    }

    @Bean
    public ObjectStorageService objectStorageService(UserAgentInterceptor userAgentInterceptor, OKHttpProxyBuilder httpProxyBuilder) {
        return new S3ObjectStorageService(userAgentInterceptor, httpProxyBuilder);
    }

    @Bean
    public MigrationLogDirManager migrationLogDirManager(BootstrapManager bootstrapManager, EventPublisher eventPublisher, ClusterInformationService clusterInformationService, ApplicationConfiguration applicationConfiguration) {
        return new MigrationLogDirManager(bootstrapManager, eventPublisher, clusterInformationService, applicationConfiguration);
    }

    @Bean
    public InstanceAssessmentService instanceAssessmentService(List<AssessmentQuery<?>> queryList, SchedulerService schedulerService, GuardrailsResponseGroupStore groupStore, GuardrailsResponseStore guardrailsResponseStore, ClusterInformationService clusterInformationService, PluginTransactionTemplate ptx, DialectResolver dialectResolver) {
        return new InstanceAssessmentService(queryList, schedulerService, dialectResolver, groupStore, guardrailsResponseStore, clusterInformationService, ptx);
    }

    @Bean
    public AssessmentJobProgressService assessmentJobProgressService(InstanceAnalysisControlStore instanceAnalysisControlStore, InstanceAnalysisControlService instanceAnalysisControlService, MigrationDarkFeaturesManager features, GuardrailsResponseGroupStore guardrailsResponseGroupStore, InstanceAssessmentService instanceAssessmentService, AccessLogProcessingJobRunner accessLogProcessingJobRunner) {
        return new AssessmentJobProgressService(instanceAnalysisControlStore, instanceAnalysisControlService, features, guardrailsResponseGroupStore, instanceAssessmentService, accessLogProcessingJobRunner);
    }

    @Bean
    public InstanceAnalysisControlService instanceAnalysisControlService(PluginTransactionTemplate ptx, InstanceAnalysisControlStore instanceAnalysisControlStore, SchedulerService schedulerService, MigrationDarkFeaturesManager features) {
        return new InstanceAnalysisControlService(ptx, instanceAnalysisControlStore, schedulerService, features);
    }

    @Bean
    public BrowserMetricsService browserMetricsService(GuardrailsBrowserMetricsStore guardrailsBrowserMetricsStore, MigrationDarkFeaturesManager features, CacheManager cacheManager, BootstrapManager bootstrapManager, PluginTransactionTemplate ptx) {
        return new BrowserMetricsService(guardrailsBrowserMetricsStore, features, cacheManager, bootstrapManager, ptx);
    }

    @Bean
    public MacroAssessmentService macroAssessmentService(EntityManagerTemplate entityManagerTemplate, BootstrapManager bootstrapManager) {
        return new MacroAssessmentService(entityManagerTemplate, bootstrapManager);
    }

    @Bean
    public InstanceAssessmentCSVService instanceAssessmentCSVService(BootstrapManager bootstrapManager, GuardrailsResponseStore guardrailsResponseStore, InstanceMetadataCollector instanceMetadataCollector, GuardrailsResponseGroupStore guardrailsResponseGroupStore, BrowserMetricsService browserMetricsService, PluginTransactionTemplate ptx, DailyUsageMetricsStore dailyUsageMetricsStore) {
        return new InstanceAssessmentCSVService(bootstrapManager, guardrailsResponseStore, instanceMetadataCollector, browserMetricsService, guardrailsResponseGroupStore, ptx, dailyUsageMetricsStore);
    }

    @Bean
    public InstanceMetadataCollector instanceMetadataCollector(SENSupplier senSupplier, SystemInformationService systemInformationService, GuardrailsResponseGroupStore guardrailsResponseGroupStore, LicenseHandler licenseHandler, TimeZoneManager timeZoneManager) {
        return new InstanceMetadataCollector(senSupplier, systemInformationService, guardrailsResponseGroupStore, licenseHandler, timeZoneManager);
    }

    @Bean
    public CloudTypeSettingsService cloudTypeSettingsService(PluginSettingsFactory pluginSettingsFactory, MigrationAgentConfiguration migrationAgentConfiguration, PlanService planService, CheckResultsService checkResultsService, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, AnalyticsEventConsumer analyticsEventConsumer, CloudSiteService cloudSiteService, PlanDecoratorService planDecoratorService) {
        return new CloudTypeSettingsService(pluginSettingsFactory, migrationAgentConfiguration, planService, checkResultsService, analyticsEventService, analyticsEventBuilder, analyticsEventConsumer, cloudSiteService, planDecoratorService);
    }

    @Bean
    public MigrationSettingsService migrationSettingsService(MigrationDarkFeaturesManager migrationDarkFeaturesManager, CloudTypeSettingsService cloudTypeSettingsService, ConcurrencySettingsService concurrencySettingsService) {
        return new MigrationSettingsService(migrationDarkFeaturesManager, cloudTypeSettingsService, concurrencySettingsService);
    }

    @Bean
    public SpaceStatisticCalculationAnalyticService spaceStatisticPreCalculationAnalyticService(AnalyticsEventBuilder analyticsEventBuilder, AnalyticsEventService analyticsEventService, CloudSiteService cloudSiteService) {
        return new SpaceStatisticCalculationAnalyticService(analyticsEventBuilder, analyticsEventService, cloudSiteService);
    }

    @Bean
    public SpaceStatisticCalculationService spaceStatisticPreCalculationService(SpaceStatisticStore spaceStatisticStore, JdbcConfluenceStore confluenceStore, EntityManagerTemplate entityManagerTemplate, PluginTransactionTemplate pluginTransactionTemplate, MigrationAgentConfiguration migrationAgentConfiguration, SpaceStatisticCalculationAnalyticService spaceStatisticCalculationAnalyticService, TeamCalendarHelper teamCalendarHelper, MigrationTimeEstimationUtils migrationTimeEstimationUtils) {
        return new SpaceStatisticCalculationService(spaceStatisticStore, confluenceStore, entityManagerTemplate, pluginTransactionTemplate, migrationAgentConfiguration, spaceStatisticCalculationAnalyticService, teamCalendarHelper, migrationTimeEstimationUtils);
    }

    @Bean
    SpaceStatisticCalculationIntervalExecutor spaceStatisticCalculationIntervalExecutor(SpaceStatisticCalculationService spaceStatisticCalculationService, MigrationDarkFeaturesManager migrationDarkFeaturesManager) {
        return new SpaceStatisticCalculationIntervalExecutor(spaceStatisticCalculationService, migrationDarkFeaturesManager);
    }

    @Bean
    SpaceMetadataIntervalEmitter spaceMetadataIntervalEmitter(MigrationDarkFeaturesManager migrationDarkFeaturesManager, MigrationMetadataAggregatorService migrationMetadataAggregatorService, SchedulerService schedulerService) {
        return new SpaceMetadataIntervalEmitter(migrationDarkFeaturesManager, migrationMetadataAggregatorService, schedulerService);
    }

    @Bean
    SpaceStatisticCalculationInitialExecutor spaceStatisticCalculationInitialExecutor(SpaceStatisticCalculationService spaceStatisticCalculationService, MigrationDarkFeaturesManager migrationDarkFeaturesManager, SchedulerService schedulerService, EventPublisher eventPublisher) {
        return new SpaceStatisticCalculationInitialExecutor(spaceStatisticCalculationService, migrationDarkFeaturesManager, schedulerService, eventPublisher);
    }

    @Bean
    RelationsAnalyzerService migrationAssessmentsService(EntityManagerTemplate entityManagerTemplate, SystemInformationService systemInformationService, SpaceManager spaceManager, StatisticsService statisticsService, ApplicationLinkService applicationLinkService, CQLSearchService cqlSearchService, SpaceKeyResolver spaceKeyResolver, SpaceService spaceService, MigrationTimeEstimationUtils migrationTimeEstimationUtils, WarnLogFileWriter warnLogFileWriter, UserGroupExtractFacade userGroupExtractFacade, MigrationDarkFeaturesManager migrationDarkFeaturesManager) {
        return new RelationsAnalyzerService(entityManagerTemplate, systemInformationService, spaceManager, statisticsService, applicationLinkService, cqlSearchService, spaceKeyResolver, spaceService, migrationTimeEstimationUtils, warnLogFileWriter, userGroupExtractFacade, migrationDarkFeaturesManager);
    }

    @Bean
    RelationsAnalyzerRunner relationAnalyzerRunner(SchedulerService schedulerService, RelationsAnalyzerService relationsAnalyzerService, BootstrapManager bootstrapManager, UserAccessor userAccessor, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, WarnLogFileWriter warnLogFileWriter) {
        return new RelationsAnalyzerRunner(schedulerService, relationsAnalyzerService, bootstrapManager, userAccessor, analyticsEventService, analyticsEventBuilder, warnLogFileWriter);
    }

    @Bean
    public EncryptionConfigHandler encryptionConfigHandler(BootstrapManager bootstrapManager, ClusterLockService clusterLockService) {
        return new EncryptionConfigHandler(bootstrapManager, clusterLockService);
    }

    @Bean
    public EncryptionSecretManager encryptionSecretManager(MigrationAgentConfiguration migrationAgentConfiguration, EncryptionConfigHandler encryptionConfigHandler) {
        return new EncryptionSecretManager(migrationAgentConfiguration, encryptionConfigHandler);
    }

    @Bean
    public EncryptionService encryptionService(EncryptionSecretManager encryptionSecretManager, MigrationDarkFeaturesManager migrationDarkFeaturesManager, AnalyticsEventBuilder analyticsEventBuilder, AnalyticsEventService analyticsEventService, CloudSiteService cloudSiteService) {
        return new EncryptionService(encryptionSecretManager, migrationDarkFeaturesManager, analyticsEventBuilder, analyticsEventService, cloudSiteService);
    }

    @Bean
    public BlockedDomainUmsService blockedDomainUmsService(InvalidEmailValidator invalidEmailValidator) {
        return new BlockedDomainUmsService(invalidEmailValidator);
    }

    @Bean
    public MostFrequentDomainService mostFrequentDomainService(UserService userService, BlockedDomainUmsService blockedDomainUmsService, AnalyticsEventBuilder analyticsEventBuilder, AnalyticsEventService analyticsEventService) {
        return new MostFrequentDomainService(userService, blockedDomainUmsService, analyticsEventBuilder, analyticsEventService);
    }

    @Bean
    public NewEmailSuggestingService newEmailSuggestingService(MostFrequentDomainService mostFrequentDomainService) {
        return new NewEmailSuggestingService(mostFrequentDomainService);
    }

    @Bean
    public ContainerTokenValidator containerTokenValidator(CloudSiteService cloudSiteService, MigrationPlatformService migrationPlatformService) {
        return new ContainerTokenValidator(cloudSiteService, migrationPlatformService);
    }

    @Bean
    public ConcurrencySettingsService concurrencySettingsService(PluginSettingsFactory pluginSettingsFactory) {
        return new ConcurrencySettingsService(pluginSettingsFactory);
    }

    @Bean
    public SpaceImportConfigFileManager spaceImportConfigFileManager(BootstrapManager bootstrapManager) {
        return new SpaceImportConfigFileManager(bootstrapManager);
    }

    @Bean
    public CsvWriterService csvWriterService() {
        return new CsvWriterService();
    }

    @Bean
    public TrustedDomainCsvWriterService trustedDomainCsvService(UserDomainService userDomainService, DefaultExportDirManager defaultExportDirManager, BlockedDomainService blockedDomainService, CsvWriterService csvWriterService) {
        return new TrustedDomainCsvWriterService(userDomainService, defaultExportDirManager, blockedDomainService, csvWriterService);
    }

    @Bean
    public DailyUsageMetricsStore dailyUsageMetricsStore(BootstrapManager bootstrapManager, MigrationDarkFeaturesManager features) {
        return new DailyUsageMetricsStore(bootstrapManager, features);
    }

    @Bean
    public AccessLogProcessingJobRunner accessLogProcessingJobRunner(SchedulerService schedulerService, ClusterInformationService clusterInformationService, TomcatAccessLogsFinder tomcatAccessLogsFinder, TomcatAccessLogParser tomcatAccessLogsParser, DailyUsageMetricsStore dailyUsageMetricsStore, MigrationDarkFeaturesManager features) {
        return new AccessLogProcessingJobRunner(schedulerService, clusterInformationService, tomcatAccessLogsFinder, tomcatAccessLogsParser, dailyUsageMetricsStore, features);
    }

    @Bean
    public TomcatAccessLogsFinder tomcatAccessLogsFinder() {
        return new TomcatAccessLogsFinder();
    }

    @Bean
    public TomcatAccessLogParser tomcatAccessLogsParser(TomcatAccessLogsFinder finder, PageTypeProvider pageTypeProvider) {
        return new TomcatAccessLogParser(pageTypeProvider, finder.getLogFormat());
    }

    @Bean
    public PageTypeProvider pageTypeProvider() {
        return new PageTypeProvider();
    }
}

