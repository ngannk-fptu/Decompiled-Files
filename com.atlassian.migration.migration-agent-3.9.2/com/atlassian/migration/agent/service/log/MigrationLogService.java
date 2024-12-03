/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.cmpt.analytics.events.EventDto
 *  com.atlassian.confluence.event.events.cluster.ClusterEventWrapper
 *  com.atlassian.event.Event
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  lombok.Generated
 *  org.apache.commons.lang.StringUtils
 *  org.apache.commons.lang3.exception.ExceptionUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.log;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.event.Event;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.catalogue.MigrationCatalogueStorageService;
import com.atlassian.migration.agent.service.catalogue.model.MigrationCatalogueStorageFile;
import com.atlassian.migration.agent.service.event.UploadMigLogsToMCSEvent;
import com.atlassian.migration.agent.service.impl.StepType;
import com.atlassian.migration.agent.service.log.MigrationLogDirManager;
import com.atlassian.migration.agent.service.log.MigrationLogException;
import java.io.File;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.Generated;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class MigrationLogService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(MigrationLogService.class);
    private static final String SAVE_FORMATTED_LOG_ACTION = "saveFormattedLog";
    private static final String FORMAT_MIGRATION_LOG_MESSAGE_ACTION = "formatMigrationLogMessage";
    private static final String UPLOAD_LOG_ZIP_TO_MCS_ACTION = "uploadedLogZipToMcs";
    private static final String UPLOAD_TO_MCS_ACTION = "uploadedFileToMcs";
    private final MigrationLogDirManager migrationLogDirManager;
    private final MigrationCatalogueStorageService migrationCatalogueStorageService;
    private final Supplier<Instant> instantSupplier;
    private final AnalyticsEventService analyticsEventService;
    private final AnalyticsEventBuilder analyticsEventBuilder;
    private final EventPublisher eventPublisher;

    public MigrationLogService(MigrationLogDirManager migrationLogDirManager, MigrationCatalogueStorageService migrationCatalogueStorageService, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, EventPublisher eventPublisher) {
        this(migrationLogDirManager, migrationCatalogueStorageService, Instant::now, analyticsEventService, analyticsEventBuilder, eventPublisher);
    }

    @VisibleForTesting
    public MigrationLogService(MigrationLogDirManager migrationLogDirManager, MigrationCatalogueStorageService migrationCatalogueStorageService, Supplier<Instant> instantSupplier, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, EventPublisher eventPublisher) {
        this.migrationLogDirManager = migrationLogDirManager;
        this.migrationCatalogueStorageService = migrationCatalogueStorageService;
        this.instantSupplier = instantSupplier;
        this.analyticsEventService = analyticsEventService;
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void initialise() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void preDestroy() {
        this.eventPublisher.unregister((Object)this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void saveToLogFile(Step step, String reason, @Nullable Throwable exception) {
        if (!StringUtils.isEmpty((String)step.getPlan().getMigrationId())) {
            boolean success = false;
            Optional<String> errorReason = Optional.empty();
            long startTime = this.instantSupplier.get().toEpochMilli();
            try {
                String errorLogMessage = this.logMessageFormat(step, reason, exception);
                this.migrationLogDirManager.saveErrorLogsToFile(step.getPlan().getMigrationId(), errorLogMessage);
                success = true;
            }
            catch (Exception e) {
                log.error("Failed to save migration error logs to file for migrationId: " + step.getPlan().getMigrationId(), (Throwable)e);
                errorReason = Optional.ofNullable(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
            }
            finally {
                long timeTaken = this.instantSupplier.get().toEpochMilli() - startTime;
                EventDto timerEvent = this.analyticsEventBuilder.buildMigrationLogTimerEvent(success, timeTaken, SAVE_FORMATTED_LOG_ACTION, step.getPlan().getMigrationId(), errorReason);
                this.analyticsEventService.saveAnalyticsEventAsync(() -> timerEvent);
            }
        } else {
            log.info("MigrationId missing. Skipping saving log to the file...!!");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void uploadMigrationErrorLogZipToMCS(String cloudId, @Nullable String migrationId) {
        if (!StringUtils.isEmpty((String)migrationId) && this.errorLogFileExists(migrationId)) {
            boolean success = false;
            long startTime = this.instantSupplier.get().toEpochMilli();
            Optional<String> errorReason = Optional.empty();
            Optional<String> fileName = Optional.ofNullable(this.migrationLogDirManager.getMigrationErrorLogZipFile(migrationId).getFileName().toString());
            try {
                this.migrationLogDirManager.zipMigrationErrorLogFile(migrationId);
                this.uploadErrorLogFilesToMCS(cloudId, migrationId);
                success = true;
            }
            catch (Exception e) {
                log.error("Error while zipping log file or uploading zip to MCS for migrationId: " + migrationId, (Throwable)e);
                errorReason = Optional.ofNullable(e.getMessage());
            }
            finally {
                long timeTaken = this.instantSupplier.get().toEpochMilli() - startTime;
                EventDto timerEvent = this.analyticsEventBuilder.buildMigrationLogTimerEvent(success, timeTaken, UPLOAD_LOG_ZIP_TO_MCS_ACTION, migrationId, cloudId, errorReason, fileName);
                this.analyticsEventService.saveAnalyticsEventAsync(() -> timerEvent);
            }
        } else {
            log.info("Either MigrationId is missing or log file is not present. Skipping uploading of zip file...!!");
        }
    }

    private void uploadErrorLogFilesToMCS(String cloudId, String migrationId) {
        Path migrationErrorLogZipFile = this.migrationLogDirManager.getMigrationErrorLogZipFile(migrationId);
        this.uploadFilesToMCS(cloudId, migrationId, migrationErrorLogZipFile, () -> this.migrationLogDirManager.cleanupMigrationErrorLogZipFile(migrationId));
    }

    @EventListener
    public void handleClusteredEvent(ClusterEventWrapper clusterEventWrapper) {
        Event wrappedEvent = clusterEventWrapper.getEvent();
        if (wrappedEvent instanceof UploadMigLogsToMCSEvent) {
            this.handleUploadClusteredMigrationLogFiles((UploadMigLogsToMCSEvent)wrappedEvent);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @EventListener
    public void handleUploadClusteredMigrationLogFiles(UploadMigLogsToMCSEvent event) {
        log.info("Handling UploadMigLogsToMCSEvent for migrationId: {}", (Object)event.getMigrationId());
        String planId = event.getPlanId();
        String migrationId = event.getMigrationId();
        String cloudId = event.getCloudId();
        boolean success = false;
        long startTime = this.instantSupplier.get().toEpochMilli();
        Optional<Object> reason = Optional.empty();
        Optional<String> fileName = Optional.ofNullable(this.migrationLogDirManager.getMigrationLogZipFile(migrationId).getFileName().toString());
        try {
            Optional<Path> migrationLogZipFile = this.migrationLogDirManager.zipMigrationLogFiles(migrationId, planId);
            if (migrationLogZipFile.isPresent()) {
                this.uploadFilesToMCS(event.getCloudId(), migrationId, migrationLogZipFile.get(), () -> this.migrationLogDirManager.cleanupMigrationLogZipFile(migrationId));
                success = true;
            } else {
                reason = Optional.ofNullable("Migration zip files are not present. Skipping uploading of zip file...!!");
                log.info((String)reason.get());
            }
        }
        catch (Exception e) {
            log.error("Error while zipping log file or handling UploadMigLogsToMCSEvent {} ", (Object)event, (Object)e);
            reason = Optional.ofNullable(e.getMessage());
        }
        finally {
            long timeTaken = this.instantSupplier.get().toEpochMilli() - startTime;
            EventDto timerEvent = this.analyticsEventBuilder.buildMigrationLogTimerEvent(success, timeTaken, UPLOAD_LOG_ZIP_TO_MCS_ACTION, migrationId, cloudId, reason, fileName);
            this.analyticsEventService.saveAnalyticsEventAsync(() -> timerEvent);
        }
    }

    private void uploadFilesToMCS(String cloudId, String migrationId, Path migrationLogZipFile, Runnable cleanupMigration) {
        boolean success = false;
        long startTime = this.instantSupplier.get().toEpochMilli();
        Optional<Object> reason = Optional.empty();
        Optional<String> fileName = Optional.ofNullable(migrationLogZipFile.getFileName().toString());
        try {
            if (migrationLogZipFile.toFile().exists()) {
                MigrationCatalogueStorageFile uploadedLogFile = this.migrationCatalogueStorageService.uploadFileToMCS(cloudId, migrationId, migrationLogZipFile);
                log.info("File uploaded to MCS fileId: {}, name: {}, size: {}", new Object[]{uploadedLogFile.getFileId(), uploadedLogFile.getName(), uploadedLogFile.getSize()});
                success = true;
            } else {
                reason = Optional.ofNullable("Migration log zip files are not present. Skipping uploading of zip file...!!");
                log.info((String)reason.get());
            }
        }
        catch (Exception e) {
            reason = Optional.ofNullable(e.getMessage());
            throw new MigrationLogException(e.getMessage(), e);
        }
        finally {
            cleanupMigration.run();
            long timeTaken = this.instantSupplier.get().toEpochMilli() - startTime;
            EventDto timerEvent = this.analyticsEventBuilder.buildMigrationLogTimerEvent(success, timeTaken, UPLOAD_TO_MCS_ACTION, migrationId, cloudId, reason, fileName);
            this.analyticsEventService.saveAnalyticsEventAsync(() -> timerEvent);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String logMessageFormat(Step step, String reason, @Nullable Throwable exception) {
        boolean success = false;
        long startTime = this.instantSupplier.get().toEpochMilli();
        try {
            String errorMessage = String.format("%s ERROR Step failed, taskName: %s | stepType: %s | message: %s", this.instantSupplier.get().toString(), step.getTask().getName(), StepType.valueOf(step.getType()), reason);
            if (exception != null) {
                String exceptionDetails = String.format("[Exception Details]: %s | %s", exception.getMessage(), ExceptionUtils.getStackTrace((Throwable)exception));
                errorMessage = errorMessage + System.lineSeparator() + exceptionDetails;
            }
            errorMessage = errorMessage + System.lineSeparator();
            success = true;
            String string = errorMessage;
            return string;
        }
        finally {
            long timeTaken = this.instantSupplier.get().toEpochMilli() - startTime;
            EventDto timerEvent = this.analyticsEventBuilder.buildMigrationLogTimerEvent(success, timeTaken, FORMAT_MIGRATION_LOG_MESSAGE_ACTION, step.getPlan().getMigrationId());
            this.analyticsEventService.saveAnalyticsEventAsync(() -> timerEvent);
        }
    }

    private boolean errorLogFileExists(String migrationId) {
        File migrationLogFile = this.migrationLogDirManager.getMigrationErrorLogFile(migrationId).toFile();
        return migrationLogFile.exists();
    }

    public void uploadMigrationLogsZipFromClustersToMCS(String cloudId, String migrationId, String planId) {
        if (!StringUtils.isEmpty((String)migrationId)) {
            this.migrationLogDirManager.uploadClusteredMigrationLogFiles(cloudId, migrationId, planId);
        } else {
            log.info("MigrationId is missing. Skipping uploading of Migration log zip file...!!");
        }
    }
}

