/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.scheduler.SchedulerService
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.migration.agent;

import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.ServiceBeanConfiguration;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.newexport.DescriptorBuilder;
import com.atlassian.migration.agent.newexport.GlobalEntitiesRapidExporter;
import com.atlassian.migration.agent.newexport.SpaceRapidExporter;
import com.atlassian.migration.agent.newexport.store.JdbcConfluenceStore;
import com.atlassian.migration.agent.service.TeamCalendarHelper;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.confluence.ConfluenceCloudService;
import com.atlassian.migration.agent.service.extract.GlobalEntityExtractionService;
import com.atlassian.migration.agent.service.impl.DefaultExportDirManager;
import com.atlassian.migration.agent.service.impl.SENSupplier;
import com.atlassian.migration.agent.service.stepexecutor.export.SpaceExportCacheService;
import com.atlassian.migration.agent.service.user.UserMappingsFileManager;
import com.atlassian.migration.agent.service.version.PluginVersionManager;
import com.atlassian.migration.agent.store.ExportCacheStore;
import com.atlassian.migration.agent.store.StepStore;
import com.atlassian.migration.agent.store.TaskStore;
import com.atlassian.migration.agent.store.jpa.impl.ConfluenceWrapperDataSource;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.scheduler.SchedulerService;
import java.sql.SQLException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(value={ServiceBeanConfiguration.class})
@Configuration
public class ExportBeanConfiguration {
    @Bean
    public DefaultExportDirManager defaultExportDirManager(BootstrapManager bootstrapManager) {
        return new DefaultExportDirManager(bootstrapManager);
    }

    @Bean
    public DescriptorBuilder descriptorBuilder(BootstrapManager bootstrapManager, SENSupplier senSupplier, PluginVersionManager pluginVersionManager, SystemInformationService sysInfoService, MigrationAgentConfiguration migrationAgentConfiguration) {
        return new DescriptorBuilder(bootstrapManager, senSupplier, pluginVersionManager, sysInfoService, migrationAgentConfiguration);
    }

    @Bean
    public GlobalEntitiesRapidExporter globalEntitiesRapidExporter(JdbcConfluenceStore confluenceStore, DescriptorBuilder descriptorBuilder, MigrationAgentConfiguration migrationAgentConfiguration, UserMappingsFileManager userMappingsFileManager, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, TaskStore taskStore, StepStore stepStore, ConfluenceCloudService confluenceCloudService, MigrationDarkFeaturesManager migrationDarkFeaturesManager, PluginTransactionTemplate ptx, GlobalEntityExtractionService globalEntityExtractionService) {
        return new GlobalEntitiesRapidExporter(confluenceStore, descriptorBuilder, migrationAgentConfiguration, userMappingsFileManager, analyticsEventService, analyticsEventBuilder, taskStore, stepStore, confluenceCloudService, migrationDarkFeaturesManager, ptx, globalEntityExtractionService);
    }

    @Bean
    public SpaceRapidExporter spaceRapidExporter(JdbcConfluenceStore confluenceStore, DescriptorBuilder descriptorBuilder, MigrationAgentConfiguration migrationAgentConfiguration, UserMappingsFileManager userMappingsFileManager, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, TeamCalendarHelper teamCalendarHelper, MigrationDarkFeaturesManager migrationDarkFeaturesManager) {
        return new SpaceRapidExporter(confluenceStore, descriptorBuilder, migrationAgentConfiguration, userMappingsFileManager, analyticsEventService, analyticsEventBuilder, teamCalendarHelper, migrationDarkFeaturesManager);
    }

    @Bean
    public SpaceExportCacheService spaceExportCacheService(ConfluenceWrapperDataSource dataSource, SpaceManager spaceManager, TransactionTemplate transactionTemplate, SchedulerService schedulerService, PluginTransactionTemplate ptx, ExportCacheStore exportCacheStore, DescriptorBuilder descriptorBuilder) throws SQLException {
        return new SpaceExportCacheService(dataSource, spaceManager, transactionTemplate, schedulerService, ptx, exportCacheStore, descriptorBuilder);
    }
}

