/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.cmpt.analytics.events.EventDto
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.stepexecutor.globalentities;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.dto.GlobalEntitiesExportStepConfig;
import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.export.MigrationExportException;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.newexport.CSVExportTaskContext;
import com.atlassian.migration.agent.newexport.GlobalEntitiesRapidExporter;
import com.atlassian.migration.agent.service.ExportDirManager;
import com.atlassian.migration.agent.service.MigrationErrorCode;
import com.atlassian.migration.agent.service.ServiceInitializeException;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.analytics.ErrorEvent;
import com.atlassian.migration.agent.service.impl.StepType;
import com.atlassian.migration.agent.service.stepexecutor.ExportExecutor;
import com.atlassian.migration.agent.service.stepexecutor.StepResult;
import com.atlassian.migration.agent.store.StepStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
public class GlobalEntitiesExportExecutor
extends ExportExecutor {
    private static final Logger log = ContextLoggerFactory.getLogger(GlobalEntitiesExportExecutor.class);
    private final GlobalEntitiesRapidExporter rapidExporter;
    private static final int CONCURRENCY_LEVEL = 1;

    public GlobalEntitiesExportExecutor(ExportDirManager exportDirManager, BootstrapManager bootstrapManager, StepStore stepStore, PluginTransactionTemplate ptx, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, MigrationDarkFeaturesManager migrationDarkFeaturesManager, GlobalEntitiesRapidExporter rapidExporter, MigrationAgentConfiguration migrationAgentConfiguration) {
        super(exportDirManager, bootstrapManager, stepStore, ptx, analyticsEventService, analyticsEventBuilder, migrationDarkFeaturesManager, migrationAgentConfiguration);
        this.rapidExporter = rapidExporter;
    }

    @Override
    public StepType getStepType() {
        return StepType.GLOBAL_ENTITIES_EXPORT;
    }

    @Override
    public StepResult runStep(String stepId) {
        Step step = this.ptx.read(() -> this.stepStore.getStep(stepId));
        return this.doExport(step.getConfig(), step.getId());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @VisibleForTesting
    StepResult doExport(String config, String stepId) {
        long startTime = ((Instant)this.instantSupplier.get()).toEpochMilli();
        GlobalEntitiesExportStepConfig exportStepConfig = Jsons.readValue(config, GlobalEntitiesExportStepConfig.class);
        Step step = this.stepStore.getStep(stepId);
        String planId = step.getPlan().getId();
        String taskId = step.getTask().getId();
        String migrationId = step.getPlan().getMigrationId();
        String cloudId = exportStepConfig.getCloudId();
        String fileId = exportStepConfig.getFileId();
        long freeHeapSizeAtStart = Runtime.getRuntime().freeMemory();
        if (StringUtils.isBlank((String)fileId)) {
            this.checkIfGoodEventAndSaveAnalyticEventsWhenStepFails(MigrationErrorCode.GLOBAL_ENTITIES_EXPORT_NO_FILE_ID, migrationId, cloudId, MigrationErrorCode.GLOBAL_ENTITIES_EXPORT_NO_FILE_ID.getMessage());
            return StepResult.failed("Attempted to perform global entity export, but no file ID was found.");
        }
        if (StringUtils.isBlank((String)cloudId)) {
            this.checkIfGoodEventAndSaveAnalyticEventsWhenStepFails(MigrationErrorCode.GLOBAL_ENTITIES_EXPORT_NO_CLOUD_ID, migrationId, cloudId, MigrationErrorCode.GLOBAL_ENTITIES_EXPORT_NO_CLOUD_ID.getMessage());
            return StepResult.failed("Attempted to perform global entity export, but no cloud ID was found.");
        }
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildGlobalEntitiesExportImportStartEvent(step, startTime));
        boolean success = false;
        try {
            String exportedFile = this.exportContentToFile(planId, taskId, cloudId);
            if (!this.migrationDarkFeaturesManager.isExportOnlyEnabled()) {
                this.exportDirManager.moveCompressedFilesToSharedHome(exportedFile, fileId);
            }
            this.buildAndSaveMetricsWhenStepSucceeds();
            success = true;
            log.info("Global entities export successful for planId: {} migrationId: {}", (Object)planId, (Object)migrationId);
            StepResult stepResult = StepResult.succeeded("Global entities data export successful", fileId);
            return stepResult;
        }
        catch (MigrationExportException ex) {
            success = this.checkIfGoodEventAndSaveAnalyticEventsWhenStepFails(MigrationErrorCode.GLOBAL_ENTITIES_EXPORT_FILE_EXPORT_FAILED, migrationId, cloudId, ex.getMessage());
            StepResult stepResult = this.failedStepResult((Exception)ex, migrationId, exportStepConfig, MigrationErrorCode.GLOBAL_ENTITIES_EXPORT_FILE_EXPORT_FAILED);
            return stepResult;
        }
        catch (AccessDeniedException ex) {
            success = this.checkIfGoodEventAndSaveAnalyticEventsWhenStepFails(MigrationErrorCode.MISSING_WRITE_TO_DIRECTORY_PERMISSIONS, migrationId, cloudId, ex.getMessage());
            StepResult stepResult = this.failedStepResult((Exception)ex, migrationId, exportStepConfig, MigrationErrorCode.MISSING_WRITE_TO_DIRECTORY_PERMISSIONS);
            return stepResult;
        }
        catch (ServiceInitializeException ex) {
            success = this.checkIfGoodEventAndSaveAnalyticEventsWhenStepFails(MigrationErrorCode.GLOBAL_ENTITIES_EXPORT_DIRECTORY_CREATION_FAILED, migrationId, cloudId, ex.getMessage());
            StepResult stepResult = this.failedStepResult((Exception)ex, migrationId, exportStepConfig, MigrationErrorCode.GLOBAL_ENTITIES_EXPORT_DIRECTORY_CREATION_FAILED);
            return stepResult;
        }
        catch (Exception ex) {
            success = this.checkIfGoodEventAndSaveAnalyticEventsWhenStepFails(MigrationErrorCode.GLOBAL_ENTITIES_EXPORT_EXECUTION_ERROR, migrationId, cloudId, ex.getMessage());
            StepResult stepResult = this.failedStepResult(ex, migrationId, exportStepConfig, MigrationErrorCode.GLOBAL_ENTITIES_EXPORT_EXECUTION_ERROR);
            return stepResult;
        }
        finally {
            ImmutableMap additionalAttributes = ImmutableMap.of((Object)"stepSuccessful", (Object)String.valueOf(success));
            EventDto completedStepEvent = this.analyticsEventBuilder.buildCompletedStepAnalyticsEvent(step, () -> GlobalEntitiesExportExecutor.lambda$doExport$2((Map)additionalAttributes));
            EventDto heapSizeAnalyticsEvent = this.analyticsEventBuilder.buildStepLevelHeapSizeAnalyticsEvent(step, freeHeapSizeAtStart, 1, 1);
            this.analyticsEventService.saveAnalyticsEvents(() -> ImmutableList.of((Object)completedStepEvent, (Object)heapSizeAnalyticsEvent));
            this.buildAndSaveStepTimerEvent(success, ((Instant)this.instantSupplier.get()).toEpochMilli() - startTime, planId, cloudId, taskId);
        }
    }

    private String exportContentToFile(String planId, String taskId, String cloudId) throws AccessDeniedException {
        CSVExportTaskContext config = new CSVExportTaskContext(cloudId, planId, taskId, this.tempDirFilePath.getAbsolutePath());
        String exportedFile = this.rapidExporter.export(config);
        this.createExportDirectoryIfNotExists();
        return exportedFile;
    }

    private boolean checkIfGoodEventAndSaveAnalyticEventsWhenStepFails(MigrationErrorCode migrationErrorCode, String migrationId, String cloudId, String message) {
        boolean isSloGoodEvent = migrationErrorCode.shouldBeTreatedAsGoodEventInReliabilitySlo();
        ErrorEvent errorEvent = new ErrorEvent.ErrorEventBuilder(migrationErrorCode, migrationErrorCode.getContainerType(), migrationId, this.getStepType()).setCloudid(cloudId).setReason(message).build();
        ImmutableList events = ImmutableList.of((Object)this.analyticsEventBuilder.buildFailedGlobalEntitiesMigration(this.getStepType(), migrationErrorCode), (Object)this.analyticsEventBuilder.buildGlobalEntitiesExportStepCounterEvent(isSloGoodEvent), (Object)this.analyticsEventBuilder.buildErrorOperationalEvent(errorEvent));
        this.analyticsEventService.sendAnalyticsEventsAsync(() -> GlobalEntitiesExportExecutor.lambda$checkIfGoodEventAndSaveAnalyticEventsWhenStepFails$4((List)events));
        return isSloGoodEvent;
    }

    private void buildAndSaveStepTimerEvent(boolean success, long duration, String planId, String cloudId, String taskId) {
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildGlobalEntitiesExportStepTimerEvent(success, duration, planId, cloudId, taskId));
    }

    private void buildAndSaveMetricsWhenStepSucceeds() {
        this.analyticsEventService.sendAnalyticsEventsAsync(() -> ImmutableList.of((Object)this.analyticsEventBuilder.buildGlobalEntitiesExportStepCounterEvent(true)));
    }

    private StepResult failedStepResult(Exception ex, String migrationId, GlobalEntitiesExportStepConfig exportStepConfig, MigrationErrorCode errorCode) {
        log.error("{} with exception: {} for migrationId: {}", new Object[]{errorCode.getMessage(), ex, migrationId});
        if (errorCode.equals((Object)MigrationErrorCode.MISSING_WRITE_TO_DIRECTORY_PERMISSIONS)) {
            return StepResult.failed(ex.getMessage());
        }
        return StepResult.failed(String.format("Failed to export %s. Error: %s", exportStepConfig, ex.getMessage()), ex);
    }

    private static /* synthetic */ Collection lambda$checkIfGoodEventAndSaveAnalyticEventsWhenStepFails$4(List events) {
        return events;
    }

    private static /* synthetic */ Map lambda$doExport$2(Map additionalAttributes) {
        return additionalAttributes;
    }
}

