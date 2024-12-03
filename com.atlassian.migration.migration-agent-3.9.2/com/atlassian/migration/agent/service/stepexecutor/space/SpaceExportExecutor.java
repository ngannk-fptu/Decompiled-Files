/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.cmpt.analytics.events.EventDto
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.stepexecutor.space;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.dto.ConfExportStepConfig;
import com.atlassian.migration.agent.entity.ExportCacheEntry;
import com.atlassian.migration.agent.entity.ExportType;
import com.atlassian.migration.agent.entity.MigrateUsersTask;
import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.export.MigrationExportException;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.newexport.SpaceCSVExportTaskContext;
import com.atlassian.migration.agent.newexport.SpaceRapidExporter;
import com.atlassian.migration.agent.newexport.util.FileUtil;
import com.atlassian.migration.agent.service.ClusterLimits;
import com.atlassian.migration.agent.service.ExportDirManager;
import com.atlassian.migration.agent.service.MigrationErrorCode;
import com.atlassian.migration.agent.service.ServiceInitializeException;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.analytics.ErrorEvent;
import com.atlassian.migration.agent.service.execution.SpaceBoundStepExecutor;
import com.atlassian.migration.agent.service.execution.UncheckedInterruptedException;
import com.atlassian.migration.agent.service.impl.StepType;
import com.atlassian.migration.agent.service.stepexecutor.ExportExecutor;
import com.atlassian.migration.agent.service.stepexecutor.StepResult;
import com.atlassian.migration.agent.service.stepexecutor.export.SpaceExportCacheService;
import com.atlassian.migration.agent.service.stepexecutor.space.SpaceExportResult;
import com.atlassian.migration.agent.store.StepStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
public class SpaceExportExecutor
extends ExportExecutor
implements SpaceBoundStepExecutor {
    @VisibleForTesting
    static final StepType EXPORT_STEP_TYPE = StepType.CONFLUENCE_EXPORT;
    private static final Logger log = ContextLoggerFactory.getLogger(SpaceExportExecutor.class);
    private final SpaceExportCacheService cacheService;
    private final SpaceManager spaceManager;
    private final SpaceRapidExporter rapidExporter;
    private final Supplier<String> fileIdGenerator;
    private final ClusterLimits clusterLimits;

    public SpaceExportExecutor(ExportDirManager exportDirManager, BootstrapManager bootstrapManager, StepStore stepStore, PluginTransactionTemplate ptx, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, MigrationDarkFeaturesManager migrationDarkFeaturesManager, SpaceRapidExporter rapidExporter, SpaceExportCacheService cacheService, SpaceManager spaceManager, MigrationAgentConfiguration migrationAgentConfiguration, ClusterLimits clusterLimits) {
        super(exportDirManager, bootstrapManager, stepStore, ptx, analyticsEventService, analyticsEventBuilder, migrationDarkFeaturesManager, migrationAgentConfiguration);
        this.cacheService = cacheService;
        this.spaceManager = spaceManager;
        this.rapidExporter = rapidExporter;
        this.fileIdGenerator = () -> UUID.randomUUID().toString();
        this.clusterLimits = clusterLimits;
    }

    @VisibleForTesting
    SpaceExportExecutor(ExportDirManager exportDirManager, BootstrapManager bootstrapManager, StepStore stepStore, PluginTransactionTemplate ptx, Supplier<Instant> instantSupplier, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, MigrationDarkFeaturesManager migrationDarkFeaturesManager, SpaceRapidExporter rapidExporter, SpaceExportCacheService cacheService, SpaceManager spaceManager, MigrationAgentConfiguration migrationAgentConfiguration, Clock clock, Supplier<String> fileIdGenerator, ClusterLimits clusterLimits) {
        super(exportDirManager, bootstrapManager, stepStore, ptx, instantSupplier, analyticsEventService, analyticsEventBuilder, migrationDarkFeaturesManager, migrationAgentConfiguration, clock);
        this.cacheService = cacheService;
        this.spaceManager = spaceManager;
        this.rapidExporter = rapidExporter;
        this.fileIdGenerator = fileIdGenerator;
        this.clusterLimits = clusterLimits;
    }

    @VisibleForTesting
    StepResult doExport(ConfExportStepConfig exportStepConfig, String stepId) {
        long startTime = ((Instant)this.instantSupplier.get()).toEpochMilli();
        long freeHeapSizeAtStart = Runtime.getRuntime().freeMemory();
        Step step = this.stepStore.getStep(stepId);
        String planId = step.getPlan().getId();
        String migrationId = step.getPlan().getMigrationId();
        String cloudId = exportStepConfig.getCloudId();
        String spaceKey = exportStepConfig.getSpaceKey();
        String fileId = this.fileIdGenerator.get();
        ExportType exportType = ExportType.RAPID;
        log.info("Initiating space export for spaceKey: {} planId: {} migrationId: {} exportType:{} fileId: {}", new Object[]{spaceKey, planId, migrationId, exportType, fileId});
        if (StringUtils.isBlank((String)spaceKey)) {
            this.checkIfGoodEventAndSaveAnalyticEventsWhenStepFails(MigrationErrorCode.SPACE_EXPORT_NO_SPACE_KEY, exportType, migrationId, cloudId, MigrationErrorCode.SPACE_EXPORT_NO_SPACE_KEY.getMessage(), spaceKey);
            return StepResult.failed("Attempted to perform an export, but no space key was found.");
        }
        if (StringUtils.isBlank((String)cloudId)) {
            this.checkIfGoodEventAndSaveAnalyticEventsWhenStepFails(MigrationErrorCode.SPACE_EXPORT_NO_CLOUD_ID, exportType, migrationId, cloudId, MigrationErrorCode.SPACE_EXPORT_NO_CLOUD_ID.getMessage(), spaceKey);
            return StepResult.failed("Attempted to perform an export, but no cloud ID was found.");
        }
        Space space = this.spaceManager.getSpace(spaceKey);
        if (space == null) {
            this.checkIfGoodEventAndSaveAnalyticEventsWhenStepFails(MigrationErrorCode.SPACE_EXPORT_SPACE_DOES_NOT_EXIST, exportType, migrationId, cloudId, MigrationErrorCode.SPACE_EXPORT_SPACE_DOES_NOT_EXIST.getMessage(), spaceKey);
            return StepResult.failed(String.format("Attempted to perform an export, but no space with the key '%s' was found.", spaceKey));
        }
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildSpaceExportStartEvent(step, exportType, space.getId(), startTime));
        return this.exportProcessor(step, exportStepConfig, exportType, startTime, freeHeapSizeAtStart, fileId);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private StepResult exportProcessor(Step step, ConfExportStepConfig exportStepConfig, ExportType exportType, long startTime, long freeHeapSizeAtStart, String fileId) {
        String migrationId = step.getPlan().getMigrationId();
        String spaceKey = exportStepConfig.getSpaceKey();
        String cloudId = exportStepConfig.getCloudId();
        String planId = step.getPlan().getId();
        String taskId = step.getTask().getId();
        boolean containsUserMigrationTask = this.containsTask(step.getPlan(), MigrateUsersTask.class);
        boolean success = false;
        SpaceExportResult exportResult = null;
        try {
            exportResult = this.exportedFileGenerator(spaceKey, exportType, planId, taskId, fileId, containsUserMigrationTask, cloudId);
            String exportedFile = exportResult.getExportFile();
            if (!this.migrationDarkFeaturesManager.isExportOnlyEnabled()) {
                log.info("Moving csv files in {} to {} inside migration exports directory for migrationId: {}", new Object[]{exportedFile, fileId, migrationId});
                this.exportDirManager.moveCompressedFilesToSharedHome(exportedFile, fileId);
            }
            this.buildAndSaveMetricsWhenStepSucceeds(exportType);
            success = true;
            log.info("Space export successful for spaceKey: {} planId: {} migrationId: {}", new Object[]{spaceKey, planId, migrationId});
            StepResult stepResult = StepResult.succeeded("Data export successful", fileId);
            return stepResult;
        }
        catch (UncheckedInterruptedException ex) {
            StepResult stepResult = StepResult.stopped();
            return stepResult;
        }
        catch (MigrationExportException ex) {
            success = this.checkIfGoodEventAndSaveAnalyticEventsWhenStepFails(MigrationErrorCode.SPACE_EXPORT_FILE_EXPORT_FAILED, exportType, migrationId, cloudId, ex.getMessage(), spaceKey);
            StepResult stepResult = this.failedStepResult(ex, migrationId, exportStepConfig, MigrationErrorCode.SPACE_EXPORT_FILE_EXPORT_FAILED);
            return stepResult;
        }
        catch (AccessDeniedException ex) {
            success = this.checkIfGoodEventAndSaveAnalyticEventsWhenStepFails(MigrationErrorCode.MISSING_WRITE_TO_DIRECTORY_PERMISSIONS, exportType, migrationId, cloudId, ex.getMessage(), spaceKey);
            StepResult stepResult = this.failedStepResult(ex, migrationId, exportStepConfig, MigrationErrorCode.MISSING_WRITE_TO_DIRECTORY_PERMISSIONS);
            return stepResult;
        }
        catch (ServiceInitializeException ex) {
            success = this.checkIfGoodEventAndSaveAnalyticEventsWhenStepFails(MigrationErrorCode.SPACE_EXPORT_DIRECTORY_CREATION_FAILED, exportType, migrationId, cloudId, ex.getMessage(), spaceKey);
            StepResult stepResult = this.failedStepResult(ex, migrationId, exportStepConfig, MigrationErrorCode.SPACE_EXPORT_DIRECTORY_CREATION_FAILED);
            return stepResult;
        }
        catch (Exception ex) {
            success = this.checkIfGoodEventAndSaveAnalyticEventsWhenStepFails(MigrationErrorCode.SPACE_EXPORT_EXECUTION_ERROR, exportType, migrationId, cloudId, ex.getMessage(), spaceKey);
            StepResult stepResult = this.failedStepResult(ex, migrationId, exportStepConfig, MigrationErrorCode.SPACE_EXPORT_EXECUTION_ERROR);
            return stepResult;
        }
        finally {
            ImmutableMap additionalAttributes = ImmutableMap.of((Object)"stepSuccessful", (Object)String.valueOf(success), (Object)"exportType", (Object)exportType.name());
            EventDto completedStepEvent = this.analyticsEventBuilder.buildCompletedStepAnalyticsEvent(step, () -> SpaceExportExecutor.lambda$exportProcessor$2((Map)additionalAttributes));
            EventDto heapSizeAnalyticsEvent = this.analyticsEventBuilder.buildStepLevelHeapSizeAnalyticsEvent(step, freeHeapSizeAtStart, this.clusterLimits.getClusterConcurrencyLimit(StepType.CONFLUENCE_EXPORT), this.clusterLimits.getConcurrencyPerNodeLimit(StepType.CONFLUENCE_EXPORT));
            EventDto timerEvent = this.analyticsEventBuilder.buildSpaceExportStepTimerEvent(success, ((Instant)this.instantSupplier.get()).toEpochMilli() - startTime, spaceKey, step, exportType, this.createAdditionalAttributes(Optional.ofNullable(exportResult)));
            this.analyticsEventService.saveAnalyticsEvents(() -> ImmutableList.of((Object)completedStepEvent, (Object)heapSizeAnalyticsEvent, (Object)timerEvent));
        }
    }

    private Map<String, Object> createAdditionalAttributes(Optional<SpaceExportResult> maybeExportResult) {
        HashMap<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("dbType", this.migrationAgentConfiguration.getDBType().name());
        maybeExportResult.ifPresent(exportResult -> attributes.putAll(exportResult.getAttributes()));
        return attributes;
    }

    private SpaceExportResult exportedFileGenerator(String spaceKey, ExportType exportType, String planId, String taskId, String fileId, boolean containsUserMigrationTask, String cloudId) throws AccessDeniedException {
        Space space = this.spaceManager.getSpace(spaceKey);
        if (this.migrationDarkFeaturesManager.exportCachingEnabled()) {
            return this.exportSpaceContentViaCache(space.getId(), spaceKey, planId, taskId, fileId, exportType, containsUserMigrationTask, cloudId);
        }
        return this.exportSpaceContentToFile(space.getId(), spaceKey, planId, taskId, containsUserMigrationTask, cloudId);
    }

    private SpaceExportResult exportSpaceContentViaCache(long spaceId, String spaceKey, String planId, String taskId, String fileId, ExportType exportType, boolean containsUserMigrationTask, String cloudId) throws AccessDeniedException {
        Optional<ExportCacheEntry> maybeCachedValue = this.cacheService.getCacheEntry(spaceKey, exportType, containsUserMigrationTask, cloudId);
        if (maybeCachedValue.isPresent()) {
            ExportCacheEntry cachedValue = maybeCachedValue.get();
            log.info("Using cached export id: {} file: {}", (Object)cachedValue.getId(), (Object)cachedValue.getFilePath());
            return new SpaceExportResult(this.copyCachedFileToTmpDir(spaceKey, fileId, cachedValue.getFilePath()).toString(), Collections.emptyMap());
        }
        return this.generateAndCacheExportFile(spaceId, spaceKey, planId, taskId, fileId, exportType, containsUserMigrationTask, cloudId);
    }

    private SpaceExportResult exportSpaceContentToFile(long spaceId, String spaceKey, String planId, String taskId, boolean containsUserMigrationTask, String cloudId) throws AccessDeniedException {
        SpaceCSVExportTaskContext config = new SpaceCSVExportTaskContext(spaceId, spaceKey, cloudId, planId, taskId, this.tempDirFilePath.getAbsolutePath(), containsUserMigrationTask);
        String exportedFile = this.rapidExporter.export(config);
        this.createExportDirectoryIfNotExists();
        return new SpaceExportResult(exportedFile, (Map<String, Object>)ImmutableMap.of((Object)"totalRowCount", (Object)config.getTotalRowCount(), (Object)"totalCharactersExported", (Object)config.getTotalCharactersExported()));
    }

    private SpaceExportResult generateAndCacheExportFile(long spaceId, String spaceKey, String planId, String taskId, String fileId, ExportType exportType, boolean containsUserMigrationTask, String cloudId) throws AccessDeniedException {
        long now = Instant.now(this.clock).toEpochMilli();
        log.info("Generating export file for fileId={} spaceKey={}, exportType={}, containsUserMigrationTask={}, snapshotTime={}", new Object[]{fileId, spaceKey, exportType, containsUserMigrationTask, now});
        SpaceExportResult result = this.exportSpaceContentToFile(spaceId, spaceKey, planId, taskId, containsUserMigrationTask, cloudId);
        String localFilePath = result.getExportFile();
        String cacheFilePath = this.exportDirManager.copyExportedFileToSharedHome(localFilePath, fileId + "_cache");
        this.cacheService.cacheExportData(now, exportType, spaceKey, cloudId, containsUserMigrationTask, cacheFilePath);
        return result;
    }

    private Path copyCachedFileToTmpDir(String spaceKey, String fileId, String cachedFile) {
        try {
            String exportDir = FileUtil.createExportDirectory(spaceKey, this.tempDirFilePath.getAbsolutePath());
            Path destinationPath = Paths.get(exportDir, new String[0]).resolve(fileId);
            Files.copy(Paths.get(cachedFile, new String[0]), destinationPath, new CopyOption[0]);
            return destinationPath;
        }
        catch (IOException e) {
            throw new MigrationExportException("Unable to copy cached file to tmp dir", e);
        }
    }

    private boolean checkIfGoodEventAndSaveAnalyticEventsWhenStepFails(MigrationErrorCode migrationErrorCode, ExportType exportType, String migrationId, String cloudId, String message, String spaceKey) {
        boolean isSloGoodEvent = migrationErrorCode.shouldBeTreatedAsGoodEventInReliabilitySlo();
        ErrorEvent errorEvent = new ErrorEvent.ErrorEventBuilder(migrationErrorCode, migrationErrorCode.getContainerType(), migrationId, EXPORT_STEP_TYPE).setCloudid(cloudId).setReason(message).setSpaceKey(spaceKey).build();
        ImmutableList events = ImmutableList.of((Object)this.analyticsEventBuilder.buildFailedSpaceMigration(EXPORT_STEP_TYPE, migrationErrorCode), (Object)this.analyticsEventBuilder.buildExportStepCounterEvent(exportType, isSloGoodEvent), (Object)this.analyticsEventBuilder.buildErrorOperationalEventForSpaceExport(errorEvent, exportType));
        this.analyticsEventService.sendAnalyticsEvents(() -> SpaceExportExecutor.lambda$checkIfGoodEventAndSaveAnalyticEventsWhenStepFails$5((List)events));
        return isSloGoodEvent;
    }

    private void buildAndSaveMetricsWhenStepSucceeds(ExportType exportType) {
        this.analyticsEventService.sendAnalyticsEvents(() -> ImmutableList.of((Object)this.analyticsEventBuilder.buildExportStepCounterEvent(exportType, true)));
    }

    @Override
    public StepType getStepType() {
        return StepType.CONFLUENCE_EXPORT;
    }

    @Override
    public StepResult runStep(String stepId) {
        Step step = this.ptx.read(() -> this.stepStore.getStep(stepId));
        ConfExportStepConfig exportStepConfig = Jsons.readValue(step.getConfig(), ConfExportStepConfig.class);
        String spaceKey = exportStepConfig.getSpaceKey();
        return this.wrapStepResultSupplier(this.analyticsEventBuilder, this.analyticsEventService, step, spaceKey, this.spaceManager, () -> this.doExport(exportStepConfig, stepId));
    }

    private static /* synthetic */ Collection lambda$checkIfGoodEventAndSaveAnalyticEventsWhenStepFails$5(List events) {
        return events;
    }

    private static /* synthetic */ Map lambda$exportProcessor$2(Map additionalAttributes) {
        return additionalAttributes;
    }
}

