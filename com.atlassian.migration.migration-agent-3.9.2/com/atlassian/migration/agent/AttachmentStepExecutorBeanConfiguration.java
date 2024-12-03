/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.migration.agent;

import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.ServiceBeanConfiguration;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.media.MediaClientTokenSupplier;
import com.atlassian.migration.agent.media.MediaFileUploaderFactory;
import com.atlassian.migration.agent.service.AttachmentService;
import com.atlassian.migration.agent.service.ClusterLimits;
import com.atlassian.migration.agent.service.StatisticsService;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.check.CheckOverrideService;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.stepexecutor.ProgressTracker;
import com.atlassian.migration.agent.service.stepexecutor.attachment.AttachmentDataProvider;
import com.atlassian.migration.agent.service.stepexecutor.attachment.AttachmentMigrationAnalyticsService;
import com.atlassian.migration.agent.service.stepexecutor.attachment.AttachmentMigrationChecker;
import com.atlassian.migration.agent.service.stepexecutor.attachment.AttachmentMigrationExecutor;
import com.atlassian.migration.agent.service.stepexecutor.attachment.AttachmentMigrator;
import com.atlassian.migration.agent.store.StepStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(value={ServiceBeanConfiguration.class})
@Configuration
public class AttachmentStepExecutorBeanConfiguration {
    @Bean
    public AttachmentDataProvider attachmentDataProvider(AttachmentManager attachmentManager, TransactionTemplate transactionTemplate, SystemInformationService systemInformationService) {
        return new AttachmentDataProvider(attachmentManager, transactionTemplate, systemInformationService);
    }

    @Bean
    public AttachmentMigrationAnalyticsService attachmentMigrationAnalyticsService(AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, ClusterLimits clusterLimits) {
        return new AttachmentMigrationAnalyticsService(analyticsEventService, analyticsEventBuilder, clusterLimits);
    }

    @Bean
    public AttachmentMigrationChecker attachmentMigrationChecker(BootstrapManager bootstrapManager, MigrationDarkFeaturesManager migrationDarkFeaturesManager) {
        return new AttachmentMigrationChecker(bootstrapManager, migrationDarkFeaturesManager);
    }

    @Bean
    public AttachmentMigrator attachmentMigrator(AttachmentService attachmentService, MediaFileUploaderFactory mediaFileUploaderFactory, AttachmentDataProvider attachmentDataProvider) {
        return new AttachmentMigrator(attachmentService, mediaFileUploaderFactory, attachmentDataProvider);
    }

    @Bean
    public AttachmentMigrationExecutor attachmentMigrationExecutor(ProgressTracker progressTracker, AttachmentService attachmentService, AttachmentMigrator attachmentMigrator, CloudSiteService cloudSiteService, MigrationAgentConfiguration migrationAgentConfiguration, StatisticsService statisticsService, MediaClientTokenSupplier mediaClientTokenSupplier, PluginTransactionTemplate ptx, StepStore stepStore, AttachmentMigrationChecker attachmentMigrationChecker, AttachmentMigrationAnalyticsService attachmentMigrationAnalyticsService, MigrationDarkFeaturesManager darkFeaturesManager, SpaceManager spaceManager, CheckOverrideService checkOverrideService) {
        return new AttachmentMigrationExecutor(progressTracker, attachmentService, attachmentMigrator, cloudSiteService, migrationAgentConfiguration, statisticsService, mediaClientTokenSupplier, ptx, stepStore, attachmentMigrationChecker, attachmentMigrationAnalyticsService, darkFeaturesManager, spaceManager, checkOverrideService);
    }
}

