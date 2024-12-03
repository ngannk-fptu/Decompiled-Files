/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.migration.agent;

import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.ExportBeanConfiguration;
import com.atlassian.migration.agent.ServiceBeanConfiguration;
import com.atlassian.migration.agent.UserGroupsBeanConfiguration;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.newexport.SpaceRapidExporter;
import com.atlassian.migration.agent.service.ClusterLimits;
import com.atlassian.migration.agent.service.ExportDirManager;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.catalogue.EnterpriseGatekeeperClient;
import com.atlassian.migration.agent.service.catalogue.MigrationCatalogueStorageService;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.confluence.ConfluenceCloudService;
import com.atlassian.migration.agent.service.stepexecutor.ProgressTracker;
import com.atlassian.migration.agent.service.stepexecutor.TombstoneMappingsPublisher;
import com.atlassian.migration.agent.service.stepexecutor.export.SpaceExportCacheService;
import com.atlassian.migration.agent.service.stepexecutor.space.SpaceExportExecutor;
import com.atlassian.migration.agent.service.stepexecutor.space.SpaceImportExecutor;
import com.atlassian.migration.agent.service.stepexecutor.space.SpaceUploadExecutor;
import com.atlassian.migration.agent.service.stepexecutor.space.SpaceUsersMigrationExecutor;
import com.atlassian.migration.agent.service.stepexecutor.space.helper.SpaceImportConfigFileManager;
import com.atlassian.migration.agent.service.user.RetryingUsersMigrationService;
import com.atlassian.migration.agent.service.user.UsersMigrationRequestBuilder;
import com.atlassian.migration.agent.store.StepStore;
import com.atlassian.migration.agent.store.impl.MigratedSpaceStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(value={ServiceBeanConfiguration.class, UserGroupsBeanConfiguration.class, ExportBeanConfiguration.class})
@Configuration
public class SpaceStepExecutorBeanConfiguration {
    @Bean
    public SpaceExportExecutor spaceExportExecutor(ExportDirManager exportDirManager, BootstrapManager bootstrapManager, StepStore stepStore, PluginTransactionTemplate ptx, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, MigrationDarkFeaturesManager migrationDarkFeaturesManager, SpaceRapidExporter rapidExporter, SpaceExportCacheService cacheService, SpaceManager spaceManager, MigrationAgentConfiguration migrationAgentConfiguration, ClusterLimits clusterLimits) {
        return new SpaceExportExecutor(exportDirManager, bootstrapManager, stepStore, ptx, analyticsEventService, analyticsEventBuilder, migrationDarkFeaturesManager, rapidExporter, cacheService, spaceManager, migrationAgentConfiguration, clusterLimits);
    }

    @Bean
    public SpaceUsersMigrationExecutor spaceUsersMigrationExecutor(StepStore stepStore, PluginTransactionTemplate pluginTransactionTemplate, MigrationDarkFeaturesManager migrationDarkFeaturesManager, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, SpaceManager spaceManager, ExportDirManager exportDirManager, UsersMigrationRequestBuilder usersMigrationRequestBuilder, RetryingUsersMigrationService usersMigrationService, EnterpriseGatekeeperClient enterpriseGatekeeperClient, TombstoneMappingsPublisher tombstoneMappingsPublisher) {
        return new SpaceUsersMigrationExecutor(stepStore, pluginTransactionTemplate, migrationDarkFeaturesManager, analyticsEventService, analyticsEventBuilder, spaceManager, exportDirManager, usersMigrationRequestBuilder, usersMigrationService, enterpriseGatekeeperClient, tombstoneMappingsPublisher);
    }

    @Bean
    public SpaceImportExecutor spaceImportExecutor(ProgressTracker progressTracker, StepStore stepStore, PluginTransactionTemplate ptx, SpaceManager spaceManager, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, MigratedSpaceStore migratedSpaceStore, MigrationDarkFeaturesManager darkFeaturesManager, MigrationAgentConfiguration migrationAgentConfiguration, ConfluenceCloudService confluenceCloudService, CloudSiteService cloudSiteService, SpaceImportConfigFileManager spaceImportConfigFileManager, ClusterLimits clusterLimits) {
        return new SpaceImportExecutor(progressTracker, stepStore, ptx, spaceManager, analyticsEventService, analyticsEventBuilder, migratedSpaceStore, darkFeaturesManager, migrationAgentConfiguration, confluenceCloudService, cloudSiteService, spaceImportConfigFileManager, clusterLimits);
    }

    @Bean
    public SpaceUploadExecutor spaceUploadExecutor(ProgressTracker progressTracker, ExportDirManager exportDirManager, StepStore stepStore, PluginTransactionTemplate ptx, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, MigrationCatalogueStorageService migrationCatalogueStorageService, SpaceManager spaceManager, SpaceImportConfigFileManager spaceImportConfigFileManager, ClusterLimits clusterLimits) {
        return new SpaceUploadExecutor(progressTracker, exportDirManager, stepStore, ptx, analyticsEventService, analyticsEventBuilder, migrationCatalogueStorageService, spaceManager, spaceImportConfigFileManager, clusterLimits);
    }
}

