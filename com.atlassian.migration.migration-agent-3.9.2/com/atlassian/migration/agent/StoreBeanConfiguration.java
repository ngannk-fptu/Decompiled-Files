/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.scheduler.SchedulerService
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.migration.agent;

import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.CommonBeanConfiguration;
import com.atlassian.migration.agent.ImportedOsgiServiceBeans;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.newexport.store.JdbcConfluenceStore;
import com.atlassian.migration.agent.service.TeamCalendarHelper;
import com.atlassian.migration.agent.store.AttachmentMigrationStore;
import com.atlassian.migration.agent.store.AttachmentStore;
import com.atlassian.migration.agent.store.CloudSiteStore;
import com.atlassian.migration.agent.store.ContentStatisticsStore;
import com.atlassian.migration.agent.store.IncorrectEmailStore;
import com.atlassian.migration.agent.store.InvalidEmailUserStore;
import com.atlassian.migration.agent.store.MapiPlanMappingStore;
import com.atlassian.migration.agent.store.MapiTaskMappingStore;
import com.atlassian.migration.agent.store.PlanStore;
import com.atlassian.migration.agent.store.SpaceStatisticStore;
import com.atlassian.migration.agent.store.StatsStore;
import com.atlassian.migration.agent.store.StepProgressPropertiesStore;
import com.atlassian.migration.agent.store.StepStore;
import com.atlassian.migration.agent.store.TaskStore;
import com.atlassian.migration.agent.store.TombstoneAccountStore;
import com.atlassian.migration.agent.store.UserBaseScanStore;
import com.atlassian.migration.agent.store.UserDomainRuleStore;
import com.atlassian.migration.agent.store.UserMappingStore;
import com.atlassian.migration.agent.store.guardrails.GuardrailsBrowserMetricsStore;
import com.atlassian.migration.agent.store.guardrails.GuardrailsBrowserMetricsStoreImpl;
import com.atlassian.migration.agent.store.guardrails.GuardrailsResponseGroupStore;
import com.atlassian.migration.agent.store.guardrails.GuardrailsResponseGroupStoreImpl;
import com.atlassian.migration.agent.store.guardrails.GuardrailsResponseStore;
import com.atlassian.migration.agent.store.guardrails.GuardrailsResponseStoreImpl;
import com.atlassian.migration.agent.store.guardrails.InstanceAnalysisControlStore;
import com.atlassian.migration.agent.store.guardrails.InstanceAnalysisControlStoreImpl;
import com.atlassian.migration.agent.store.impl.AnalyticsEventStore;
import com.atlassian.migration.agent.store.impl.AppAccessScopeStore;
import com.atlassian.migration.agent.store.impl.AppAssessmentInfoStore;
import com.atlassian.migration.agent.store.impl.AttachmentMigrationStoreImpl;
import com.atlassian.migration.agent.store.impl.AttachmentStoreImpl;
import com.atlassian.migration.agent.store.impl.CheckOverrideStore;
import com.atlassian.migration.agent.store.impl.CheckResultStore;
import com.atlassian.migration.agent.store.impl.CloudSiteStoreImpl;
import com.atlassian.migration.agent.store.impl.ConfluenceSpaceTaskStoreImpl;
import com.atlassian.migration.agent.store.impl.ContentStatisticsStoreImpl;
import com.atlassian.migration.agent.store.impl.DetectedEmailEventLogStore;
import com.atlassian.migration.agent.store.impl.ExportCacheStoreImpl;
import com.atlassian.migration.agent.store.impl.IncorrectEmailStoreImpl;
import com.atlassian.migration.agent.store.impl.InvalidEmailUserStoreImpl;
import com.atlassian.migration.agent.store.impl.MapiPlanMappingStoreImpl;
import com.atlassian.migration.agent.store.impl.MapiTaskMappingStoreImpl;
import com.atlassian.migration.agent.store.impl.MigratedSpaceStore;
import com.atlassian.migration.agent.store.impl.PlanStoreImpl;
import com.atlassian.migration.agent.store.impl.ProgressStoreImpl;
import com.atlassian.migration.agent.store.impl.RecentlyViewedStore;
import com.atlassian.migration.agent.store.impl.SpacePermissionStore;
import com.atlassian.migration.agent.store.impl.SpaceStatisticStoreImpl;
import com.atlassian.migration.agent.store.impl.SpaceStore;
import com.atlassian.migration.agent.store.impl.StatsStoreImpl;
import com.atlassian.migration.agent.store.impl.StepProgressPropertiesStoreImpl;
import com.atlassian.migration.agent.store.impl.StepStoreImpl;
import com.atlassian.migration.agent.store.impl.TaskStoreImpl;
import com.atlassian.migration.agent.store.impl.TombstoneAccountStoreImpl;
import com.atlassian.migration.agent.store.impl.UserBaseScanStoreImpl;
import com.atlassian.migration.agent.store.impl.UserDomainRuleStoreImpl;
import com.atlassian.migration.agent.store.impl.UserMappingStoreImpl;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import com.atlassian.migration.agent.store.jpa.impl.ConfluenceWrapperDataSource;
import com.atlassian.scheduler.SchedulerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(value={ImportedOsgiServiceBeans.class, CommonBeanConfiguration.class})
@Configuration
public class StoreBeanConfiguration {
    @Bean
    public PlanStore planStore(EntityManagerTemplate tmpl) {
        return new PlanStoreImpl(tmpl);
    }

