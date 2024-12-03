/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.audit.api.AuditRetentionConfigService
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.scheduler.SchedulerService
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.audit.plugin.configuration;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.audit.ao.dao.AuditEntityDao;
import com.atlassian.audit.api.AuditRetentionConfigService;
import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.coverage.AnalyticsTrackedCoverageConfigService;
import com.atlassian.audit.coverage.AuditedCoverageConfigService;
import com.atlassian.audit.coverage.InternalAuditCoverageConfigService;
import com.atlassian.audit.coverage.LicenseAwareCoverageConfigService;
import com.atlassian.audit.coverage.ProductLicenseChecker;
import com.atlassian.audit.coverage.RestrictiveCoverageConfigService;
import com.atlassian.audit.coverage.SalAuditCoverageConfigService;
import com.atlassian.audit.denylist.AnalyticsTrackedExcludedActionsService;
import com.atlassian.audit.denylist.AuditedExcludedActionsService;
import com.atlassian.audit.denylist.DefaultExcludedActionsService;
import com.atlassian.audit.denylist.ExcludedActionsProvider;
import com.atlassian.audit.denylist.ExcludedActionsService;
import com.atlassian.audit.denylist.ExcludedActionsUpdater;
import com.atlassian.audit.denylist.RestrictiveExcludedActionsService;
import com.atlassian.audit.file.AuditRetentionFileConfigLicenseChecker;
import com.atlassian.audit.file.AuditRetentionFileConfigService;
import com.atlassian.audit.permission.PermissionChecker;
import com.atlassian.audit.plugin.AuditPluginInfo;
import com.atlassian.audit.plugin.configuration.PermissionsEnforced;
import com.atlassian.audit.plugin.configuration.PermissionsNotEnforced;
import com.atlassian.audit.plugin.configuration.PropertiesProvider;
import com.atlassian.audit.plugin.configuration.SystemThenSalPropertiesProvider;
import com.atlassian.audit.retention.RestrictiveRetentionConfigService;
import com.atlassian.audit.retention.RestrictiveRetentionFileConfigService;
import com.atlassian.audit.retention.RetentionScheduler;
import com.atlassian.audit.retention.SalAuditRetentionConfigService;
import com.atlassian.audit.retention.SalAuditRetentionFileConfigService;
import com.atlassian.audit.schedule.AuditScheduler;
import com.atlassian.audit.schedule.db.limit.DbLimiterScheduler;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.scheduler.SchedulerService;
import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditConfigConfiguration {
    @Bean
    @PermissionsEnforced
    public InternalAuditCoverageConfigService restrictiveAuditCoverageConfigService(PermissionChecker permissionChecker, @PermissionsNotEnforced InternalAuditCoverageConfigService configService, EventPublisher eventPublisher, AuditPluginInfo auditPluginInfo, AuditService auditService) {
        return new RestrictiveCoverageConfigService(permissionChecker, new AnalyticsTrackedCoverageConfigService(new AuditedCoverageConfigService(configService, auditService), eventPublisher, auditPluginInfo));
    }

    @Bean
    @PermissionsNotEnforced
    public InternalAuditCoverageConfigService internalAuditCoverageConfigService(PluginSettingsFactory factory, ProductLicenseChecker licenseChecker, EventPublisher eventPublisher) {
        return new LicenseAwareCoverageConfigService(licenseChecker, new SalAuditCoverageConfigService(factory, eventPublisher));
    }

    @Bean
    @PermissionsEnforced
    public Supplier<AuditRetentionConfigService> restrictiveRetentionConfigServiceSupplier(@PermissionsEnforced AuditRetentionConfigService configService) {
        return () -> configService;
    }

    @Bean
    @PermissionsEnforced
    public AuditRetentionConfigService restrictiveRetentionConfigService(PermissionChecker permissionChecker, @PermissionsNotEnforced AuditRetentionConfigService configService) {
        return new RestrictiveRetentionConfigService(permissionChecker, configService);
    }

    @Bean
    @PermissionsEnforced
    public AuditRetentionFileConfigService restrictiveRetentionFileConfigService(PermissionChecker permissionChecker, @PermissionsNotEnforced AuditRetentionFileConfigService configService, AuditRetentionFileConfigLicenseChecker auditRetentionFileConfigLicenseChecker) {
        return new RestrictiveRetentionFileConfigService(permissionChecker, configService, auditRetentionFileConfigLicenseChecker);
    }

    @Bean
    public AuditRetentionFileConfigLicenseChecker auditRetentionFileConfigLicenseChecker(ProductLicenseChecker productLicenseChecker, ApplicationProperties applicationProperties) {
        return new AuditRetentionFileConfigLicenseChecker(productLicenseChecker, applicationProperties);
    }

    @Bean
    @PermissionsNotEnforced
    public AuditRetentionConfigService auditRetentionConfigService(EventPublisher eventPublisher, AuditPluginInfo auditPluginInfo, PluginSettingsFactory pluginSettingsFactory, AuditService auditService) {
        return new SalAuditRetentionConfigService(eventPublisher, auditPluginInfo, pluginSettingsFactory, auditService);
    }

    @Bean
    @PermissionsNotEnforced
    public AuditRetentionFileConfigService auditRetentionFileConfigService(EventPublisher eventPublisher, AuditPluginInfo auditPluginInfo, PluginSettingsFactory pluginSettingsFactory, AuditService auditService, PropertiesProvider propertiesProvider) {
        return new SalAuditRetentionFileConfigService(eventPublisher, auditPluginInfo, pluginSettingsFactory, auditService, propertiesProvider);
    }

    @Bean
    public RetentionScheduler retentionScheduler(SchedulerService schedulerService, @PermissionsNotEnforced AuditRetentionConfigService auditRetentionConfigService, AuditEntityDao auditEntityDao, PluginSettingsFactory pluginSettingsFactory, PropertiesProvider propertiesProvider) {
        return new RetentionScheduler(schedulerService, auditRetentionConfigService, auditEntityDao, pluginSettingsFactory, propertiesProvider);
    }

    @Bean
    public DbLimiterScheduler dbLimiterScheduler(SchedulerService schedulerService, AuditEntityDao auditEntityDao, PropertiesProvider propertiesProvider) {
        return new DbLimiterScheduler(schedulerService, auditEntityDao, propertiesProvider);
    }

    @Bean
    public AuditScheduler auditScheduler(AuditPluginInfo auditPluginInfo, EventPublisher eventPublisher, RetentionScheduler retentionScheduler, DbLimiterScheduler dbLimiterScheduler) {
        return new AuditScheduler(auditPluginInfo, eventPublisher, retentionScheduler, dbLimiterScheduler);
    }

    @Bean
    public ExcludedActionsProvider denyListProvider(ActiveObjects ao, TransactionTemplate transactionTemplate, PropertiesProvider propertiesProvider) {
        int refreshIntervalInSeconds = propertiesProvider.getInteger("plugin.audit.deny.list.actions.cache.refresh.seconds", 60);
        return new ExcludedActionsProvider(ao, transactionTemplate, refreshIntervalInSeconds);
    }

    @Bean
    public ExcludedActionsUpdater denyListUpdater(ActiveObjects ao, TransactionTemplate transactionTemplate) {
        return new ExcludedActionsUpdater(ao, transactionTemplate);
    }

    @Bean
    public ExcludedActionsService denyListService(ExcludedActionsUpdater excludedActionsUpdater, ExcludedActionsProvider excludedActionsProvider, AuditService auditService, EventPublisher eventPublisher, AuditPluginInfo auditPluginInfo, PermissionChecker permissionChecker) {
        return new RestrictiveExcludedActionsService(new AnalyticsTrackedExcludedActionsService(new AuditedExcludedActionsService(new DefaultExcludedActionsService(excludedActionsUpdater, excludedActionsProvider), auditService), eventPublisher, auditPluginInfo), permissionChecker);
    }

    @Bean
    public PropertiesProvider propertiesProvider(ApplicationProperties applicationProperties) {
        return new SystemThenSalPropertiesProvider(applicationProperties);
    }
}

