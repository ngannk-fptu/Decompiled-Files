/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.io.FileUtils
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.stepexecutor.space;

import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.migration.agent.Tracker;
import com.atlassian.migration.agent.entity.ConfluenceSpaceTask;
import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.entity.UploadDestinationType;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.ClusterLimits;
import com.atlassian.migration.agent.service.ExportDirManager;
import com.atlassian.migration.agent.service.MigrationErrorCode;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.analytics.ErrorEvent;
import com.atlassian.migration.agent.service.catalogue.MigrationCatalogueStorageService;
import com.atlassian.migration.agent.service.catalogue.model.MigrationCatalogueStorageFile;
import com.atlassian.migration.agent.service.execution.SpaceBoundStepExecutor;
import com.atlassian.migration.agent.service.execution.UncheckedInterruptedException;
import com.atlassian.migration.agent.service.impl.StepType;
import com.atlassian.migration.agent.service.stepexecutor.ProgressTracker;
import com.atlassian.migration.agent.service.stepexecutor.StepExecutionException;
import com.atlassian.migration.agent.service.stepexecutor.StepResult;
import com.atlassian.migration.agent.service.stepexecutor.space.SpaceUploadProgressTracker;
import com.atlassian.migration.agent.service.stepexecutor.space.helper.SpaceImportConfigFileManager;
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
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
public class SpaceUploadExecutor
implements SpaceBoundStepExecutor {
    @VisibleForTesting
    static final StepType UPLOAD_STEP_TYPE = StepType.DATA_UPLOAD;
    private static final Logger log = ContextLoggerFactory.getLogger(SpaceUploadExecutor.class);
    private static final int UPLOAD_WORK_THREAD_COUNT = 5;
    private final ProgressTracker progressTracker;
    private final ExportDirManager exportDirManager;
    private final StepStore stepStore;
    private final PluginTransactionTemplate ptx;
    private final Supplier<Instant> instantSupplier;
    private final AnalyticsEventService analyticsEventService;
    private final AnalyticsEventBuilder analyticsEventBuilder;
    private final MigrationCatalogueStorageService migrationCatalogueStorageService;
    private final SpaceManager spaceManager;
    private final SpaceImportConfigFileManager spaceImportConfigFileManager;
    private final ClusterLimits clusterLimits;

    public SpaceUploadExecutor(ProgressTracker progressTracker, ExportDirManager exportDirManager, StepStore stepStore, PluginTransactionTemplate ptx, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, MigrationCatalogueStorageService migrationCatalogueStorageService, SpaceManager spaceManager, SpaceImportConfigFileManager spaceImportConfigFileManager, ClusterLimits clusterLimits) {
        this(progressTracker, exportDirManager, stepStore, ptx, Instant::now, analyticsEventService, analyticsEventBuilder, migrationCatalogueStorageService, spaceManager, spaceImportConfigFileManager, clusterLimits);
    }

    @VisibleForTesting
    SpaceUploadExecutor(ProgressTracker progressTracker, ExportDirManager exportDirManager, StepStore stepStore, PluginTransactionTemplate ptx, Supplier<Instant> instantSupplier, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, MigrationCatalogueStorageService migrationCatalogueStorageService, SpaceManager spaceManager, SpaceImportConfigFileManager spaceImportConfigFileManager, ClusterLimits clusterLimits) {
        this.progressTracker = progressTracker;
        this.exportDirManager = exportDirManager;
        this.stepStore = stepStore;
        this.ptx = ptx;
        this.instantSupplier = instantSupplier;
        this.analyticsEventService = analyticsEventService;
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.migrationCatalogueStorageService = migrationCatalogueStorageService;
        this.spaceManager = spaceManager;
        this.spaceImportConfigFileManager = spaceImportConfigFileManager;
        this.clusterLimits = clusterLimits;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @VisibleForTesting
    StepResult doSpaceUpload(String stepId, String fileId, String cloudId, String migrationId, String spaceKey) {
        long startTime = this.instantSupplier.get().toEpochMilli();
        long freeHeapSizeAtStart = Runtime.getRuntime().freeMemory();
        long combinedUploadSize = this.exportDirManager.getExportSize(fileId);
        boolean success = false;
        Step step = this.ptx.read(() -> this.stepStore.getStep(stepId));
        UploadDestinationType uploadType = UploadDestinationType.MCS;
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildSpaceUploadStartEvent(step, startTime, uploadType, fileId, spaceKey));
        log.info("Migration {} Space {} upload started with uploadType {}", new Object[]{migrationId, spaceKey, uploadType});
        try {
            SpaceUploadProgressTracker spaceUploadProgressTracker = new SpaceUploadProgressTracker(uploadedBytes -> this.updateProgress(stepId, (Long)uploadedBytes, combinedUploadSize, spaceKey));
            String spaceId = String.valueOf(Objects.requireNonNull(this.spaceManager.getSpace(spaceKey)).getId());
            List<MigrationCatalogueStorageFile> uploadedFiles = this.uploadFilesToMCS(cloudId, migrationId, fileId, spaceId, spaceKey, step, spaceUploadProgressTracker::addUploadedBytes);
            this.spaceImportConfigFileManager.saveSpaceImportStepConfigToFile(migrationId, spaceId, uploadedFiles);
            String stepConfigFileName = this.spaceImportConfigFileManager.getSpaceImportStepConfigFileName(migrationId, spaceId);
            success = true;
            StepResult stepResult = StepResult.succeeded("Data upload successful", stepConfigFileName);
            return stepResult;
        }
        catch (UncheckedInterruptedException e) {
            log.info("Space Upload was stopped - spacekey [{}] | stepId [{}] | fileId [{}] | migrationId [{}]", new Object[]{spaceKey, stepId, fileId, migrationId});
            StepResult spaceId = StepResult.stopped();
            return spaceId;
        }
        catch (StepExecutionException e) {
            String failureReason = "Failed to upload file with ID " + fileId;
            success = this.checkIfGoodEventAndLoggingAnalyticEvents(migrationId, cloudId, e.getErrorCode(), e.getMessage(), spaceKey, failureReason, uploadType);
            StepResult stepResult = StepResult.failed(failureReason, e);
            return stepResult;
        }
        catch (Exception e) {
            String failureReason = "Failed to upload file with ID " + fileId;
            success = this.checkIfGoodEventAndLoggingAnalyticEvents(migrationId, cloudId, MigrationErrorCode.SPACE_UPLOAD_FAILED, e.getMessage(), spaceKey, failureReason, uploadType);
            StepResult stepResult = StepResult.failed(failureReason, e);
            return stepResult;
        }
        finally {
            ImmutableMap additionalAttributes = ImmutableMap.of((Object)"stepSuccessful", (Object)String.valueOf(success), (Object)"uploadType", (Object)uploadType.name());
            this.analyticsEventService.saveAnalyticsEvents(() -> this.lambda$doSpaceUpload$4(step, (Map)additionalAttributes, freeHeapSizeAtStart));
            this.saveStepCounterMetric(uploadType, success);
            this.saveStepTimerEvent(success, this.instantSupplier.get().toEpochMilli() - startTime, spaceKey, step, combinedUploadSize, uploadType);
        }
    }

    @VisibleForTesting
    synchronized void updateProgress(String stepId, Long uploadedBytes, long combinedUploadSize, String spaceKey) {
        int percentageComplete = combinedUploadSize == 0L ? 100 : (int)(uploadedBytes * 100L / combinedUploadSize);
        String uploadedBytesStr = FileUtils.byteCountToDisplaySize((long)uploadedBytes);
        String combinedUploadSizeStr = FileUtils.byteCountToDisplaySize((long)combinedUploadSize);
        log.info("Uploaded {} of {} - Space Upload progress for spaceKey {} is {} percentage", new Object[]{uploadedBytesStr, combinedUploadSizeStr, spaceKey, percentageComplete});
        this.progressTracker.progress(stepId, percentageComplete, String.format("Uploaded %s of %s", uploadedBytesStr, combinedUploadSizeStr), StepType.DATA_UPLOAD.getDetailedStatus());
    }

    private List<MigrationCatalogueStorageFile> uploadFilesToMCS(String cloudId, String migrationId, String fileId, String spaceId, String spaceKey, Step step, Tracker progressTracker) {
        Path exportFilePath = this.exportDirManager.getExportFilePath(fileId);
        ForkJoinPool customThreadPool = new ForkJoinPool(5);
        try {
            List list = (List)((ForkJoinTask)customThreadPool.submit(() -> new ArrayList(FileUtils.listFiles((File)exportFilePath.toFile(), null, (boolean)false)).parallelStream().map(file -> this.upload(cloudId, migrationId, (File)file, spaceKey, step, spaceId, progressTracker)).collect(Collectors.toList()))).get();
            return list;
        }
        catch (InterruptedException | ExecutionException e) {
            if (StopConditionCheckingUtil.isStoppingExceptionInCausalChain(e)) {
                log.warn("File upload was stopped - spaceKey [{}] | fileId [{}] | planId [{}]", new Object[]{spaceKey, fileId, step.getPlan().getId()});
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
    private MigrationCatalogueStorageFile upload(String cloudId, String migrationId, File file, String spaceKey, Step step, String spaceId, Tracker progressTracker) {
        long startTime = this.instantSupplier.get().toEpochMilli();
        boolean success = false;
        try {
            MigrationCatalogueStorageFile fileUpload = this.migrationCatalogueStorageService.uploadFileToMCS(cloudId, migrationId, file.toPath(), spaceId, progressTracker);
            log.info("File uploaded to MCS fileId: {}, name: {}, size: {}", new Object[]{fileUpload.getFileId(), fileUpload.getName(), fileUpload.getSize()});
            success = true;
            MigrationCatalogueStorageFile migrationCatalogueStorageFile = fileUpload;
            long timeTaken = this.instantSupplier.get().toEpochMilli() - startTime;
            this.saveMCSFileUploadTimerEvent(success, timeTaken, spaceKey, step, file.canRead() ? file.length() : -1L, file.getName());
            return migrationCatalogueStorageFile;
        }
        catch (Throwable throwable) {
            long timeTaken = this.instantSupplier.get().toEpochMilli() - startTime;
            this.saveMCSFileUploadTimerEvent(success, timeTaken, spaceKey, step, file.canRead() ? file.length() : -1L, file.getName());
            throw throwable;
        }
    }

    private void saveStepTimerEvent(boolean successful, long timeTaken, String spaceKey, Step step, long uploadSize, UploadDestinationType uploadType) {
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildSpaceUploadStepTimerEvent(successful, timeTaken, spaceKey, step, uploadSize, uploadType));
    }

    private void saveMCSFileUploadTimerEvent(boolean successful, long timeTaken, String spaceKey, Step step, long uploadSize, String filename) {
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildMCSFileUploadTimerEvent(successful, timeTaken, spaceKey, step, uploadSize, filename));
    }

    private void saveStepCounterMetric(UploadDestinationType uploadDestinationType, boolean success) {
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildUploadStepCounterEvent(uploadDestinationType, success));
    }

    private boolean checkIfGoodEventAndLoggingAnalyticEvents(String migrationId, String cloudId, MigrationErrorCode errorCode, String response, String spaceKey, String failureReason, UploadDestinationType uploadType) {
        log.error("Migration: {} Steptype: DATA_UPLOAD spaceKey: {} error: {}", new Object[]{migrationId, spaceKey, failureReason});
        return this.checkIfGoodEventAndSaveStepErrorAnalyticEvents(migrationId, cloudId, errorCode, response, spaceKey, uploadType);
    }

    private boolean checkIfGoodEventAndSaveStepErrorAnalyticEvents(String migrationId, String cloudId, MigrationErrorCode errorCode, String response, String spaceKey, UploadDestinationType uploadType) {
        boolean isSloGoodEvent = errorCode.shouldBeTreatedAsGoodEventInReliabilitySlo();
        ErrorEvent errorEvent = new ErrorEvent.ErrorEventBuilder(errorCode, errorCode.getContainerType(), migrationId, UPLOAD_STEP_TYPE).setCloudid(cloudId).setReason(response).setSpaceKey(spaceKey).build();
        this.analyticsEventService.sendAnalyticsEvents(() -> ImmutableList.of((Object)this.analyticsEventBuilder.buildFailedSpaceMigration(UPLOAD_STEP_TYPE, errorCode), (Object)this.analyticsEventBuilder.buildErrorOperationalEventForSpaceUpload(errorEvent, uploadType)));
        return isSloGoodEvent;
    }

    @Override
    public StepType getStepType() {
        return StepType.DATA_UPLOAD;
    }

    @Override
    public StepResult runStep(String stepId) {
        Step step = this.ptx.read(() -> this.stepStore.getStep(stepId));
        String spaceKey = ((ConfluenceSpaceTask)step.getTask()).getSpaceKey();
        return this.wrapStepResultSupplier(this.analyticsEventBuilder, this.analyticsEventService, step, spaceKey, this.spaceManager, () -> this.doSpaceUpload(step.getId(), step.getConfig(), step.getPlan().getCloudSite().getCloudId(), step.getPlan().getMigrationId(), spaceKey));
    }

    private /* synthetic */ Collection lambda$doSpaceUpload$4(Step step, Map additionalAttributes, long freeHeapSizeAtStart) {
        return ImmutableList.of((Object)this.analyticsEventBuilder.buildCompletedStepAnalyticsEvent(step, () -> additionalAttributes), (Object)this.analyticsEventBuilder.buildStepLevelHeapSizeAnalyticsEvent(step, freeHeapSizeAtStart, this.clusterLimits.getClusterConcurrencyLimit(this.getStepType()), this.clusterLimits.getConcurrencyPerNodeLimit(this.getStepType())));
    }
}

