/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.cmpt.analytics.events.EventDto
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  org.apache.commons.collections.CollectionUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.newexport;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.entity.GlobalEntityType;
import com.atlassian.migration.agent.entity.MigrateGlobalEntitiesTask;
import com.atlassian.migration.agent.entity.Plan;
import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.newexport.CSVExportTaskContext;
import com.atlassian.migration.agent.newexport.DescriptorBuilder;
import com.atlassian.migration.agent.newexport.Queries;
import com.atlassian.migration.agent.newexport.RapidExporter;
import com.atlassian.migration.agent.newexport.TemplatedQueries;
import com.atlassian.migration.agent.newexport.store.JdbcConfluenceStore;
import com.atlassian.migration.agent.newexport.util.FileUtil;
import com.atlassian.migration.agent.service.NonSpaceTemplateConflictsInfo;
import com.atlassian.migration.agent.service.UserMappingsManager;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.catalogue.model.GlobalEntitiesExecutionState;
import com.atlassian.migration.agent.service.confluence.ConfluenceCloudService;
import com.atlassian.migration.agent.service.extract.GlobalEntityExtractionService;
import com.atlassian.migration.agent.service.impl.StepType;
import com.atlassian.migration.agent.service.user.UserMappingsFileManager;
import com.atlassian.migration.agent.store.StepStore;
import com.atlassian.migration.agent.store.TaskStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalEntitiesRapidExporter
extends RapidExporter<CSVExportTaskContext> {
    private static final Logger log = LoggerFactory.getLogger(GlobalEntitiesRapidExporter.class);
    private static final String ENTITY_NAME = "global_entities";
    private final TaskStore taskStore;
    private final StepStore stepStore;
    private final ConfluenceCloudService confluenceCloudService;
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;
    private final PluginTransactionTemplate ptx;
    private final GlobalEntityExtractionService globalEntityExtractionService;
    private final Map<GlobalEntityType, TemplatedQueries> templateQueryMap = ImmutableMap.of((Object)((Object)GlobalEntityType.GLOBAL_SYSTEM_TEMPLATES), (Object)Queries.globalAndSystemTemplatesQuery, (Object)((Object)GlobalEntityType.GLOBAL_TEMPLATES), (Object)Queries.globalTemplatesQuery, (Object)((Object)GlobalEntityType.SYSTEM_TEMPLATES), (Object)Queries.systemTemplatesQuery);
    private final Map<GlobalEntityType, String> templateModuleKeyMap = ImmutableMap.of((Object)((Object)GlobalEntityType.GLOBAL_SYSTEM_TEMPLATES), (Object)"(MODULEKEY is null or MODULEKEY in ('spacecontent-global', 'spacecontent-personal', 'welcome-message'))", (Object)((Object)GlobalEntityType.GLOBAL_TEMPLATES), (Object)"MODULEKEY is null", (Object)((Object)GlobalEntityType.SYSTEM_TEMPLATES), (Object)"MODULEKEY in ('spacecontent-global', 'spacecontent-personal', 'welcome-message')");

    public GlobalEntitiesRapidExporter(JdbcConfluenceStore confluenceStore, DescriptorBuilder descriptorBuilder, MigrationAgentConfiguration migrationAgentConfiguration, UserMappingsFileManager userMappingsFileManager, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, TaskStore taskStore, StepStore stepStore, ConfluenceCloudService confluenceCloudService, MigrationDarkFeaturesManager migrationDarkFeaturesManager, PluginTransactionTemplate ptx, GlobalEntityExtractionService globalEntityExtractionService) {
        super(confluenceStore, descriptorBuilder, migrationAgentConfiguration, userMappingsFileManager, analyticsEventService, analyticsEventBuilder);
        this.taskStore = taskStore;
        this.stepStore = stepStore;
        this.confluenceCloudService = confluenceCloudService;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
        this.ptx = ptx;
        this.globalEntityExtractionService = globalEntityExtractionService;
    }

    @VisibleForTesting
    public GlobalEntitiesRapidExporter(JdbcConfluenceStore confluenceStore, DescriptorBuilder descriptorBuilder, MigrationAgentConfiguration migrationAgentConfiguration, UserMappingsFileManager userMappingsFileManager, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, Supplier<Instant> instantSupplier, TaskStore taskStore, StepStore stepStore, ConfluenceCloudService confluenceCloudService, MigrationDarkFeaturesManager migrationDarkFeaturesManager, PluginTransactionTemplate ptx, GlobalEntityExtractionService globalEntityExtractionService) {
        super(confluenceStore, descriptorBuilder, migrationAgentConfiguration, userMappingsFileManager, analyticsEventService, analyticsEventBuilder, instantSupplier);
        this.taskStore = taskStore;
        this.stepStore = stepStore;
        this.confluenceCloudService = confluenceCloudService;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
        this.ptx = ptx;
        this.globalEntityExtractionService = globalEntityExtractionService;
    }

    @Override
    protected void logResults(String tableName, CSVExportTaskContext taskContext, long totalTime) {
        log.info("Serialized results of query on table [{}] in [{} ms].", (Object)tableName, (Object)totalTime);
    }

    @Override
    public String export(CSVExportTaskContext taskContext) throws AccessDeniedException {
        String exportDir = FileUtil.createExportDirectory(taskContext.getTempDirFilePath());
        log.info("Starting CSV export of global templates to path: {}", (Object)exportDir);
        long startTime = ((Instant)this.instantSupplier.get()).toEpochMilli();
        HashSet<String> discoveredUserKeys = new HashSet<String>();
        log.info("Try to get user mappings from file for planId: {}", (Object)taskContext.getPlanId());
        Map<String, String> mappings = this.userMappingsFileManager.getUserMappingsFromFile(taskContext.getPlanId());
        UserMappingsManager userMappingsManager = new UserMappingsManager(this.migrationDarkFeaturesManager, mappings);
        this.runTemplateQuery(taskContext, exportDir, discoveredUserKeys);
        this.exportUserMappings(exportDir, discoveredUserKeys, taskContext, userMappingsManager);
        this.exportFileCount(exportDir, taskContext, ENTITY_NAME);
        this.descriptorBuilder.generateNonSpaceDescriptor(exportDir, taskContext.getTotalRowCount());
        log.info("Completed CSV export of global templates in {}ms", (Object)(((Instant)this.instantSupplier.get()).toEpochMilli() - startTime));
        log.info("Exported CSV files of global templates are located in directory {}", (Object)exportDir);
        return exportDir;
    }

    private void runTemplateQuery(CSVExportTaskContext taskContext, String exportDir, Set<String> discoveredUserKeys) {
        MigrateGlobalEntitiesTask migrateGlobalEntitiesTask = (MigrateGlobalEntitiesTask)this.taskStore.getTask(taskContext.getTaskId());
        NonSpaceTemplateConflictsInfo conflictsInfo = this.confluenceCloudService.getNonSpaceTemplateConflictsInfo(migrateGlobalEntitiesTask.getGlobalEntityType(), taskContext.getCloudId());
        List<String> conflictingTemplateIds = conflictsInfo.getConflicts().stream().map(conflict -> conflict.serverTemplateId).collect(Collectors.toList());
        String conflictingIdClauses = this.buildConflictingIdClauses(conflictingTemplateIds);
        this.runQueries(this.templateQueryMap.get((Object)migrateGlobalEntitiesTask.getGlobalEntityType()).toQueries(this.getDbType(), conflictingIdClauses, conflictingIdClauses), this.createQueryRunner(Collections.emptyMap(), discoveredUserKeys), exportDir, taskContext);
        this.setExecutionStateAndBuildAndSaveGlobalEntitiesConflictingStepEvent(conflictsInfo, taskContext.getPlanId(), taskContext.getTaskId());
        String labelIdClauses = conflictingIdClauses.replace("TEMPLATEID", "PAGETEMPLATEID");
        this.exportLabels(taskContext, exportDir, discoveredUserKeys, labelIdClauses, this.templateModuleKeyMap.get((Object)migrateGlobalEntitiesTask.getGlobalEntityType()));
    }

    private void exportLabels(CSVExportTaskContext taskContext, String exportDir, Set<String> discoveredUserKeys, String templateIdClausesString, String moduleKey) {
        this.runQueries(Queries.contentLabelQueriesForGlobalTemplates.toQueries(this.getDbType(), templateIdClausesString, moduleKey), this.createQueryRunner(Collections.emptyMap(), discoveredUserKeys), exportDir, taskContext);
    }

    private void setExecutionStateAndBuildAndSaveGlobalEntitiesConflictingStepEvent(NonSpaceTemplateConflictsInfo conflictsInfo, String planId, String taskId) {
        long numOfGlobalPageTemplatesMigrated = conflictsInfo.getTotalNumOfTemplatesMigrated(GlobalEntityType.GLOBAL_TEMPLATES);
        long numOfSystemTemplatesMigrated = conflictsInfo.getTotalNumOfTemplatesMigrated(GlobalEntityType.SYSTEM_TEMPLATES);
        Optional optionalStep = this.ptx.read(() -> this.stepStore.getStepsByTaskId(taskId).stream().filter(s -> s.getType().equals(StepType.GLOBAL_ENTITIES_EXPORT.name())).findFirst());
        if (optionalStep.isPresent()) {
            Step step = (Step)optionalStep.get();
            GlobalEntitiesExecutionState file = new GlobalEntitiesExecutionState(this.globalEntityExtractionService.getGlobalTemplatesCount(), this.globalEntityExtractionService.getSystemTemplatesCount(), numOfGlobalPageTemplatesMigrated, numOfSystemTemplatesMigrated);
            step.setExecutionState(Jsons.valueAsString(file));
            this.ptx.write(() -> this.stepStore.update(step));
        }
        Plan plan = this.ptx.read(() -> this.taskStore.getTask(taskId).getPlan());
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildGlobalEntitiesConflictingExportStepEvent(numOfGlobalPageTemplatesMigrated, numOfSystemTemplatesMigrated, planId, taskId, plan.getMigrationId()));
    }

    private String buildConflictingIdClauses(List<String> templateIds) {
        ArrayList<String> clauses = new ArrayList<String>();
        if (CollectionUtils.isEmpty(templateIds)) {
            clauses.add("1 = 1");
        } else {
            ArrayList templateKeyBatches = Lists.newArrayList((Iterable)Iterables.partition(templateIds, (int)1000));
            for (List templateKeyBatch : templateKeyBatches) {
                StringBuilder sb = new StringBuilder();
                for (String key : templateKeyBatch) {
                    sb.append(String.format("%s,", key));
                }
                sb.deleteCharAt(sb.length() - 1);
                clauses.add(String.format("TEMPLATEID NOT in (%s)", sb));
            }
        }
        return String.join((CharSequence)" and ", clauses);
    }

    @Override
    public void reportExportTablePerformance(CSVExportTaskContext taskContext, boolean success, String tableName, String query, String dbType, long totalTimeTaken, long timeToFirstRow, long rowsExported, long totalContentChars) {
        EventDto tableExportEvent = this.analyticsEventBuilder.buildGlobalEntitiesTableExportedToCSVTimerEvent(success, totalTimeTaken, taskContext.getPlanId(), taskContext.getCloudId(), taskContext.getTaskId(), tableName, query, dbType, timeToFirstRow, rowsExported, totalContentChars);
        this.analyticsEventService.saveAnalyticsEventAsync(() -> tableExportEvent);
    }
}

