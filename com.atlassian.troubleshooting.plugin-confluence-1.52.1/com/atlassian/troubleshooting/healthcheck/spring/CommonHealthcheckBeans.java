/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.activeobjects.external.TransactionalAnnotationProcessor
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.sal.api.message.HelpPathResolver
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.scheduling.PluginScheduler
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.usersettings.UserSettingsService
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.troubleshooting.healthcheck.spring;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.external.TransactionalAnnotationProcessor;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugins.osgi.javaconfig.ExportOptions;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.message.HelpPathResolver;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.scheduling.PluginScheduler;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.usersettings.UserSettingsService;
import com.atlassian.troubleshooting.api.ClusterService;
import com.atlassian.troubleshooting.api.PluginInfo;
import com.atlassian.troubleshooting.api.healthcheck.DatabaseService;
import com.atlassian.troubleshooting.api.healthcheck.ExecutorServiceFactory;
import com.atlassian.troubleshooting.api.healthcheck.FileSystemInfo;
import com.atlassian.troubleshooting.api.healthcheck.IndexInfoService;
import com.atlassian.troubleshooting.api.healthcheck.LogFileHelper;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheckManager;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheckSupplier;
import com.atlassian.troubleshooting.api.supportzip.SupportZipBundle;
import com.atlassian.troubleshooting.healthcheck.DefaultSupportHealthCheckSupplier;
import com.atlassian.troubleshooting.healthcheck.SupportHealthStatusBuilder;
import com.atlassian.troubleshooting.healthcheck.api.HealthCheckProcessFactory;
import com.atlassian.troubleshooting.healthcheck.api.HealthCheckUserSettingsService;
import com.atlassian.troubleshooting.healthcheck.checks.FontHealthCheck;
import com.atlassian.troubleshooting.healthcheck.checks.FontManagerChecker;
import com.atlassian.troubleshooting.healthcheck.checks.NetworkMountHealthCheck;
import com.atlassian.troubleshooting.healthcheck.checks.ThreadLimitHealthCheck;
import com.atlassian.troubleshooting.healthcheck.checks.ThreadLimitHealthCheckCondition;
import com.atlassian.troubleshooting.healthcheck.checks.auditing.AuditLogCapacityHealthCheck;
import com.atlassian.troubleshooting.healthcheck.checks.conditions.CodeCacheCondition;
import com.atlassian.troubleshooting.healthcheck.checks.conditions.DisabledCondition;
import com.atlassian.troubleshooting.healthcheck.checks.datacenter.NonDCLicenseCondition;
import com.atlassian.troubleshooting.healthcheck.checks.datacenter.database.clustered.aws.aurora.AmazonAuroraDCOnlyCheck;
import com.atlassian.troubleshooting.healthcheck.checks.eol.ClockFactory;
import com.atlassian.troubleshooting.healthcheck.checks.eol.DefaultClockFactory;
import com.atlassian.troubleshooting.healthcheck.checks.eol.DefaultProductReleaseDateManager;
import com.atlassian.troubleshooting.healthcheck.checks.eol.EolSupportHealthCheck;
import com.atlassian.troubleshooting.healthcheck.checks.eol.ProductReleaseDateManager;
import com.atlassian.troubleshooting.healthcheck.checks.http.BrowserEventListenerRegistrar;
import com.atlassian.troubleshooting.healthcheck.checks.http.NetworkPerformanceStatisticsService;
import com.atlassian.troubleshooting.healthcheck.checks.http.PageProtocolsHealthCheck;
import com.atlassian.troubleshooting.healthcheck.checks.http.ProtocolsEventProvider;
import com.atlassian.troubleshooting.healthcheck.checks.jfr.JavaFlightRecorderHealthCheck;
import com.atlassian.troubleshooting.healthcheck.checks.vuln.CveValidator;
import com.atlassian.troubleshooting.healthcheck.checks.vuln.SecurityVulnerabilityHealthCheck;
import com.atlassian.troubleshooting.healthcheck.concurrent.DefaultExecutorServiceFactory;
import com.atlassian.troubleshooting.healthcheck.impl.DefaultHealthCheckProcessFactory;
import com.atlassian.troubleshooting.healthcheck.impl.DefaultLogFileHelper;
import com.atlassian.troubleshooting.healthcheck.impl.DefaultSupportHealthCheckManager;
import com.atlassian.troubleshooting.healthcheck.impl.HealthCheckEmailNotifier;
import com.atlassian.troubleshooting.healthcheck.impl.HealthCheckTimeoutListener;
import com.atlassian.troubleshooting.healthcheck.persistence.service.HealthCheckDisabledService;
import com.atlassian.troubleshooting.healthcheck.persistence.service.HealthCheckDisabledServiceImpl;
import com.atlassian.troubleshooting.healthcheck.persistence.service.HealthCheckUserSettingsServiceImpl;
import com.atlassian.troubleshooting.healthcheck.persistence.service.HealthCheckWatcherService;
import com.atlassian.troubleshooting.healthcheck.persistence.service.HealthCheckWatcherServiceImpl;
import com.atlassian.troubleshooting.healthcheck.persistence.service.HealthStatusPersistenceService;
import com.atlassian.troubleshooting.healthcheck.persistence.service.HealthStatusPersistenceServiceImpl;
import com.atlassian.troubleshooting.healthcheck.persistence.service.HealthStatusPropertiesPersistenceService;
import com.atlassian.troubleshooting.healthcheck.persistence.service.HealthStatusPropertiesPersistenceServiceImpl;
import com.atlassian.troubleshooting.healthcheck.persistence.service.NotificationService;
import com.atlassian.troubleshooting.healthcheck.persistence.service.NotificationServiceImpl;
import com.atlassian.troubleshooting.healthcheck.rest.HealthCheckResource;
import com.atlassian.troubleshooting.healthcheck.rest.HealthCheckUserSettingsResource;
import com.atlassian.troubleshooting.healthcheck.scheduler.HealthCheckJob;
import com.atlassian.troubleshooting.healthcheck.scheduler.HealthCheckScheduler;
import com.atlassian.troubleshooting.healthcheck.scheduler.HealthCheckSchedulerImpl;
import com.atlassian.troubleshooting.healthcheck.util.MapHelper;
import com.atlassian.troubleshooting.spring.CommonBeans;
import com.atlassian.troubleshooting.stp.mxbean.MXBeanProvider;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.salext.bundle.HealthcheckResultsBundle;
import com.atlassian.troubleshooting.stp.salext.mail.MailUtility;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value={CommonBeans.class, ProtocolsEventProvider.class, AuditLogCapacityHealthCheck.class, CodeCacheCondition.class, DisabledCondition.class, NonDCLicenseCondition.class, ThreadLimitHealthCheckCondition.class, BrowserEventListenerRegistrar.class, NetworkPerformanceStatisticsService.class, PageProtocolsHealthCheck.class, HealthCheckResource.class, HealthCheckUserSettingsResource.class, HealthCheckJob.class, MapHelper.class, HealthcheckResultsBundle.class, SecurityVulnerabilityHealthCheck.class, CveValidator.class, JavaFlightRecorderHealthCheck.class})
public class CommonHealthcheckBeans {
    @Bean
    public AmazonAuroraDCOnlyCheck amazonAuroraDCOnlyCheck(ApplicationProperties applicationProperties, DatabaseService databaseService, SupportHealthStatusBuilder supportHealthStatusBuilder) {
        return new AmazonAuroraDCOnlyCheck(databaseService, applicationProperties, supportHealthStatusBuilder);
    }

