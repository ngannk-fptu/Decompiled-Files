/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.listener.ProductAnalyticsEventListener
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.scheduler.SchedulerService
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.analytics.client.spring.shared;

import com.atlassian.analytics.api.listener.ProductAnalyticsEventListener;
import com.atlassian.analytics.client.DefaultTimeKeeper;
import com.atlassian.analytics.client.LoginPageRedirector;
import com.atlassian.analytics.client.ServerIdProvider;
import com.atlassian.analytics.client.TimeKeeper;
import com.atlassian.analytics.client.UserPermissionsHelper;
import com.atlassian.analytics.client.base.BaseDataLogger;
import com.atlassian.analytics.client.configuration.AnalyticsConfig;
import com.atlassian.analytics.client.configuration.LastPrivacyPolicyUpdateDateProvider;
import com.atlassian.analytics.client.detect.PrivacyPolicyUpdateDetector;
import com.atlassian.analytics.client.eventfilter.AllowedWordFilter;
import com.atlassian.analytics.client.eventfilter.BlacklistFilter;
import com.atlassian.analytics.client.eventfilter.whitelist.PluginsWhitelistCollector;
import com.atlassian.analytics.client.eventfilter.whitelist.WhitelistCollector;
import com.atlassian.analytics.client.eventfilter.whitelist.WhitelistFilter;
import com.atlassian.analytics.client.eventfilter.whitelist.WhitelistSearcher;
import com.atlassian.analytics.client.hash.AnalyticsEmailHasher;
import com.atlassian.analytics.client.hash.BcryptAnalyticsEmailHasher;
import com.atlassian.analytics.client.license.LicenseProvider;
import com.atlassian.analytics.client.listener.ProductEventListener;
import com.atlassian.analytics.client.logger.AnalyticsLogger;
import com.atlassian.analytics.client.logger.EventAnonymizer;
import com.atlassian.analytics.client.pipeline.AnalyticPipelineConfiguration;
import com.atlassian.analytics.client.pipeline.AnalyticsPipeline;
import com.atlassian.analytics.client.properties.AnalyticsPropertyService;
import com.atlassian.analytics.client.properties.LoggingProperties;
import com.atlassian.analytics.client.report.EventReportPermissionManager;
import com.atlassian.analytics.client.report.EventReporter;
import com.atlassian.analytics.client.report.TimeoutChecker;
import com.atlassian.analytics.client.s3.AnalyticsS3Client;
import com.atlassian.analytics.client.service.LicenseCreationDateService;
import com.atlassian.analytics.client.upload.EventUploaderConfigurationProvider;
import com.atlassian.analytics.client.upload.PeriodicEventUploaderScheduler;
import com.atlassian.analytics.client.upload.RemoteFilterRead;
import com.atlassian.analytics.client.upload.S3EventUploader;
import com.atlassian.analytics.client.upload.UploadAnalyticsInitialiser;
import com.atlassian.analytics.client.uuid.ProductUUIDProvider;
import com.atlassian.analytics.event.logging.LogEventFormatter;
import com.atlassian.analytics.event.logging.MerlinLogEventFormatter;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.scheduler.SchedulerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value={AnalyticPipelineConfiguration.class})
public class SharedBeans {
    @Bean
    public AllowedWordFilter allowedWordFilter() {
        return new AllowedWordFilter();
    }

    @Bean
    public AnalyticsEmailHasher analyticsEmailHasher(AnalyticsS3Client analyticsS3Client) {
        return new BcryptAnalyticsEmailHasher(analyticsS3Client);
    }

    @Bean
    public AnalyticsS3Client analyticsS3Client() {
        return new AnalyticsS3Client();
    }

    @Bean
    public BlacklistFilter blacklistFilter(AnalyticsPropertyService analyticsPropertyService, AnalyticsS3Client analyticsS3Client) {
        return new BlacklistFilter(analyticsPropertyService, analyticsS3Client);
    }

    @Bean
    public EventAnonymizer eventAnonymizer(ProductUUIDProvider productUUIDProvider) {
        return new EventAnonymizer(productUUIDProvider);
    }

    @Bean
    public EventReporter eventReporter(TimeoutChecker timeoutChecker) {
        return new EventReporter(timeoutChecker);
    }

    @Bean
    public EventReportPermissionManager eventReportPermissionManager(UserPermissionsHelper userPermissionsHelper, DarkFeatureManager darkFeatureManager) {
        return new EventReportPermissionManager(userPermissionsHelper, darkFeatureManager);
    }

    @Bean
    public EventUploaderConfigurationProvider eventUploaderConfigurationProvider(PluginSettingsFactory pluginSettingsFactory, LoggingProperties loggingProperties) {
        return new EventUploaderConfigurationProvider(pluginSettingsFactory, loggingProperties);
    }

