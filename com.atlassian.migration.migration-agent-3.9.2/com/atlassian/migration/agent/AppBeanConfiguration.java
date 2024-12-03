/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.app.dto.check.AppVendorCheckProperties
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.migration.agent;

import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.ServiceBeanConfiguration;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.analytics.DefaultAppAnalyticsEventService;
import com.atlassian.migration.agent.service.app.AppAccessScopeService;
import com.atlassian.migration.agent.service.app.AppAssessmentInfoService;
import com.atlassian.migration.agent.service.app.PluginManager;
import com.atlassian.migration.agent.service.catalogue.PlatformService;
import com.atlassian.migration.agent.service.check.app.CloudAppKeyFetcher;
import com.atlassian.migration.agent.service.check.app.assessmentcomplete.AppAssessmentCompleteCheckRegistration;
import com.atlassian.migration.agent.service.check.app.assessmentcomplete.AppAssessmentCompleteContextProvider;
import com.atlassian.migration.agent.service.check.app.consent.AppDataMigrationConsentCheckRegistration;
import com.atlassian.migration.agent.service.check.app.consent.AppDataMigrationConsentChecker;
import com.atlassian.migration.agent.service.check.app.consent.AppDataMigrationConsentContextProvider;
import com.atlassian.migration.agent.service.check.app.license.AppLicenseCheckRegistration;
import com.atlassian.migration.agent.service.check.app.license.AppLicenseChecker;
import com.atlassian.migration.agent.service.check.app.license.AppLicenseContextProvider;
import com.atlassian.migration.agent.service.check.app.license.AppLicenseMapper;
import com.atlassian.migration.agent.service.check.app.notinstalled.AppsNotInstalledOnCloudCheckRegistration;
import com.atlassian.migration.agent.service.check.app.notinstalled.AppsNotInstalledOnCloudChecker;
import com.atlassian.migration.agent.service.check.app.notinstalled.AppsNotInstalledOnCloudContextProvider;
import com.atlassian.migration.agent.service.check.app.outdated.ServerAppsOutdatedCheckRegistration;
import com.atlassian.migration.agent.service.check.app.outdated.ServerAppsOutdatedChecker;
import com.atlassian.migration.agent.service.check.app.outdated.ServerAppsOutdatedContextProvider;
import com.atlassian.migration.agent.service.check.app.reliability.AppReliabilityContextProvider;
import com.atlassian.migration.agent.service.check.app.reliability.AppReliabilityRegistration;
import com.atlassian.migration.agent.service.check.app.reliability.AppReliabiltityChecker;
import com.atlassian.migration.agent.service.check.app.vendorcheck.AppVendorCheckContextProvider;
import com.atlassian.migration.agent.service.check.app.vendorcheck.AppVendorCheckRegistration;
import com.atlassian.migration.agent.service.check.app.vendorcheck.AppVendorChecker;
import com.atlassian.migration.agent.service.check.app.webhook.AppWebhookEndpointCheckContextProvider;
import com.atlassian.migration.agent.service.check.app.webhook.AppWebhookEndpointCheckMapper;
import com.atlassian.migration.agent.service.check.app.webhook.AppWebhookEndpointCheckRegistration;
import com.atlassian.migration.agent.service.check.app.webhook.AppWebhookEndpointCheckServiceClient;
import com.atlassian.migration.agent.service.check.app.webhook.AppWebhookEndpointChecker;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.impl.AppAssessmentFacade;
import com.atlassian.migration.agent.service.impl.AppConsentService;
import com.atlassian.migration.agent.service.impl.AppMigrationDevelopmentService;
import com.atlassian.migration.agent.service.impl.AppRerunService;
import com.atlassian.migration.agent.service.impl.AppTransferLogService;
import com.atlassian.migration.agent.service.impl.DefaultBackdoorService;
import com.atlassian.migration.agent.service.impl.MigrationAppAggregatorService;
import com.atlassian.migration.agent.service.impl.SENSupplier;
import com.atlassian.migration.agent.store.CloudSiteStore;
import com.atlassian.migration.agent.store.PlanStore;
import com.atlassian.migration.agent.store.impl.AppAssessmentInfoStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.atlassian.migration.app.AbstractCloudMigrationRegistrar;
import com.atlassian.migration.app.AppAssessmentClient;
import com.atlassian.migration.app.AppMigrationDarkFeatures;
import com.atlassian.migration.app.AppPreflightExecutorImpl;
import com.atlassian.migration.app.DefaultAppMigrationServiceClient;
import com.atlassian.migration.app.DefaultRegistrar;
import com.atlassian.migration.app.MigratabliltyInfo;
import com.atlassian.migration.app.MigrationAppAggregatorClient;
import com.atlassian.migration.app.OsgiBundleHelper;
import com.atlassian.migration.app.dto.check.AppVendorCheckProperties;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(value={ServiceBeanConfiguration.class})
@Configuration
public class AppBeanConfiguration {
    @Bean
    public AppMigrationDevelopmentService appMigrationDevelopmentService(PluginTransactionTemplate ptx, DefaultRegistrar registrar, PlanStore planStore, PlatformService platformService) {
        return new AppMigrationDevelopmentService(ptx, registrar, planStore, platformService);
    }

