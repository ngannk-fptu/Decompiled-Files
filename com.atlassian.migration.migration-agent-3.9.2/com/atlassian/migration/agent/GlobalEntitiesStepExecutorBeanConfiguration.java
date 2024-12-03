/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.BootstrapManager
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.migration.agent;

import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.ExportBeanConfiguration;
import com.atlassian.migration.agent.ServiceBeanConfiguration;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.newexport.GlobalEntitiesRapidExporter;
import com.atlassian.migration.agent.service.ExportDirManager;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.catalogue.MigrationCatalogueStorageService;
import com.atlassian.migration.agent.service.confluence.ConfluenceCloudService;
import com.atlassian.migration.agent.service.stepexecutor.ProgressTracker;
import com.atlassian.migration.agent.service.stepexecutor.globalentities.GlobalEntitiesExportExecutor;
import com.atlassian.migration.agent.service.stepexecutor.globalentities.GlobalEntitiesImportExecutor;
import com.atlassian.migration.agent.service.stepexecutor.globalentities.GlobalEntitiesUploadExecutor;
import com.atlassian.migration.agent.store.StepStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(value={ServiceBeanConfiguration.class, ExportBeanConfiguration.class})
@Configuration
public class GlobalEntitiesStepExecutorBeanConfiguration {
    @Bean
    public GlobalEntitiesExportExecutor globalEntitiesExportExecutor(ExportDirManager exportDirManager, BootstrapManager bootstrapManager, StepStore stepStore, PluginTransactionTemplate ptx, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, MigrationDarkFeaturesManager migrationDarkFeaturesManager, GlobalEntitiesRapidExporter rapidExporter, MigrationAgentConfiguration migrationAgentConfiguration) {
        return new GlobalEntitiesExportExecutor(exportDirManager, bootstrapManager, stepStore, ptx, analyticsEventService, analyticsEventBuilder, migrationDarkFeaturesManager, rapidExporter, migrationAgentConfiguration);
    }

    @Bean
    public GlobalEntitiesUploadExecutor globalEntitiesUploadExecutor(ExportDirManager exportDirManager, StepStore stepStore, PluginTransactionTemplate ptx, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, MigrationCatalogueStorageService migrationCatalogueStorageService) {
        return new GlobalEntitiesUploadExecutor(exportDirManager, stepStore, ptx, analyticsEventService, analyticsEventBuilder, migrationCatalogueStorageService);
    }

    @Bean
    public GlobalEntitiesImportExecutor globalEntitiesImportExecutor(ProgressTracker progressTracker, StepStore stepStore, ConfluenceCloudService confluenceCloudService, PluginTransactionTemplate ptx, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, MigrationAgentConfiguration migrationAgentConfiguration) {
        return new GlobalEntitiesImportExecutor(progressTracker, stepStore, confluenceCloudService, ptx, analyticsEventService, analyticsEventBuilder, migrationAgentConfiguration);
    }
}