    @Bean
    public ContentStatisticsStore contentStatisticsStore(EntityManagerTemplate tmpl, MigrationAgentConfiguration config, SpaceManager spaceManager, TeamCalendarHelper teamCalendarHelper, MigrationAgentConfiguration migrationAgentConfiguration) {
        return new ContentStatisticsStoreImpl(tmpl, config, spaceManager, teamCalendarHelper, migrationAgentConfiguration);
    }

    @Bean
    public CloudSiteStore cloudSiteStore(EntityManagerTemplate tmpl) {
        return new CloudSiteStoreImpl(tmpl);
    }

    @Bean
    public AttachmentMigrationStore attachmentMigrationStore(EntityManagerTemplate tmpl) {
        return new AttachmentMigrationStoreImpl(tmpl);
    }

    @Bean
    public InvalidEmailUserStore invalidEmailUserStore(EntityManagerTemplate tmpl) {
        return new InvalidEmailUserStoreImpl(tmpl);
    }

    @Bean
    public StatsStore statsStore(EntityManagerTemplate tmpl) {
        return new StatsStoreImpl(tmpl);
    }

    @Bean
    public StepStore stepStore(EntityManagerTemplate tmpl) {
        return new StepStoreImpl(tmpl);
    }

    @Bean
    public StepProgressPropertiesStore stepProgressPropertiesStore(EntityManagerTemplate tmpl) {
        return new StepProgressPropertiesStoreImpl(tmpl);
    }

    @Bean
    public CheckResultStore checkResultStore(EntityManagerTemplate tmpl) {
        return new CheckResultStore(tmpl);
    }

    @Bean
    public CheckOverrideStore checkOverrideStore(EntityManagerTemplate tmpl) {
        return new CheckOverrideStore(tmpl);
    }

    @Bean
    public RecentlyViewedStore recentlyViewedStore(EntityManagerTemplate tmpl, MigrationAgentConfiguration migrationAgentConfiguration) {
        return new RecentlyViewedStore(tmpl, migrationAgentConfiguration);
    }

    @Bean
    public AttachmentStore attachmentStore(EntityManagerTemplate tmpl) {
        return new AttachmentStoreImpl(tmpl);
    }

    @Bean
    public SpacePermissionStore spacePermissionStore(EntityManagerTemplate tmpl, MigrationAgentConfiguration migrationAgentConfiguration) {
        return new SpacePermissionStore(tmpl, migrationAgentConfiguration);
    }

    @Bean
    public TaskStore taskStore(EntityManagerTemplate tmpl) {
        return new TaskStoreImpl(tmpl);
    }

    @Bean
    public ConfluenceSpaceTaskStoreImpl confluenceSpaceTaskStoreImpl(EntityManagerTemplate tmpl, MigrationAgentConfiguration config) {
        return new ConfluenceSpaceTaskStoreImpl(tmpl, config);
    }

