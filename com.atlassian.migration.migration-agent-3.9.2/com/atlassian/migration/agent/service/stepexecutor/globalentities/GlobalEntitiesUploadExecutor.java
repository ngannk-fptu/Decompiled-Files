/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.io.FileUtils
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.stepexecutor.globalentities;

import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.entity.UploadDestinationType;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.ExportDirManager;
import com.atlassian.migration.agent.service.MigrationErrorCode;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.analytics.ErrorEvent;
import com.atlassian.migration.agent.service.catalogue.MigrationCatalogueStorageService;
import com.atlassian.migration.agent.service.catalogue.model.MigrationCatalogueStorageFile;
import com.atlassian.migration.agent.service.execution.StepExecutor;
import com.atlassian.migration.agent.service.execution.UncheckedInterruptedException;
import com.atlassian.migration.agent.service.impl.StepType;
import com.atlassian.migration.agent.service.stepexecutor.StepExecutionException;
import com.atlassian.migration.agent.service.stepexecutor.StepResult;
import com.atlassian.migration.agent.service.util.StopConditionCheckingUtil;
import com.atlassian.migration.agent.store.StepStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
public class GlobalEntitiesUploadExecutor
implements StepExecutor {
    @VisibleForTesting
    static final StepType UPLOAD_STEP_TYPE = StepType.GLOBAL_ENTITIES_DATA_UPLOAD;
    private static final Logger log = ContextLoggerFactory.getLogger(GlobalEntitiesUploadExecutor.class);
    private static final int UPLOAD_WORK_THREAD_COUNT = 3;
    private static final int CONCURRENCY_LEVEL = 1;
    private final ExportDirManager exportDirManager;
    private final StepStore stepStore;
    private final PluginTransactionTemplate ptx;
    private final Supplier<Instant> instantSupplier;
    private final AnalyticsEventService analyticsEventService;
    private final AnalyticsEventBuilder analyticsEventBuilder;
    private final MigrationCatalogueStorageService migrationCatalogueStorageService;

    public GlobalEntitiesUploadExecutor(ExportDirManager exportDirManager, StepStore stepStore, PluginTransactionTemplate ptx, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, MigrationCatalogueStorageService migrationCatalogueStorageService) {
        this(exportDirManager, stepStore, ptx, Instant::now, analyticsEventService, analyticsEventBuilder, migrationCatalogueStorageService);
    }

    @VisibleForTesting
    GlobalEntitiesUploadExecutor(ExportDirManager exportDirManager, StepStore stepStore, PluginTransactionTemplate ptx, Supplier<Instant> instantSupplier, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, MigrationCatalogueStorageService migrationCatalogueStorageService) {
        this.exportDirManager = exportDirManager;
        this.stepStore = stepStore;
        this.ptx = ptx;
        this.instantSupplier = instantSupplier;
        this.analyticsEventService = analyticsEventService;
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.migrationCatalogueStorageService = migrationCatalogueStorageService;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @VisibleForTesting
    StepResult doUpload(String stepId, String fileId, String cloudId, String migrationId, String planId, String taskId) {
        long startTime = this.instantSupplier.get().toEpochMilli();
        long freeHeapSizeAtStart = Runtime.getRuntime().freeMemory();
        long combinedUploadSize = -1L;
        boolean success = false;
        Step step = this.ptx.read(() -> this.stepStore.getStep(stepId));
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildGlobalEntitiesUploadStartEvent(step, startTime, fileId));
        log.info("Migration {} upload started with uploadType {}", (Object)migrationId, (Object)UploadDestinationType.MCS);
        try {
            List<MigrationCatalogueStorageFile> uploadedFiles = this.uploadFilesToMCS(cloudId, migrationId, fileId, planId, taskId);
            String jsonAsString = Jsons.valueAsString(uploadedFiles);
            combinedUploadSize = uploadedFiles.stream().mapToLong(MigrationCatalogueStorageFile::getSize).sum();
            success = true;
            StepResult stepResult = StepResult.succeeded("Data upload successful", jsonAsString);
            return stepResult;
        }
        catch (UncheckedInterruptedException e) {
            log.info("Global entities upload was stopped - stepId [{}] | fileId [{}] | migrationId [{}]", new Object[]{stepId, fileId, migrationId});
            StepResult jsonAsString = StepResult.stopped();
            return jsonAsString;
        }
        catch (StepExecutionException e) {
            String failureReason = "Failed to upload file with ID " + fileId;
            success = this.checkIfGoodEventAndLoggingAnalyticEvents(migrationId, cloudId, e.getErrorCode(), e.getMessage(), failureReason);
            StepResult stepResult = StepResult.failed(failureReason, e);
            return stepResult;
        }
        catch (Exception e) {
            String failureReason = "Failed to upload file with ID " + fileId;
            success = this.checkIfGoodEventAndLoggingAnalyticEvents(migrationId, cloudId, MigrationErrorCode.GLOBAL_ENTITIES_UPLOAD_FAILED, e.getMessage(), failureReason);
            StepResult stepResult = StepResult.failed(failureReason, e);
            return stepResult;
        }
        finally {
            ImmutableMap additionalAttributes = ImmutableMap.of((Object)"stepSuccessful", (Object)String.valueOf(success));
            this.analyticsEventService.saveAnalyticsEvents(() -> this.lambda$doUpload$3(step, (Map)additionalAttributes, freeHeapSizeAtStart));
            this.saveStepCounterMetric(success);
            this.saveStepTimerEvent(success, this.instantSupplier.get().toEpochMilli() - startTime, planId, cloudId, taskId, combinedUploadSize);
        }
    }

    private List<MigrationCatalogueStorageFile> uploadFilesToMCS(String cloudId, String migrationId, String fileId, String planId, String taskId) {
        Path exportFilePath = this.exportDirManager.getExportFilePath(fileId);
        ForkJoinPool customThreadPool = new ForkJoinPool(3);
        try {
            List list = (List)((ForkJoinTask)customThreadPool.submit(() -> new ArrayList(FileUtils.listFiles((File)exportFilePath.toFile(), null, (boolean)false)).parallelStream().map(file -> this.upload(cloudId, migrationId, (File)file, planId, taskId)).collect(Collectors.toList()))).get();
            return list;
        }
        catch (InterruptedException | ExecutionException e) {
            if (StopConditionCheckingUtil.isStoppingExceptionInCausalChain(e)) {
                log.warn("File upload was stopped - fileId [{}] | planId [{}]", (Object)fileId, (Object)planId);
                throw new UncheckedInterruptedException(e);
            }
            throw new StepExecutionException(MigrationErrorCode.MCS_API_ERROR, UPLOAD_STEP_TYPE, migrationId, e.getMessage(), e);
        }
        catch (Exception e) {
            log.error("Error in uploading files to MCS", (Throwable)e);
            throw new StepExecutionException(MigrationErrorCode.MCS_API_ERROR, UPLOAD_STEP_TYPE, migrationId, e.getMessage(), e);
        }
        finally {
            this.exportDirManager.cleanupExportFile(exportFilePath);
            customThreadPool.shutdownNow();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private MigrationCatalogueStorageFile upload(String cloudId, String migrationId, File file, String planId, String taskId) {
        long startTime = this.instantSupplier.get().toEpochMilli();
        boolean success = false;
        try {
            MigrationCatalogueStorageFile fileUpload = this.migrationCatalogueStorageService.uploadFileToMCS(cloudId, migrationId, file.toPath());
            log.info("File uploaded to MCS fileId: {}, name: {}, size: {}", new Object[]{fileUpload.getFileId(), fileUpload.getName(), fileUpload.getSize()});
            success = true;
            MigrationCatalogueStorageFile migrationCatalogueStorageFile = fileUpload;
            long timeTaken = this.instantSupplier.get().toEpochMilli() - startTime;
            this.saveMCSFileUploadTimerEvent(success, timeTaken, planId, cloudId, taskId, file.canRead() ? file.length() : -1L, file.getName());
            return migrationCatalogueStorageFile;
        }
        catch (Throwable throwable) {
            long timeTaken = this.instantSupplier.get().toEpochMilli() - startTime;
            this.saveMCSFileUploadTimerEvent(success, timeTaken, planId, cloudId, taskId, file.canRead() ? file.length() : -1L, file.getName());
            throw throwable;
        }
    }

    private void saveStepTimerEvent(boolean successful, long timeTaken, String planId, String cloudId, String taskId, long uploadSize) {
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildGlobalEntitiesUploadStepTimerEvent(successful, timeTaken, planId, cloudId, taskId, uploadSize));
    }

    private void saveMCSFileUploadTimerEvent(boolean successful, long timeTaken, String planId, String cloudId, String taskId, long uploadSize, String filename) {
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildGlobalEntitiesMCSFileUploadTimerEvent(successful, timeTaken, planId, cloudId, taskId, uploadSize, filename));
    }

    private void saveStepCounterMetric(boolean success) {
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildGlobalEntitiesUploadStepCounterEvent(success));
    }

    private boolean checkIfGoodEventAndLoggingAnalyticEvents(String migrationId, String cloudId, MigrationErrorCode errorCode, String response, String failureReason) {
        log.error("Migration: {} Steptype: {} error: {}", new Object[]{migrationId, UPLOAD_STEP_TYPE.name(), failureReason});
        return this.checkIfGoodEventAndSaveStepErrorAnalyticEvents(migrationId, cloudId, errorCode, response);
    }

    private boolean checkIfGoodEventAndSaveStepErrorAnalyticEvents(String migrationId, String cloudId, MigrationErrorCode errorCode, String response) {
        boolean isSloGoodEvent = errorCode.shouldBeTreatedAsGoodEventInReliabilitySlo();
        ErrorEvent errorEvent = new ErrorEvent.ErrorEventBuilder(errorCode, errorCode.getContainerType(), migrationId, UPLOAD_STEP_TYPE).setCloudid(cloudId).setReason(response).build();
        this.analyticsEventService.sendAnalyticsEvents(() -> ImmutableList.of((Object)this.analyticsEventBuilder.buildFailedGlobalEntitiesMigration(UPLOAD_STEP_TYPE, errorCode), (Object)this.analyticsEventBuilder.buildErrorOperationalEvent(errorEvent)));
        return isSloGoodEvent;
    }

    @Override
    public StepType getStepType() {
        return UPLOAD_STEP_TYPE;
    }

    @Override
    public StepResult runStep(String stepId) {
        Step step = this.ptx.read(() -> this.stepStore.getStep(stepId));
        return this.doUpload(step.getId(), step.getConfig(), step.getPlan().getCloudSite().getCloudId(), step.getPlan().getMigrationId(), step.getPlan().getId(), step.getTask().getId());
    }

    private /* synthetic */ Collection lambda$doUpload$3(Step step, Map additionalAttributes, long freeHeapSizeAtStart) {
        return ImmutableList.of((Object)this.analyticsEventBuilder.buildCompletedStepAnalyticsEvent(step, () -> additionalAttributes), (Object)this.analyticsEventBuilder.buildStepLevelHeapSizeAnalyticsEvent(step, freeHeapSizeAtStart, 1, 1));
    }
}