    @Bean
    public LastPrivacyPolicyUpdateDateProvider lastPrivacyPolicyUpdateDateProvider() {
        return new LastPrivacyPolicyUpdateDateProvider();
    }

    @Bean
    public LicenseCreationDateService licenseCreationDateService(LastPrivacyPolicyUpdateDateProvider lastPrivacyPolicyUpdateDateProvider, LicenseProvider licenseProvider) {
        return new LicenseCreationDateService(lastPrivacyPolicyUpdateDateProvider, licenseProvider);
    }

    @Bean
    public LogEventFormatter logEventFormatter() {
        return new MerlinLogEventFormatter();
    }

    @Bean
    public LoginPageRedirector loginPageRedirector(LoginUriProvider loginUriProvider) {
        return new LoginPageRedirector(loginUriProvider);
    }

    @Bean
    public PeriodicEventUploaderScheduler periodicEventUploaderScheduler(SchedulerService schedulerService, AnalyticsConfig analyticsConfig) {
        return new PeriodicEventUploaderScheduler(schedulerService, analyticsConfig);
    }

    @Bean
    public PrivacyPolicyUpdateDetector privacyPolicyUpdateDetector(AnalyticsConfig analyticsConfig, UserPermissionsHelper userPermissionsHelper, LicenseCreationDateService licenseCreationDateService) {
        return new PrivacyPolicyUpdateDetector(analyticsConfig, userPermissionsHelper, licenseCreationDateService);
    }

    @Bean
    public ProductAnalyticsEventListener productAnalyticsEventListener(WhitelistFilter whitelistFilter, AnalyticsPipeline analyticsPipeline) {
        return new ProductEventListener(whitelistFilter, analyticsPipeline);
    }

    @Bean
    public ProductUUIDProvider productUUIDProvider(PluginSettingsFactory pluginSettingsFactory, ServerIdProvider serverIdProvider) {
        return new ProductUUIDProvider(pluginSettingsFactory, serverIdProvider);
    }

    @Bean
    public RemoteFilterRead remoteFilterRead(PeriodicEventUploaderScheduler periodicEventUploaderScheduler, BlacklistFilter blacklistFilter, BaseDataLogger baseDataLogger, AnalyticsConfig analyticsConfig, AnalyticsEmailHasher analyticsEmailHasher) {
        return new RemoteFilterRead(periodicEventUploaderScheduler, blacklistFilter, baseDataLogger, analyticsConfig, analyticsEmailHasher);
    }

    @Bean
    public S3EventUploader s3EventUploader(PeriodicEventUploaderScheduler scheduler, AnalyticsConfig analyticsConfig, AnalyticsLogger analyticsLogger, EventUploaderConfigurationProvider eventUploaderConfigurationProvider, AnalyticsS3Client analyticsS3Client) {
        return new S3EventUploader(scheduler, analyticsConfig, analyticsLogger, eventUploaderConfigurationProvider, analyticsS3Client);
    }

    @Bean
    public ServerIdProvider serverIdProvider(LicenseHandler licenseHandler) {
        return new ServerIdProvider(licenseHandler);
    }

    @Bean
    public TimeKeeper timeKeeper() {
        return new DefaultTimeKeeper();
    }

    @Bean
    public TimeoutChecker timeoutChecker(TimeKeeper timeKeeper) {
        return new TimeoutChecker(timeKeeper);
    }

    @Bean
    public UploadAnalyticsInitialiser uploadAnalyticsInitialiser(SchedulerService schedulerService, PeriodicEventUploaderScheduler periodicEventUploaderScheduler, RemoteFilterRead remoteFilterRead, S3EventUploader s3EventUploader) {
        return new UploadAnalyticsInitialiser(schedulerService, periodicEventUploaderScheduler, remoteFilterRead, s3EventUploader);
    }

    @Bean
    public UserPermissionsHelper userPermissionsHelper(UserManager userManager) {
        return new UserPermissionsHelper(userManager);
    }

    @Bean
    public WhitelistCollector whitelistCollector(PluginAccessor pluginAccessor) {
        return new PluginsWhitelistCollector(pluginAccessor);
    }

    @Bean
    public WhitelistFilter whitelistFilter(AllowedWordFilter allowedWordFilter, WhitelistCollector whitelistCollector, EventAnonymizer eventAnonymizer, DarkFeatureManager darkFeatureManager) {
        return new WhitelistFilter(allowedWordFilter, whitelistCollector, eventAnonymizer, darkFeatureManager);
    }

    @Bean
    public WhitelistSearcher whitelistSearcher(WhitelistFilter whitelistFilter) {
        return new WhitelistSearcher(whitelistFilter);
    }
}