    @Bean
    public AppRerunService appRerunService(PluginTransactionTemplate ptx, AbstractCloudMigrationRegistrar registrar, PlanStore planStore) {
        return new AppRerunService(ptx, registrar, planStore);
    }

    @Bean
    public DefaultAppAnalyticsEventService defaultAppAnalyticsEventService(AnalyticsEventService analyticsEventService, Clock clock, AnalyticsEventBuilder analyticsEventBuilder) {
        return new DefaultAppAnalyticsEventService(analyticsEventService, clock, analyticsEventBuilder);
    }

    @Bean
    public AppConsentService appConsentService(DefaultAppMigrationServiceClient appMigrationServiceClient, CloudSiteService cloudSiteService, SENSupplier senSupplier) {
        return new AppConsentService(appMigrationServiceClient, cloudSiteService, senSupplier);
    }

    @Bean
    public CloudAppKeyFetcher cloudAppKeyFetcher(MigrationAppAggregatorService appAggregatorService, DefaultRegistrar registrar) {
        MigratabliltyInfo migratabliltyInfo = new MigratabliltyInfo(registrar);
        return new CloudAppKeyFetcher(appAggregatorService, migratabliltyInfo);
    }

    @Bean
    public AppAssessmentCompleteCheckRegistration appAssessmentCompleteCheckRegistration(AppAssessmentFacade appsAssessmentService, AnalyticsEventBuilder analyticsEventBuilder) {
        return new AppAssessmentCompleteCheckRegistration(appsAssessmentService, analyticsEventBuilder);
    }

    @Bean
    public AppAssessmentCompleteContextProvider appAssessmentCompleteContextProvider(AppAssessmentFacade appAssessmentService) {
        return new AppAssessmentCompleteContextProvider(appAssessmentService);
    }

    @Bean
    public AppDataMigrationConsentChecker appDataMigrationConsentChecker(AppAccessScopeService appConsentService, AppAssessmentInfoService appAssessmentInfoService, PluginManager pluginManager) {
        return new AppDataMigrationConsentChecker(appConsentService, appAssessmentInfoService, pluginManager);
    }

    @Bean
    public AppDataMigrationConsentCheckRegistration appDataMigrationConsentCheckRegistration(AppAssessmentInfoService appAssessmentInfoService, AppAssessmentFacade appAssessmentFacade, AnalyticsEventBuilder analyticsEventBuilder, AppAccessScopeService appConsentService, PluginManager pluginManager) {
        return new AppDataMigrationConsentCheckRegistration(appAssessmentInfoService, appAssessmentFacade, analyticsEventBuilder, appConsentService, pluginManager);
    }

    @Bean
    public AppDataMigrationConsentContextProvider appDataMigrationConsentContextProvider(AppAssessmentFacade appAssessmentFacade) {
        return new AppDataMigrationConsentContextProvider(appAssessmentFacade);
    }