    @Bean
    public ClockFactory clockFactory() {
        return new DefaultClockFactory();
    }

    @Bean
    public EolSupportHealthCheck eolSupportHealthCheck(ApplicationProperties applicationProperties, ClockFactory clockFactory, EventPublisher eventPublisher, LocaleResolver localeResolver, ProductReleaseDateManager productReleaseDateManager, SupportHealthStatusBuilder supportHealthStatusBuilder) {
        return new EolSupportHealthCheck(applicationProperties, localeResolver, clockFactory, supportHealthStatusBuilder, eventPublisher, productReleaseDateManager);
    }

    @Bean
    public ExecutorServiceFactory executorServiceFactory() {
        return new DefaultExecutorServiceFactory();
    }

    @Bean
    public FontHealthCheck fontHealthCheck(FontManagerChecker fontManagerChecker, SupportHealthStatusBuilder supportHealthStatusBuilder) {
        return new FontHealthCheck(supportHealthStatusBuilder, fontManagerChecker);
    }

    @Bean
    public FontManagerChecker fontManagerChecker() {
        return new FontManagerChecker();
    }

    @Bean
    public HealthCheckProcessFactory healthCheckProcessFactory(HealthCheckTimeoutListener healthCheckTimeoutListener, HealthStatusPersistenceService healthStatusPersistenceService, ExecutorServiceFactory executorServiceFactory, ClusterService clusterService) {
        return new DefaultHealthCheckProcessFactory(healthCheckTimeoutListener, healthStatusPersistenceService, executorServiceFactory, clusterService);
    }