    @Bean
    public ExportCacheStoreImpl exportCacheStoreImpl(EntityManagerTemplate tmpl) {
        return new ExportCacheStoreImpl(tmpl);
    }

    @Bean
    public MigratedSpaceStore migratedSpaceStore(EntityManagerTemplate tmpl) {
        return new MigratedSpaceStore(tmpl);
    }

    @Bean
    public ProgressStoreImpl progressStoreImpl(EntityManagerTemplate tmpl) {
        return new ProgressStoreImpl(tmpl);
    }

    @Bean
    public SpaceStore spaceStore(EntityManagerTemplate tmpl, MigratedSpaceStore migratedSpaceStore, JdbcConfluenceStore confluenceStore, MigrationDarkFeaturesManager migrationDarkFeaturesManager, SchedulerService schedulerService) {
        return new SpaceStore(tmpl, migratedSpaceStore, confluenceStore, migrationDarkFeaturesManager, schedulerService);
    }

    @Bean
    public JdbcConfluenceStore jdbcConfluenceStore(ConfluenceWrapperDataSource dataSource, MigrationAgentConfiguration config) {
        return new JdbcConfluenceStore(dataSource, config);
    }

    @Bean
    public AppAssessmentInfoStore appAssessmentInfoStore(EntityManagerTemplate tmpl) {
        return new AppAssessmentInfoStore(tmpl);
    }

    @Bean
    public AppAccessScopeStore appAccessScopeStore(EntityManagerTemplate tmpl) {
        return new AppAccessScopeStore(tmpl);
    }

    @Bean
    public AnalyticsEventStore analyticsEventStore(EntityManagerTemplate tmpl) {
        return new AnalyticsEventStore(tmpl);
    }

    @Bean
    public DetectedEmailEventLogStore eventLogStore(EntityManagerTemplate tmpl) {
        return new DetectedEmailEventLogStore(tmpl);
    }

    @Bean
    public UserDomainRuleStore userDomainRuleStore(EntityManagerTemplate tmpl) {
        return new UserDomainRuleStoreImpl(tmpl);
    }

    @Bean
    public UserMappingStore userMappingStore(EntityManagerTemplate tmpl) {
        return new UserMappingStoreImpl(tmpl);
    }

    @Bean
    public GuardrailsResponseGroupStore guardRailsResponseGroupStore(EntityManagerTemplate tmpl) {
        return new GuardrailsResponseGroupStoreImpl(tmpl);
    }

    @Bean
    public GuardrailsResponseStore guardrailsResponseStore(EntityManagerTemplate tmpl) {
        return new GuardrailsResponseStoreImpl(tmpl);
    }

    @Bean
    public IncorrectEmailStore incorrectEmailStore(EntityManagerTemplate tmpl) {
        return new IncorrectEmailStoreImpl(tmpl);
    }

    @Bean
    public UserBaseScanStore userBaseScanStore(EntityManagerTemplate tmpl) {
        return new UserBaseScanStoreImpl(tmpl);
    }

    @Bean
    public SpaceStatisticStore spaceStatisticStore(EntityManagerTemplate entityManagerTemplate) {
        return new SpaceStatisticStoreImpl(entityManagerTemplate);
    }

    @Bean
    public TombstoneAccountStore tombstoneAccountStore(EntityManagerTemplate entityManagerTemplate) {
        return new TombstoneAccountStoreImpl(entityManagerTemplate);
    }

    @Bean
    public MapiPlanMappingStore mapiPlanMappingStore(EntityManagerTemplate tmpl) {
        return new MapiPlanMappingStoreImpl(tmpl);
    }

    @Bean
    public InstanceAnalysisControlStore instanceAnalysisControlStore(EntityManagerTemplate tmpl) {
        return new InstanceAnalysisControlStoreImpl(tmpl);
    }

    @Bean
    public GuardrailsBrowserMetricsStore guardrailsBrowserMetricsStore(EntityManagerTemplate tmpl) {
        return new GuardrailsBrowserMetricsStoreImpl(tmpl);
    }

    @Bean
    public MapiTaskMappingStore mapiTaskMappingStore(EntityManagerTemplate tmpl) {
        return new MapiTaskMappingStoreImpl(tmpl);
    }
}