    @Bean
    public AppsNotInstalledOnCloudChecker appsNotInstalledOnCloudChecker(MigrationAppAggregatorService appAggregatorService, AppAssessmentClient appAssessmentClient, CloudSiteService cloudSiteService, AppAssessmentFacade appAssessmentFacade, CloudAppKeyFetcher cloudAppKeyFetcher) {
        return new AppsNotInstalledOnCloudChecker(appAggregatorService, appAssessmentClient, cloudSiteService, appAssessmentFacade, cloudAppKeyFetcher);
    }

    @Bean
    public AppsNotInstalledOnCloudCheckRegistration appsNotInstalledOnCloudCheckRegistration(MigrationAppAggregatorService appAggregatorService, AppAssessmentClient appAssessmentClient, AppAssessmentInfoService appAssessmentInfoService, AppAssessmentFacade appAssessmentFacade, CloudSiteService cloudSiteService, AnalyticsEventBuilder analyticsEventBuilder, CloudAppKeyFetcher cloudAppKeyFetcher) {
        return new AppsNotInstalledOnCloudCheckRegistration(appAggregatorService, appAssessmentClient, appAssessmentInfoService, appAssessmentFacade, cloudSiteService, analyticsEventBuilder, cloudAppKeyFetcher);
    }

    @Bean
    public AppsNotInstalledOnCloudContextProvider appsNotInstalledOnCloudContextProvider(AppAssessmentInfoService appAssessmentInfoService) {
        return new AppsNotInstalledOnCloudContextProvider(appAssessmentInfoService);
    }

    @Bean
    public AppLicenseMapper appLicenseMapper() {
        return new AppLicenseMapper();
    }

    @Bean
    public AppLicenseChecker appLicenseChecker(MigrationAppAggregatorService appAggregatorService, AppAssessmentClient appAssessmentClient, CloudSiteService cloudSiteService, AppAssessmentFacade appAssessmentFacade, CloudAppKeyFetcher cloudAppKeyFetcher) {
        return new AppLicenseChecker(appAggregatorService, appAssessmentClient, cloudSiteService, appAssessmentFacade, cloudAppKeyFetcher);
    }

    @Bean
    public AppLicenseCheckRegistration appLicenseCheckRegistration(AppLicenseChecker appLicenseChecker, AppLicenseContextProvider appLicenseContextProvider, AppLicenseMapper appLicenseMapper, AnalyticsEventBuilder analyticsEventBuilder) {
        return new AppLicenseCheckRegistration(appLicenseChecker, appLicenseContextProvider, appLicenseMapper, analyticsEventBuilder);
    }

    @Bean
    public AppLicenseContextProvider appLicenseContextProvider() {
        return new AppLicenseContextProvider();
    }

    @Bean
    public ServerAppsOutdatedChecker serverAppsOutdatedChecker(PluginManager pluginManager, MigrationAppAggregatorService appAggregatorService, AppAssessmentFacade appAssessmentFacade) {
        return new ServerAppsOutdatedChecker(pluginManager, appAggregatorService, appAssessmentFacade);
    }

    @Bean
    public ServerAppsOutdatedCheckRegistration serverAppsOutdatedCheckRegistration(AnalyticsEventBuilder analyticsEventBuilder, AppAssessmentInfoService appAssessmentInfoService, PluginManager pluginManager, MigrationAppAggregatorService appAggregatorService, AppAssessmentFacade appAssessmentFacade) {
        return new ServerAppsOutdatedCheckRegistration(analyticsEventBuilder, appAssessmentInfoService, pluginManager, appAggregatorService, appAssessmentFacade);
    }

    @Bean
    public ServerAppsOutdatedContextProvider serverAppsOutdatedContextProvider(AppAssessmentInfoService appAssessmentInfoService, MigrationAppAggregatorService migrationAppAggregatorService) {
        return new ServerAppsOutdatedContextProvider(appAssessmentInfoService, migrationAppAggregatorService);
    }

    @Bean
    public AppReliabilityContextProvider appReliabilityContextProvider(AppAssessmentFacade appAssessmentFacade) {
        return new AppReliabilityContextProvider(appAssessmentFacade);
    }

    @Bean
    public AppReliabiltityChecker appReliabiltityChecker(MigrationAppAggregatorService migrationAppAggregatorService, PluginManager pluginManager) {
        return new AppReliabiltityChecker(migrationAppAggregatorService, pluginManager);
    }