    @Bean
    public HealthCheckScheduler healthCheckScheduler(HealthStatusPersistenceService healthStatusPersistenceService, HealthStatusPropertiesPersistenceService healthStatusPropertiesPersistenceService, NotificationService notificationService, PluginScheduler pluginScheduler, SupportHealthCheckManager supportHealthCheckManager, EventPublisher eventPublisher, PluginInfo pluginInfo) {
        return new HealthCheckSchedulerImpl(healthStatusPersistenceService, healthStatusPropertiesPersistenceService, notificationService, pluginScheduler, supportHealthCheckManager, eventPublisher, pluginInfo);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportHealthCheckScheduler(HealthCheckScheduler healthCheckScheduler) {
        return OsgiServices.exportOsgiService(healthCheckScheduler, ExportOptions.as(LifecycleAware.class, new Class[0]));
    }

    @Bean
    public HealthCheckTimeoutListener healthCheckTimeoutListener(ApplicationProperties applicationProperties, EventPublisher eventPublisher) {
        return new HealthCheckTimeoutListener(applicationProperties, eventPublisher);
    }

    @Bean
    public HealthCheckUserSettingsService healthCheckUserSettingsService(UserSettingsService userSettingsService, HealthCheckWatcherService watcherService, MailUtility mailUtility) {
        return new HealthCheckUserSettingsServiceImpl(userSettingsService, watcherService, mailUtility);
    }

    @Bean
    public HealthStatusPersistenceService healthStatusPersistenceService(ActiveObjects activeObjects, HealthStatusPropertiesPersistenceService healthStatusPropertiesPersistenceService, EventPublisher eventPublisher, ClusterService clusterService) {
        return new HealthStatusPersistenceServiceImpl(activeObjects, healthStatusPropertiesPersistenceService, eventPublisher, clusterService);
    }

    @Bean
    public HealthStatusPropertiesPersistenceService healthStatusPropertiesPersistenceService(ActiveObjects activeObjects) {
        return new HealthStatusPropertiesPersistenceServiceImpl(activeObjects);
    }

    @Bean
    public HealthCheckWatcherService healthCheckWatcherService(ActiveObjects ao) {
        return new HealthCheckWatcherServiceImpl(ao);
    }

    @Bean
    public LogFileHelper logFileHelper(MXBeanProvider mxBeanProvider) {
        return new DefaultLogFileHelper(mxBeanProvider);
    }

    @Bean
    public NetworkMountHealthCheck networkMountHealthCheck(FileSystemInfo fileSystemInfo, IndexInfoService indexInfo, SupportHealthStatusBuilder healthStatusBuilder) {
        return new NetworkMountHealthCheck(fileSystemInfo, indexInfo, healthStatusBuilder);
    }

    @Bean
    public NotificationService notificationService(ActiveObjects activeObjects, HealthCheckUserSettingsService healthCheckUserSettingsService, HealthStatusPersistenceService healthStatusPersistenceService, HealthStatusPropertiesPersistenceService healthStatusPropertiesPersistenceService, ClockFactory clockFactory) {
        return new NotificationServiceImpl(activeObjects, healthCheckUserSettingsService, healthStatusPersistenceService, healthStatusPropertiesPersistenceService, clockFactory);
    }

    @Bean
    public ProductReleaseDateManager productReleaseDateManager() {
        return new DefaultProductReleaseDateManager();
    }

    @Bean
    public SupportHealthCheckManager supportHealthCheckManager(HealthCheckProcessFactory healthCheckProcessFactory, SupportHealthCheckSupplier healthCheckSupplier) {
        return new DefaultSupportHealthCheckManager(healthCheckProcessFactory, healthCheckSupplier);
    }

    @Bean
    public HealthCheckDisabledService healthCheckDisabledService(ActiveObjects ao) {
        return new HealthCheckDisabledServiceImpl(ao);
    }

    @Bean
    public SupportHealthCheckSupplier supportHealthCheckSupplier(I18nResolver i18nResolver, PluginAccessor pluginAccessor, HealthCheckDisabledService healthCheckDisabledService) {
        return new DefaultSupportHealthCheckSupplier(pluginAccessor, i18nResolver, healthCheckDisabledService);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportSupportHealthCheckSupplier(SupportHealthCheckSupplier supportHealthCheckSupplier) {
        return OsgiServices.exportOsgiService(supportHealthCheckSupplier, ExportOptions.as(SupportHealthCheckSupplier.class, new Class[0]));
    }

    @Bean
    public SupportHealthStatusBuilder supportHealthStatusBuilder(ApplicationProperties applicationProperties, HelpPathResolver helpPathResolver, I18nResolver i18nResolver, SupportHealthCheckSupplier supportHealthCheckSupplier, ClusterService clusterService) {
        return new SupportHealthStatusBuilder(i18nResolver, applicationProperties, helpPathResolver, supportHealthCheckSupplier, clusterService);
    }

    @Bean
    public HealthCheckEmailNotifier healthCheckEmailNotifier(EventPublisher eventPublisher, MailUtility mailUtility, HealthCheckWatcherService watcherService, UserManager userManager, SupportApplicationInfo info, HealthCheckUserSettingsService userSettingsService) {
        return new HealthCheckEmailNotifier(eventPublisher, mailUtility, watcherService, userManager, info, userSettingsService);
    }

    @Bean
    public ThreadLimitHealthCheck threadLimitHealthCheck(ApplicationProperties applicationProperties, FileSystemInfo fileSystemInfo, SupportHealthStatusBuilder supportHealthStatusBuilder) {
        return new ThreadLimitHealthCheck(fileSystemInfo, applicationProperties, supportHealthStatusBuilder);
    }

    @Bean
    public TransactionalAnnotationProcessor transactionalAnnotationProcessor(ActiveObjects activeObjects) {
        return new TransactionalAnnotationProcessor(activeObjects);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportHealthcheckResultsBundle(HealthcheckResultsBundle healthcheckResultsBundle) {
        return OsgiServices.exportOsgiService(healthcheckResultsBundle, ExportOptions.as(SupportZipBundle.class, new Class[0]));
    }
}

