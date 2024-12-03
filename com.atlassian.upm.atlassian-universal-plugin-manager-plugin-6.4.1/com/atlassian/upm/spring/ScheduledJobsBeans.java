/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.scheduler.SchedulerService
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.upm.spring;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.upm.core.BundledUpdateInfoStore;
import com.atlassian.upm.core.PluginDownloadService;
import com.atlassian.upm.core.PluginInstallationService;
import com.atlassian.upm.core.async.AsynchronousTaskManager;
import com.atlassian.upm.core.log.AuditLogService;
import com.atlassian.upm.license.internal.HostLicenseEventReader;
import com.atlassian.upm.license.internal.HostLicenseProvider;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import com.atlassian.upm.mac.HostLicenseUpdatedEventListener;
import com.atlassian.upm.notification.NotificationCache;
import com.atlassian.upm.notification.PluginLicenseNotificationChecker;
import com.atlassian.upm.notification.PluginRequestNotificationChecker;
import com.atlassian.upm.notification.PluginUpdateChecker;
import com.atlassian.upm.schedule.BundledUpdateCheckJob;
import com.atlassian.upm.schedule.LocalPluginLicenseNotificationJob;
import com.atlassian.upm.schedule.NotificationCacheUpdateEventListener;
import com.atlassian.upm.schedule.PluginRequestCheckJob;
import com.atlassian.upm.schedule.PluginUpdateCheckJob;
import com.atlassian.upm.schedule.UpmScheduler;
import com.atlassian.upm.schedule.UpmSchedulerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScheduledJobsBeans {
    @Bean
    public BundledUpdateCheckJob bundledUpdateCheckJob(ApplicationProperties applicationProperties, AsynchronousTaskManager asynchronousTaskManager, AuditLogService auditLogService, BundledUpdateInfoStore bundledUpdateInfoStore, I18nResolver i18nResolver, PluginAccessor pluginAccessor, PluginDownloadService pluginDownloadService, PluginInstallationService pluginInstallationService, UpmScheduler scheduler) {
        return new BundledUpdateCheckJob(applicationProperties, asynchronousTaskManager, auditLogService, bundledUpdateInfoStore, i18nResolver, pluginAccessor, pluginDownloadService, pluginInstallationService, scheduler);
    }

    @Bean
    public HostLicenseUpdatedEventListener hostLicenseUpdatedEventListener(EventPublisher eventPublisher, HostLicenseEventReader hostLicenseEventReader, PluginLicenseNotificationChecker notificationChecker, PluginLicenseRepository licenseRepository, HostLicenseProvider hostLicenseProvider) {
        return new HostLicenseUpdatedEventListener(eventPublisher, hostLicenseEventReader, notificationChecker, licenseRepository, hostLicenseProvider);
    }

    @Bean
    public LocalPluginLicenseNotificationJob localPluginLicenseNotificationJob(PluginLicenseNotificationChecker notificationChecker, NotificationCache cache, UpmScheduler scheduler) {
        return new LocalPluginLicenseNotificationJob(notificationChecker, cache, scheduler);
    }

    @Bean
    public NotificationCacheUpdateEventListener notificationCacheUpdateEventListener(EventPublisher eventPublisher, NotificationCache cache) {
        return new NotificationCacheUpdateEventListener(eventPublisher, cache);
    }

    @Bean
    public PluginRequestCheckJob pluginRequestCheckJob(PluginRequestNotificationChecker updateChecker, UpmScheduler scheduler) {
        return new PluginRequestCheckJob(updateChecker, scheduler);
    }

    @Bean
    public PluginUpdateCheckJob pluginUpdateCheckJob(PluginUpdateChecker updateChecker, UpmScheduler scheduler) {
        return new PluginUpdateCheckJob(updateChecker, scheduler);
    }

    @Bean
    public UpmScheduler upmScheduler(ThreadLocalDelegateExecutorFactory executorFactory, TransactionTemplate txTemplate, SchedulerService pluginScheduler) {
        return new UpmSchedulerImpl(executorFactory, txTemplate, pluginScheduler);
    }
}

