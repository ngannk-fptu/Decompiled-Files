/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  javax.servlet.Filter
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.context.annotation.Bean
 *  org.springframework.stereotype.Component
 */
package com.atlassian.ratelimiting.internal;

import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.cache.CacheManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.pocketknife.api.querydsl.DatabaseAccessor;
import com.atlassian.ratelimiting.analytics.AnalyticsService;
import com.atlassian.ratelimiting.audit.AuditService;
import com.atlassian.ratelimiting.bucket.TokenBucketFactory;
import com.atlassian.ratelimiting.cluster.ClusterEventService;
import com.atlassian.ratelimiting.configuration.SystemPropertiesService;
import com.atlassian.ratelimiting.dao.RateLimitingSettingsVersionDao;
import com.atlassian.ratelimiting.dao.SystemRateLimitingSettingsDao;
import com.atlassian.ratelimiting.dao.SystemRateLimitingSettingsProvider;
import com.atlassian.ratelimiting.dao.UserRateLimitCounterDao;
import com.atlassian.ratelimiting.dao.UserRateLimitSettingsDao;
import com.atlassian.ratelimiting.dao.UserRateLimitingSettingsProvider;
import com.atlassian.ratelimiting.db.internal.cacheable.dao.CachingSystemRateLimitingSettingsProvider;
import com.atlassian.ratelimiting.db.internal.cacheable.dao.CachingUserRateLimitingSettingsProvider;
import com.atlassian.ratelimiting.db.internal.dao.QDSLRateLimitingSettingsVersionDao;
import com.atlassian.ratelimiting.db.internal.dao.QDSLSystemRateLimitingSettingsDao;
import com.atlassian.ratelimiting.db.internal.dao.QDSLUserRateLimitCounterDao;
import com.atlassian.ratelimiting.db.internal.dao.QDSLUserRateLimitSettingsDao;
import com.atlassian.ratelimiting.dmz.DmzRateLimitSettingsModificationService;
import com.atlassian.ratelimiting.dmz.RateLimitingMode;
import com.atlassian.ratelimiting.dmz.SystemRateLimitingSettings;
import com.atlassian.ratelimiting.dmz.TokenBucketSettings;
import com.atlassian.ratelimiting.featureflag.RateLimitingFeatureFlagService;
import com.atlassian.ratelimiting.history.RateLimitHistoryReportResultMapper;
import com.atlassian.ratelimiting.history.RateLimitingReportService;
import com.atlassian.ratelimiting.internal.ConfigurationConstants;
import com.atlassian.ratelimiting.internal.RateLimitingJobScheduler;
import com.atlassian.ratelimiting.internal.analytics.AnalyticsBatchEventsPublisherJob;
import com.atlassian.ratelimiting.internal.analytics.DefaultAnalyticsService;
import com.atlassian.ratelimiting.internal.audit.AuditEntryFactory;
import com.atlassian.ratelimiting.internal.audit.AuditListener;
import com.atlassian.ratelimiting.internal.audit.ObservabilityAuditService;
import com.atlassian.ratelimiting.internal.bucket.DefaultTokenBucketFactory;
import com.atlassian.ratelimiting.internal.cluster.SingleNodeClusterEventService;
import com.atlassian.ratelimiting.internal.concurrent.OperationThrottler;
import com.atlassian.ratelimiting.internal.configuration.ConfigurationLoggerJob;
import com.atlassian.ratelimiting.internal.configuration.DefaultSystemPropertiesService;
import com.atlassian.ratelimiting.internal.filter.RateLimitFilter;
import com.atlassian.ratelimiting.internal.filter.RateLimitPreAuthFilter;
import com.atlassian.ratelimiting.internal.frontend.RateLimitingEnabledCondition;
import com.atlassian.ratelimiting.internal.frontend.RatelimitingServlet;
import com.atlassian.ratelimiting.internal.history.DefaultRateLimitHistoryReportResultMapper;
import com.atlassian.ratelimiting.internal.history.DefaultRateLimitHistoryService;
import com.atlassian.ratelimiting.internal.history.HistoryCleanupJob;
import com.atlassian.ratelimiting.internal.history.HistoryFlushJob;
import com.atlassian.ratelimiting.internal.history.HistoryIntervalManager;
import com.atlassian.ratelimiting.internal.license.DefaultLicenseChecker;
import com.atlassian.ratelimiting.internal.plugin.PluginChecker;
import com.atlassian.ratelimiting.internal.properties.AppLinkWhitelistedOAuthConsumers;
import com.atlassian.ratelimiting.internal.properties.SystemProperties;
import com.atlassian.ratelimiting.internal.requesthandler.BasicAuthRequestDecoder;
import com.atlassian.ratelimiting.internal.requesthandler.DefaultPreAuthRequestDecoder;
import com.atlassian.ratelimiting.internal.requesthandler.DefaultRateLimitResponseHandler;
import com.atlassian.ratelimiting.internal.requesthandler.DefaultUserRequestRateLimitHandler;
import com.atlassian.ratelimiting.internal.requesthandler.logging.RateLimitedRequestLogger;
import com.atlassian.ratelimiting.internal.service.DefaultRateLimitService;
import com.atlassian.ratelimiting.internal.service.RateLimitReaperJob;
import com.atlassian.ratelimiting.internal.settings.RateLimitLightweightAccessService;
import com.atlassian.ratelimiting.internal.settings.SettingsReloaderJob;
import com.atlassian.ratelimiting.license.LicenseChecker;
import com.atlassian.ratelimiting.node.RateLimitService;
import com.atlassian.ratelimiting.properties.RateLimitingProperties;
import com.atlassian.ratelimiting.requesthandler.PreAuthRequestDecoder;
import com.atlassian.ratelimiting.requesthandler.PreAuthRequestSingleMethodDecoder;
import com.atlassian.ratelimiting.requesthandler.RateLimitResponseHandler;
import com.atlassian.ratelimiting.requesthandler.RateLimitUiRequestHandler;
import com.atlassian.ratelimiting.requesthandler.RateLimitUserRequestHandler;
import com.atlassian.ratelimiting.rest.resource.RateLimitHistoryResource;
import com.atlassian.ratelimiting.rest.resource.RateLimitingExceptionMapper;
import com.atlassian.ratelimiting.rest.resource.SystemRateLimitSettingsResource;
import com.atlassian.ratelimiting.rest.resource.UserRateLimitSettingsResource;
import com.atlassian.ratelimiting.rest.resource.UserResource;
import com.atlassian.ratelimiting.scheduling.ScheduledJobSource;
import com.atlassian.ratelimiting.user.UserService;
import com.atlassian.ratelimiting.user.keyprovider.UserKeyProvider;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.servlet.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RateLimitingConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(RateLimitingConfiguration.class);
    public static final String PLUGIN_KEY = "com.atlassian.ratelimiting.rate-limiting-plugin";
    public static final Long DEFAULT_EXEMPTIONS_LIMIT = 50000L;
    @Autowired
    @ComponentImport
    private PermissionEnforcer permissionEnforcer;
    @Autowired
    @ComponentImport
    private LocaleResolver localeResolver;
    @Autowired
    @ComponentImport
    private I18nResolver i18nResolver;
    @Autowired
    @ComponentImport
    private EventPublisher eventPublisher;
    @Autowired
    @ComponentImport
    private com.atlassian.audit.api.AuditService auditService;
    @Autowired
    @ComponentImport
    private CacheManager cacheManager;
    @Autowired
    @ComponentImport
    private SchedulerService schedulerService;
    @Autowired
    @ComponentImport
    private LicenseHandler licenseHandler;
    @Autowired
    @ComponentImport
    private DarkFeatureManager darkFeatureManager;
    @Autowired
    @ComponentImport
    private SoyTemplateRenderer templateRenderer;
    @Autowired
    @ComponentImport
    private LoginUriProvider loginUriProvider;
    @Autowired
    @ComponentImport
    private ApplicationLinkService applicationLinkService;

    @Bean
    public ConfigurationConstants configurationConstants() {
        return new ConfigurationConstants(PLUGIN_KEY, DEFAULT_EXEMPTIONS_LIMIT);
    }

    @Bean(name={"rateLimitFilter"})
    public Filter rateLimitFilter(RateLimitUserRequestHandler userRequestRateLimitHandler, RateLimitService rateLimitService, RateLimitLightweightAccessService rateLimitSettingsService, UserService userService, RateLimitResponseHandler rateLimitResponseHandler, RateLimitedRequestLogger rateLimitedRequestLogger, RateLimitUiRequestHandler rateLimitUiRequestHandler) {
        OperationThrottler<UserKey> operationThrottler = new OperationThrottler<UserKey>(Duration.ofMinutes(5L));
        return new RateLimitFilter(userRequestRateLimitHandler, rateLimitService, rateLimitSettingsService, userService, operationThrottler, rateLimitResponseHandler, rateLimitedRequestLogger, rateLimitUiRequestHandler);
    }

    @Bean(name={"rateLimitPreAuthFilter"})
    public Filter rateLimitPreAuthFilter(RateLimitUserRequestHandler userRequestRateLimitHandler, RateLimitService rateLimitService, RateLimitLightweightAccessService rateLimitSettingsService, RateLimitingProperties rateLimitingProperties, PreAuthRequestDecoder preAuthRequestDecoder, RateLimitResponseHandler rateLimitResponseHandler, RateLimitedRequestLogger rateLimitedRequestLogger, RateLimitUiRequestHandler rateLimitUiRequestHandler) {
        return new RateLimitPreAuthFilter(rateLimitService, rateLimitSettingsService, rateLimitingProperties, preAuthRequestDecoder, rateLimitResponseHandler, rateLimitedRequestLogger, rateLimitUiRequestHandler, userRequestRateLimitHandler);
    }

    @Bean
    public RateLimitResponseHandler rateLimitResponseHandler(I18nResolver i18nResolver) {
        return new DefaultRateLimitResponseHandler(i18nResolver, RateLimitResponseHandler.RateLimitHeaderOption.AUTHENTICATED_REQUEST_ONLY);
    }

    @Bean
    public AppLinkWhitelistedOAuthConsumers atlassianAppLinkOauthResolver(ApplicationLinkService applicationLinkService) {
        return new AppLinkWhitelistedOAuthConsumers(applicationLinkService);
    }

    @Bean
    public RateLimitedRequestLogger rateLimitedRequestLogger() {
        return new RateLimitedRequestLogger();
    }

    @Bean
    public RateLimitUserRequestHandler userRequestHandler(RateLimitingProperties rateLimitingProperties, RateLimitUiRequestHandler rateLimitUiRequestHandler) {
        logger.info("Rate Limited Request Handler ENABLED");
        return new DefaultUserRequestRateLimitHandler(rateLimitingProperties, rateLimitUiRequestHandler);
    }

    @Bean
    public PreAuthRequestDecoder preAuthRequestDecoder(List<PreAuthRequestSingleMethodDecoder> decoders) {
        return new DefaultPreAuthRequestDecoder(decoders);
    }

    @Bean
    public PreAuthRequestSingleMethodDecoder basicAuthRequestDecoder(UserKeyProvider userKeyProvider) {
        return new BasicAuthRequestDecoder(userKeyProvider);
    }

    @Bean
    public SystemRateLimitSettingsResource rateLimitSettingsResource(DmzRateLimitSettingsModificationService rateLimitSettingsService, I18nResolver i18nResolver) {
        return new SystemRateLimitSettingsResource(rateLimitSettingsService, i18nResolver, this.permissionEnforcer);
    }

    @Bean
    public RateLimitHistoryResource rateLimitHistoryResource(I18nResolver i18nResolver, RateLimitingReportService rateLimitHistoryService, UserService userService, PermissionEnforcer permissionEnforcer) {
        return new RateLimitHistoryResource(i18nResolver, rateLimitHistoryService, userService, permissionEnforcer);
    }

    @Bean
    public UserRateLimitSettingsResource userRateLimitSettingsResource(I18nResolver i18nResolver, DmzRateLimitSettingsModificationService rateLimitSettingsService, UserService userService, PermissionEnforcer permissionEnforcer) {
        return new UserRateLimitSettingsResource(i18nResolver, rateLimitSettingsService, userService, permissionEnforcer);
    }

    @Bean
    public UserResource userResource(UserService userService, PermissionEnforcer permissionEnforcer) {
        return new UserResource(userService, permissionEnforcer);
    }

    @Bean
    public final RateLimitingExceptionMapper exceptionMapper() {
        return new RateLimitingExceptionMapper();
    }

    @Bean
    public SystemPropertiesService systemPropertiesService(SystemRateLimitingSettingsDao systemRateLimitingSettingsDao) {
        SystemRateLimitingSettings initialSystemRLSettings = new SystemRateLimitingSettings.Builder().mode(RateLimitingMode.OFF).bucketSettings(new TokenBucketSettings(50, 10, 1, ChronoUnit.SECONDS)).bucketCollectionJobFrequencyDuration(Duration.ofMinutes(5L)).reportingDbArchivingJobFrequencyDuration(Duration.ofSeconds(70L)).reportingDbRetentionPeriodDuration(Duration.ofDays(1L)).settingsReloadPeriodDuration(Duration.ofMinutes(1L)).bucketCleanupJobFrequencyDuration(Duration.ofMinutes(15L)).build();
        return new DefaultSystemPropertiesService(systemRateLimitingSettingsDao, initialSystemRLSettings, true);
    }

    @Bean
    public SystemProperties systemProperties() {
        return new SystemProperties();
    }

    @Bean
    public TokenBucketFactory bucketFactory(RateLimitLightweightAccessService rateLimitSettingsService) {
        return new DefaultTokenBucketFactory(rateLimitSettingsService);
    }

    @Bean
    public RateLimitService rateLimitService(HistoryIntervalManager historyIntervalManager, RateLimitLightweightAccessService rateLimitSettingsService, ClusterEventService clusterEventService, TokenBucketFactory bucketFactory, AnalyticsService analyticsService) {
        return new DefaultRateLimitService(historyIntervalManager, rateLimitSettingsService, clusterEventService, this.eventPublisher, bucketFactory, analyticsService);
    }

    @Bean
    public ClusterEventService clusterEventService(EventPublisher eventPublisher) {
        return new SingleNodeClusterEventService(eventPublisher);
    }

    @Bean
    final RateLimitLightweightAccessService rateLimitLightweightAccessService(SystemRateLimitingSettingsProvider systemRateLimitingSettingsProvider, UserRateLimitingSettingsProvider userRateLimitingSettingsProvider, LicenseChecker licenseChecker, RateLimitingFeatureFlagService rateLimitingFeatureFlagService) {
        return new RateLimitLightweightAccessService(systemRateLimitingSettingsProvider, userRateLimitingSettingsProvider, licenseChecker, rateLimitingFeatureFlagService, true);
    }

    @Bean
    public RateLimitHistoryReportResultMapper historyReportResultMapper(UserService userService, DmzRateLimitSettingsModificationService rateLimitSettingsModificationService) {
        return new DefaultRateLimitHistoryReportResultMapper(userService, rateLimitSettingsModificationService);
    }

    @Bean
    public RateLimitingReportService rateLimitHistoryService(UserRateLimitCounterDao userRateLimitCounterDao, RateLimitHistoryReportResultMapper historyReportResultMapper) {
        return new DefaultRateLimitHistoryService(userRateLimitCounterDao, historyReportResultMapper);
    }

    @Bean
    public AnalyticsService analyticsService(EventPublisher eventPublisher) {
        return new DefaultAnalyticsService(eventPublisher);
    }

    @Bean
    public HistoryIntervalManager historyIntervalManager() {
        return new HistoryIntervalManager();
    }

    @Bean
    public SystemRateLimitingSettingsProvider cachingSystemRateLimitingSettingsProvider(SystemRateLimitingSettingsDao systemRateLimitingSettingsDao) {
        return new CachingSystemRateLimitingSettingsProvider(systemRateLimitingSettingsDao);
    }

    @Bean
    public SystemRateLimitingSettingsDao systemRateLimitingSettingsDao(DatabaseAccessor databaseAccessor, RateLimitingSettingsVersionDao settingsVersionDao) {
        return new QDSLSystemRateLimitingSettingsDao(databaseAccessor, settingsVersionDao);
    }

    @Bean
    public QDSLRateLimitingSettingsVersionDao aORateLimitingSettingsVersionDao(DatabaseAccessor databaseAccessor) {
        return new QDSLRateLimitingSettingsVersionDao(databaseAccessor);
    }

    @Bean
    public CachingUserRateLimitingSettingsProvider cachingUserRateLimitingSettingsProvider(UserRateLimitSettingsDao userRateLimitSettingsDao) {
        return new CachingUserRateLimitingSettingsProvider(userRateLimitSettingsDao);
    }

    @Bean
    public UserRateLimitSettingsDao userRateLimitSettingsDao(DatabaseAccessor databaseAccessor, RateLimitingSettingsVersionDao settingsVersionDao) {
        return new QDSLUserRateLimitSettingsDao(databaseAccessor, settingsVersionDao);
    }

    @Bean
    public UserRateLimitCounterDao userRateLimitCounterDao(DatabaseAccessor databaseAccessor) {
        return new QDSLUserRateLimitCounterDao(databaseAccessor);
    }

    @Bean
    public final SettingsReloaderJob settingsReloaderJob(SystemPropertiesService systemPropertiesService, SystemRateLimitingSettingsProvider cachingSystemRateLimitingSettingsProvider, UserRateLimitingSettingsProvider cachingUserRateLimitingSettingsProvider, RateLimitingProperties rateLimitingProperties, EventPublisher eventPublisher) {
        return new SettingsReloaderJob(systemPropertiesService, cachingSystemRateLimitingSettingsProvider, cachingUserRateLimitingSettingsProvider, rateLimitingProperties, eventPublisher);
    }

    @Bean
    public ScheduledJobSource historyFlushJob(UserRateLimitCounterDao userRateLimitCounterDao, HistoryIntervalManager historyIntervalManager, SystemPropertiesService systemPropertiesService) {
        return new HistoryFlushJob(userRateLimitCounterDao, historyIntervalManager, systemPropertiesService);
    }

    @Bean
    public ScheduledJobSource historyCleanupJob(UserRateLimitCounterDao userRateLimitCounterDao, SystemPropertiesService systemPropertiesService) {
        return new HistoryCleanupJob(userRateLimitCounterDao, systemPropertiesService);
    }

    @Bean
    public ScheduledJobSource reaperJob(RateLimitService rateLimitService, SystemPropertiesService systemPropertiesService) {
        return new RateLimitReaperJob(rateLimitService, systemPropertiesService);
    }

    @Bean
    public ScheduledJobSource analyticsBatchEventsPublisherJob(AnalyticsService analyticsService) {
        return new AnalyticsBatchEventsPublisherJob(analyticsService);
    }

    @Bean
    public ScheduledJobSource configurationLoggerJob(SystemPropertiesService systemPropertiesService) {
        return new ConfigurationLoggerJob(systemPropertiesService);
    }

    @Bean
    public RateLimitingJobScheduler jobScheduler(SchedulerService schedulerService, EventPublisher eventPublisher, List<ScheduledJobSource> rateLimitingJobs, ConfigurationConstants configurationConstants) {
        return new RateLimitingJobScheduler(schedulerService, eventPublisher, rateLimitingJobs, configurationConstants.pluginKey);
    }

    @Bean
    public AuditService internalAuditService(com.atlassian.audit.api.AuditService auditService, LocaleResolver localeResolver, I18nResolver i18nResolver) {
        return new ObservabilityAuditService(auditService, localeResolver, i18nResolver);
    }

    @Bean
    public AuditEntryFactory auditEntryFactory(I18nResolver i18nResolver, UserService userService) {
        return new AuditEntryFactory(i18nResolver, userService);
    }

    @Bean
    public AuditListener settingsEventListener(AuditService auditService, EventPublisher eventPublisher, AuditEntryFactory auditEntryFactory) {
        return new AuditListener(auditService, eventPublisher, auditEntryFactory);
    }

    @Bean
    public PluginChecker publicChecker() {
        return new PluginChecker();
    }

    @Bean(name={"licenseChecker"})
    public LicenseChecker licenseChecker(LicenseHandler licenseHandler, PluginChecker pluginChecker, EventPublisher eventPublisher) {
        return new DefaultLicenseChecker(licenseHandler, pluginChecker, eventPublisher);
    }

    @Bean
    public RateLimitingEnabledCondition rateLimitingEnabledCondition(LicenseChecker licenseChecker, PermissionEnforcer permissionEnforcer) {
        return new RateLimitingEnabledCondition(licenseChecker, permissionEnforcer);
    }

    @Bean
    public RatelimitingServlet ratelimitingServlet(SoyTemplateRenderer templateRenderer, PermissionEnforcer permissionEnforcer, LoginUriProvider loginUriProvider, LicenseChecker licenseChecker) {
        return new RatelimitingServlet(templateRenderer, permissionEnforcer, loginUriProvider, licenseChecker);
    }
}

