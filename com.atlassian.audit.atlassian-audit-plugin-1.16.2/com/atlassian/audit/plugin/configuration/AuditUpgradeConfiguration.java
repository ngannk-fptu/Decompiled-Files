/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.audit.api.AuditRetentionConfigService
 *  com.atlassian.audit.spi.migration.LegacyAuditEntityMigrator
 *  com.atlassian.audit.spi.migration.LegacyRetentionConfigProvider
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  com.google.common.collect.ImmutableList
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.audit.plugin.configuration;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.audit.ao.consumer.DatabaseAuditConsumer;
import com.atlassian.audit.api.AuditRetentionConfigService;
import com.atlassian.audit.cache.schedule.BuildCacheJobScheduler;
import com.atlassian.audit.plugin.configuration.PermissionsNotEnforced;
import com.atlassian.audit.plugin.configuration.condition.NonJira;
import com.atlassian.audit.plugin.upgrade.AuditUpgradeTask;
import com.atlassian.audit.plugin.upgrade.UpgradeTaskCollector;
import com.atlassian.audit.plugin.upgrade.task.UpgradeTask1MigrateLegacyEvents;
import com.atlassian.audit.plugin.upgrade.task.UpgradeTask2MigrateLegacyRetentionConfig;
import com.atlassian.audit.plugin.upgrade.task.UpgradeTask3MigrateJiraCategories;
import com.atlassian.audit.plugin.upgrade.task.UpgradeTask3Noop;
import com.atlassian.audit.plugin.upgrade.task.UpgradeTask4BuildActionAndCategoriesCache;
import com.atlassian.audit.spi.migration.LegacyAuditEntityMigrator;
import com.atlassian.audit.spi.migration.LegacyRetentionConfigProvider;
import com.atlassian.plugins.osgi.javaconfig.ExportOptions;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.JiraOnly;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditUpgradeConfiguration {
    @Bean
    public UpgradeTask1MigrateLegacyEvents migrateLegacyEventsUpgradeTask(LegacyAuditEntityMigrator legacyAuditEntityMigrator, DatabaseAuditConsumer databaseAuditConsumer) {
        return new UpgradeTask1MigrateLegacyEvents(legacyAuditEntityMigrator, databaseAuditConsumer);
    }

    @Bean
    public UpgradeTask2MigrateLegacyRetentionConfig upgradeTask2MigrateRetentionConfig(@PermissionsNotEnforced AuditRetentionConfigService auditRetentionConfigService, LegacyRetentionConfigProvider configProvider) {
        return new UpgradeTask2MigrateLegacyRetentionConfig(auditRetentionConfigService, configProvider);
    }

    @Bean
    @Conditional(value={JiraOnly.class})
    public UpgradeTask3MigrateJiraCategories upgradeTask3MigrateJiraIssues(ActiveObjects ao) {
        return new UpgradeTask3MigrateJiraCategories(ao);
    }

    @Bean
    public UpgradeTask3Noop upgradeTask3Noop() {
        return new UpgradeTask3Noop();
    }

    @Bean
    public UpgradeTask4BuildActionAndCategoriesCache upgradeTask4MigrateUiFilters(BuildCacheJobScheduler buildCacheJobScheduler) {
        return new UpgradeTask4BuildActionAndCategoriesCache(buildCacheJobScheduler);
    }

    @Bean
    @Conditional(value={NonJira.class})
    public FactoryBean<ServiceRegistration> publishMigrateLegacyEventsUpgradeTask(UpgradeTask1MigrateLegacyEvents migrateLegacyEventsUpgradeTask) {
        return OsgiServices.exportOsgiService(migrateLegacyEventsUpgradeTask, ExportOptions.as(PluginUpgradeTask.class, new Class[0]));
    }

    @Bean
    @Conditional(value={NonJira.class})
    public FactoryBean<ServiceRegistration> publishMigrateRetentionConfigUpgradeTask(UpgradeTask2MigrateLegacyRetentionConfig upgradeTask2MigrateLegacyRetentionConfig) {
        return OsgiServices.exportOsgiService(upgradeTask2MigrateLegacyRetentionConfig, ExportOptions.as(PluginUpgradeTask.class, new Class[0]));
    }

    @Bean
    @Conditional(value={NonJira.class})
    public FactoryBean<ServiceRegistration> publishUpgradeTask3Noop(UpgradeTask3Noop upgradeTask3Noop) {
        return OsgiServices.exportOsgiService(upgradeTask3Noop, ExportOptions.as(PluginUpgradeTask.class, new Class[0]));
    }

    @Bean
    @Conditional(value={NonJira.class})
    public FactoryBean<ServiceRegistration> publishUpgradeTask4MigrateUiFilters(UpgradeTask4BuildActionAndCategoriesCache upgradeTask4BuildActionAndCategoriesCache) {
        return OsgiServices.exportOsgiService(upgradeTask4BuildActionAndCategoriesCache, ExportOptions.as(PluginUpgradeTask.class, new Class[0]));
    }

    @Bean
    @Conditional(value={JiraOnly.class})
    public UpgradeTaskCollector upgradeTaskCollector(UpgradeTask1MigrateLegacyEvents upgradeTask1MigrateLegacyEvents, UpgradeTask2MigrateLegacyRetentionConfig upgradeTask2MigrateLegacyRetentionConfig, UpgradeTask3MigrateJiraCategories upgradeTask3MigrateJiraCategories, UpgradeTask4BuildActionAndCategoriesCache upgradeTask4BuildActionAndCategoriesCache) {
        return new UpgradeTaskCollector((Collection<AuditUpgradeTask>)ImmutableList.of((Object)upgradeTask1MigrateLegacyEvents, (Object)upgradeTask2MigrateLegacyRetentionConfig, (Object)upgradeTask3MigrateJiraCategories, (Object)upgradeTask4BuildActionAndCategoriesCache));
    }
}