    @Bean
    public AppReliabilityRegistration appReliabilityRegistration(AppReliabiltityChecker appReliabiltityChecker, AppReliabilityContextProvider contextProvider, AnalyticsEventBuilder analyticsEventBuilder) {
        return new AppReliabilityRegistration(appReliabiltityChecker, contextProvider, analyticsEventBuilder);
    }

    @Bean
    public AppVendorCheckProperties appVendorCheckProperties(MigrationAgentConfiguration configuration) {
        return new AppVendorCheckProperties(configuration.getAppVendorCheckGlobalTimeout(), configuration.getAppVendorCheckPerCheckTimeout(), configuration.getAppVendorCheckCsvFileSizeInBytes());
    }

    @Bean
    public AppPreflightExecutorImpl appPreflightExecutor(MigrationAppAggregatorClient migrationAppAggregatorClient, AppMigrationDarkFeatures appMigrationFeatures, OsgiBundleHelper osgiBundleHelper, AppVendorCheckProperties appVendorCheckProperties) {
        return new AppPreflightExecutorImpl(osgiBundleHelper, migrationAppAggregatorClient, appMigrationFeatures, appVendorCheckProperties);
    }

    @Bean
    public AppVendorCheckContextProvider appVendorCheckContextProvider() {
        return new AppVendorCheckContextProvider();
    }

    @Bean
    public AppVendorChecker appVendorCheckChecker(CloudSiteStore cloudSiteStore, AppPreflightExecutorImpl appPreflightExecutor, MigrationDarkFeaturesManager migrationDarkFeaturesManager, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder) {
        return new AppVendorChecker(cloudSiteStore, appPreflightExecutor, migrationDarkFeaturesManager, analyticsEventService, analyticsEventBuilder);
    }

    @Bean
    public AppVendorCheckRegistration appVendorCheckRegistration(AppVendorChecker appVendorChecker, AppVendorCheckContextProvider contextProvider, AnalyticsEventBuilder analyticsEventBuilder) {
        return new AppVendorCheckRegistration(appVendorChecker, contextProvider, analyticsEventBuilder);
    }

    @Bean
    public AppWebhookEndpointCheckContextProvider appWebhookEndpointCheckContextProvider() {
        return new AppWebhookEndpointCheckContextProvider();
    }

    @Bean
    public AppWebhookEndpointChecker appWebhookEndpointChecker(AppWebhookEndpointCheckServiceClient appWebhookEndpointCheckServiceClient, AbstractCloudMigrationRegistrar cloudMigrationRegistrar, MigrationAppAggregatorService migrationAppAggregatorService, PlatformService platformService) {
        return new AppWebhookEndpointChecker(appWebhookEndpointCheckServiceClient, platformService, cloudMigrationRegistrar, migrationAppAggregatorService);
    }

    @Bean
    public AppWebhookEndpointCheckMapper appWebhookEndpointCheckMapper() {
        return new AppWebhookEndpointCheckMapper();
    }

    @Bean
    public AppWebhookEndpointCheckRegistration appWebhookEndpointCheckRegistration(AppWebhookEndpointChecker appWebhookEndpointChecker, AppWebhookEndpointCheckContextProvider appWebhookEndpointCheckContextProvider, AppWebhookEndpointCheckMapper appWebhookEndpointCheckMapper, AnalyticsEventBuilder analyticsEventBuilder) {
        return new AppWebhookEndpointCheckRegistration(appWebhookEndpointChecker, appWebhookEndpointCheckContextProvider, appWebhookEndpointCheckMapper, analyticsEventBuilder);
    }

    @Bean
    public AppTransferLogService appTransferLogService(PluginTransactionTemplate ptx, DefaultAppMigrationServiceClient appMigrationServiceClient, PlanStore planStore) {
        return new AppTransferLogService(ptx, appMigrationServiceClient, planStore);
    }

    @Bean
    public DefaultBackdoorService defaultBackdoorService(PluginTransactionTemplate ptx, EntityManagerTemplate entityManagerTemplate, AppAssessmentInfoStore appInfoStore) {
        return new DefaultBackdoorService(ptx, entityManagerTemplate, appInfoStore);
    }
}

