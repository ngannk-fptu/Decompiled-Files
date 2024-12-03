/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.business.insights.api.dataset.DataChannel
 *  com.atlassian.business.insights.api.dataset.Dataset
 *  com.atlassian.business.insights.api.dataset.DatasetProvider
 *  com.atlassian.business.insights.api.extract.EntityStreamerQuery
 *  com.atlassian.business.insights.api.extract.LogRecordStreamer
 *  com.atlassian.business.insights.api.extract.StreamerValidationResult$Action
 *  com.atlassian.business.insights.api.filter.OptOutEntity
 *  com.atlassian.business.insights.api.user.RequestContext
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  javax.annotation.Nonnull
 *  one.util.streamex.StreamEx
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.business.insights.core.service;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.audit.api.AuditService;
import com.atlassian.business.insights.api.dataset.DataChannel;
import com.atlassian.business.insights.api.dataset.Dataset;
import com.atlassian.business.insights.api.dataset.DatasetProvider;
import com.atlassian.business.insights.api.extract.EntityStreamerQuery;
import com.atlassian.business.insights.api.extract.LogRecordStreamer;
import com.atlassian.business.insights.api.extract.StreamerValidationResult;
import com.atlassian.business.insights.api.filter.OptOutEntity;
import com.atlassian.business.insights.api.user.RequestContext;
import com.atlassian.business.insights.core.analytics.export.FullExportCancelledAnalyticEvent;
import com.atlassian.business.insights.core.analytics.export.FullExportFailedAnalyticEvent;
import com.atlassian.business.insights.core.analytics.export.FullExportFinishedAnalyticEvent;
import com.atlassian.business.insights.core.analytics.export.FullExportStartedAnalyticEvent;
import com.atlassian.business.insights.core.ao.dao.entity.ExportProgressStatus;
import com.atlassian.business.insights.core.audit.AuditEventFactory;
import com.atlassian.business.insights.core.mapper.FileRecord;
import com.atlassian.business.insights.core.mapper.api.LogRecordMapper;
import com.atlassian.business.insights.core.service.DataExportJobExecutor;
import com.atlassian.business.insights.core.service.DiskSpaceValidator;
import com.atlassian.business.insights.core.service.ExportStatusContext;
import com.atlassian.business.insights.core.service.api.ConfigService;
import com.atlassian.business.insights.core.service.api.DataExportOrchestrator;
import com.atlassian.business.insights.core.service.api.EntityOptOutService;
import com.atlassian.business.insights.core.service.api.EventPublisherService;
import com.atlassian.business.insights.core.service.api.ExportJobState;
import com.atlassian.business.insights.core.service.api.ExportJobStateService;
import com.atlassian.business.insights.core.service.api.OptedOutEntity;
import com.atlassian.business.insights.core.util.DateDifferenceUtil;
import com.atlassian.business.insights.core.writer.api.DatasetWriter;
import com.atlassian.business.insights.core.writer.api.DatasetWriterFactory;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import one.util.streamex.StreamEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public class DefaultDataExportOrchestrator
implements DataExportOrchestrator,
LifecycleAware,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(DefaultDataExportOrchestrator.class);
    private final AtomicReference<ExportJobState> exportJobStateReference;
    private final AuditService auditService;
    private final ExportJobStateService exportJobStateService;
    private final DatasetWriterFactory writerFactory;
    private final LogRecordMapper logRecordMapper;
    private final DataExportJobExecutor exportJobExecutor;
    private final EventPublisherService eventPublisherService;
    private final RequestContext requestContext;
    private final ConfigService configService;
    private final DiskSpaceValidator diskSpaceValidator;
    private final String platformId;
    private final DatasetProvider datasetProvider;
    private final EntityOptOutService optOutService;

    public DefaultDataExportOrchestrator(AuditService auditService, ExportJobStateService exportJobStateService, DataExportJobExecutor exportJobExecutor, DatasetWriterFactory writerFactory, LogRecordMapper logRecordMapper, EventPublisherService eventPublisherService, RequestContext requestContext, ConfigService configService, ApplicationProperties applicationProperties, DiskSpaceValidator diskSpaceValidator, DatasetProvider datasetProvider, EntityOptOutService optOutService) {
        this(auditService, exportJobStateService, exportJobExecutor, writerFactory, logRecordMapper, eventPublisherService, requestContext, new AtomicReference<Object>(null), configService, applicationProperties, diskSpaceValidator, datasetProvider, optOutService);
    }

    @VisibleForTesting
    DefaultDataExportOrchestrator(AuditService auditService, ExportJobStateService exportJobStateService, DataExportJobExecutor exportJobExecutor, DatasetWriterFactory writerFactory, LogRecordMapper logRecordMapper, EventPublisherService eventPublisherService, RequestContext requestContext, AtomicReference<ExportJobState> exportJobStateReference, ConfigService configService, ApplicationProperties applicationProperties, DiskSpaceValidator diskSpaceValidator, DatasetProvider datasetProvider, EntityOptOutService optOutService) {
        this.auditService = auditService;
        this.exportJobStateService = exportJobStateService;
        this.writerFactory = writerFactory;
        this.logRecordMapper = logRecordMapper;
        this.exportJobExecutor = exportJobExecutor;
        this.eventPublisherService = eventPublisherService;
        this.requestContext = requestContext;
        this.configService = configService;
        this.exportJobStateReference = exportJobStateReference;
        this.diskSpaceValidator = diskSpaceValidator;
        this.datasetProvider = datasetProvider;
        this.platformId = applicationProperties.getPlatformId();
        this.optOutService = optOutService;
    }

    @Override
    @Nonnull
    public synchronized ExportJobState runFullExport(@Nonnull Dataset dataset, @Nonnull Instant exportFrom, boolean forcedExport) {
        Objects.requireNonNull(dataset);
        Objects.requireNonNull(exportFrom);
        Optional<ExportJobState> latestRunningJobState = this.exportJobStateService.getLatestRunningExportJobState();
        if (latestRunningJobState.isPresent()) {
            return latestRunningJobState.get();
        }
        Path rootExportPath = this.configService.getRootExportPathHolder().getRootExportPath();
        ExportJobState exportJobState = this.createExportJobState(dataset.getVersion(), exportFrom, forcedExport, rootExportPath);
        this.publishFullExportStartedAnalyticEvent(exportFrom, exportJobState);
        if (exportJobState.getStatus() == ExportProgressStatus.FAILED) {
            ExportStatusContext exportStatusContext = new ExportStatusContext(ExportProgressStatus.FAILED, forcedExport);
            this.publishFullExportExecutionResult(exportJobState, exportStatusContext);
            return exportJobState;
        }
        this.exportJobStateReference.set(exportJobState);
        this.requestContext.dumpThreadContextInfo();
        try {
            this.diskSpaceValidator.verifyEnoughUsableSpaceRemaining(rootExportPath);
            this.exportJobExecutor.execute(() -> this.requestContext.runInCustomContext(() -> {
                List effectiveOptOutEntities = this.optOutService.getOptOutEntities().stream().filter(optOutResource -> optOutResource.getKey() != null).collect(Collectors.toList());
                ExportStatusContext exportStatusContext = new ExportStatusContext(exportJobState.getStatus(), forcedExport);
                exportStatusContext.setOptedOutEntities(effectiveOptOutEntities.stream().map(OptedOutEntity::fromOptOutResource).collect(Collectors.toList()));
                try {
                    for (DataChannel dataChannel : dataset.getChannels()) {
                        if (this.exportJobStateService.shouldJobBeCancelled(exportJobState)) break;
                        DatasetWriter writer = this.writerFactory.create(dataChannel.getSchema(), exportJobState.getId(), dataset.getVersion(), exportJobState.getCreatedTime(), rootExportPath);
                        Throwable throwable = null;
                        try {
                            writer.writeHeaders();
                            log.debug("Starting to export from {}", (Object)dataChannel.getLogRecordStreamer().getClass().getSimpleName());
                            try (Stream stream = dataChannel.getLogRecordStreamer().stream(EntityStreamerQuery.builder().from(exportFrom).optOutEntityIdentifiers(effectiveOptOutEntities.stream().map(OptOutEntity::getIdentifier).collect(Collectors.toList())).build());){
                                ((StreamEx)StreamEx.of(stream.peek(entity -> exportStatusContext.incrementExportedEntitiesCounter()).map(logRecord -> this.logRecordMapper.map(dataChannel.getSchema(), logRecord))).takeWhile(fileRecord -> !this.exportJobStateService.shouldJobBeCancelled(exportJobState))).forEach(fileRecord -> {
                                    this.diskSpaceValidator.verifyEnoughUsableSpaceRemaining(rootExportPath);
                                    int writtenRows = writer.write((Stream<FileRecord>)fileRecord);
                                    exportStatusContext.incrementRowCounter(writtenRows);
                                });
                            }
                            log.debug("Finished exporting from {}", (Object)dataChannel.getLogRecordStreamer().getClass().getSimpleName());
                        }
                        catch (Throwable throwable2) {
                            throwable = throwable2;
                            throw throwable2;
                        }
                        finally {
                            if (writer == null) continue;
                            if (throwable != null) {
                                try {
                                    writer.close();
                                }
                                catch (Throwable throwable3) {
                                    throwable.addSuppressed(throwable3);
                                }
                                continue;
                            }
                            writer.close();
                        }
                    }
                    exportStatusContext.setExportProgressStatus(this.exportJobStateService.shouldJobBeCancelled(exportJobState) ? ExportProgressStatus.CANCELLED : ExportProgressStatus.COMPLETED);
                }
                catch (Throwable throwable) {
                    Exception e = throwable instanceof Exception ? (Exception)throwable : new RuntimeException(throwable);
                    exportStatusContext.setExportProgressStatus(ExportProgressStatus.FAILED, e);
                    log.error("Failed writing entities to file - processId: {}", (Object)exportJobState.getId(), (Object)e);
                }
                finally {
                    this.exportJobStateReference.set(null);
                }
                ExportJobState executedExportJobState = this.exportJobStateService.updateExportJobState(exportJobState, exportStatusContext);
                this.publishFullExportExecutionResult(executedExportJobState, exportStatusContext);
            }));
            this.auditService.audit(AuditEventFactory.createFullExportTriggeredAuditEvent(exportJobState.getId()));
        }
        catch (Exception e) {
            log.error("Failed to start the export.", (Throwable)e);
            this.exportJobStateReference.set(null);
            ExportStatusContext exportStatusContext = new ExportStatusContext(ExportProgressStatus.FAILED, forcedExport);
            exportStatusContext.setExportProgressStatus(ExportProgressStatus.FAILED, e);
            ExportJobState failedExportJobState = this.exportJobStateService.updateExportJobState(exportJobState, exportStatusContext);
            this.publishFullExportExecutionResult(failedExportJobState, exportStatusContext);
            return failedExportJobState;
        }
        return exportJobState;
    }

    @Override
    @Nonnull
    public synchronized ExportJobState runScheduledExport(int schemaVersion, @Nonnull Instant exportFrom, boolean forcedExport) {
        Objects.requireNonNull(exportFrom);
        Optional dataset = this.datasetProvider.getDataset(schemaVersion);
        Optional<ExportJobState> latestRunningJobState = this.exportJobStateService.getLatestRunningExportJobState();
        if (dataset.isPresent() && !latestRunningJobState.isPresent()) {
            return this.runFullExport((Dataset)dataset.get(), exportFrom, forcedExport);
        }
        Path rootExportPath = this.configService.getRootExportPathHolder().getRootExportPath();
        ExportJobState exportJobState = this.createExportJobState(schemaVersion, exportFrom, forcedExport, rootExportPath);
        ExportStatusContext exportStatusContext = new ExportStatusContext(ExportProgressStatus.FAILED, forcedExport);
        return this.exportJobStateService.updateExportJobState(exportJobState, exportStatusContext);
    }

    public synchronized void onStop() {
        if (this.exportJobStateReference.get() != null && this.exportJobStateReference.get().getStatus() == ExportProgressStatus.STARTED) {
            log.info("DefaultDataExportOrchestrator is about to be destroyed. Cancelling in flight export.");
            this.exportJobStateService.requestCancellation(this.exportJobStateReference.get());
            this.exportJobStateReference.set(null);
        }
    }

    public synchronized void destroy() throws Exception {
        if ("conf".equals(this.platformId)) {
            log.info("DefaultDataExportOrchestrator is about to be destroyed, invoking onStop() for Confluence.");
            this.onStop();
        }
    }

    public void onStart() {
    }

    private void publishFullExportExecutionResult(ExportJobState exportJobState, ExportStatusContext exportStatusContext) {
        long exportJobRunTime = exportJobState.getFinishedTime().getEpochSecond() - exportJobState.getCreatedTime().getEpochSecond();
        if (exportJobState.getStatus() == ExportProgressStatus.COMPLETED) {
            this.publishFullExportFinishedAnalyticEvent(exportStatusContext, exportJobRunTime);
        } else if (exportJobState.getStatus() == ExportProgressStatus.FAILED) {
            this.publishFullExportFailedAnalyticEvent(exportJobRunTime);
        } else if (exportJobState.getStatus() == ExportProgressStatus.CANCELLED) {
            this.publishFullExportCancelledAnalyticEvent(exportJobRunTime);
        }
    }

    private void publishFullExportStartedAnalyticEvent(Instant exportFrom, ExportJobState exportJobState) {
        this.eventPublisherService.publish(new FullExportStartedAnalyticEvent(this.eventPublisherService.getPluginVersion(), DateDifferenceUtil.absoluteDifferenceInDays(exportFrom, exportJobState.getCreatedTime())));
    }

    private void publishFullExportFinishedAnalyticEvent(ExportStatusContext exportStatusContext, long exportJobRunTime) {
        this.eventPublisherService.publish(new FullExportFinishedAnalyticEvent(this.eventPublisherService.getPluginVersion(), exportJobRunTime, exportStatusContext.getExportedEntities(), exportStatusContext.getRowCount()));
    }

    private void publishFullExportFailedAnalyticEvent(long exportJobRunTime) {
        this.eventPublisherService.publish(new FullExportFailedAnalyticEvent(this.eventPublisherService.getPluginVersion(), exportJobRunTime));
    }

    private void publishFullExportCancelledAnalyticEvent(long exportJobRunTime) {
        this.eventPublisherService.publish(new FullExportCancelledAnalyticEvent(this.eventPublisherService.getPluginVersion(), exportJobRunTime));
    }

    private ExportJobState createExportJobState(int schemaVersion, Instant exportFrom, boolean forcedExport, Path rootExportPath) {
        ExportJobState exportJobState = this.exportJobStateService.create(schemaVersion, exportFrom, forcedExport, rootExportPath);
        boolean shouldAbortExport = this.shouldAbortExport(schemaVersion);
        ExportStatusContext context = new ExportStatusContext(this.getExportJobState(exportJobState.getStatus(), shouldAbortExport, forcedExport), forcedExport);
        Optional dataset = this.datasetProvider.getDataset(schemaVersion);
        if (dataset.isPresent()) {
            if (((Dataset)dataset.get()).isDeprecated()) {
                context.addWarning("export.schema.version.deprecated", "Schema version " + schemaVersion + " is deprecated");
            }
            ((Dataset)dataset.get()).getChannels().stream().map(DataChannel::getLogRecordStreamer).map(logRecordStreamer -> logRecordStreamer.isReady().getDetailMessage()).filter(message -> !message.isEmpty()).forEach(message -> context.addPreValidationError((String)message, forcedExport));
        } else {
            context.addError("export.schema.version.missing", "Schema version " + schemaVersion + " is missing");
        }
        if (!shouldAbortExport && context.getErrors().isEmpty() && context.getWarnings().isEmpty()) {
            return exportJobState;
        }
        return this.exportJobStateService.updateExportJobState(exportJobState, context);
    }

    private ExportProgressStatus getExportJobState(ExportProgressStatus initialProgressStatus, boolean shouldAbortExport, boolean forcedExport) {
        if (shouldAbortExport && !forcedExport) {
            return ExportProgressStatus.FAILED;
        }
        return initialProgressStatus;
    }

    private boolean shouldAbortExport(int schemaVersion) {
        Optional dataset = this.datasetProvider.getDataset(schemaVersion);
        return dataset.map(value -> value.getChannels().stream().map(DataChannel::getLogRecordStreamer).map(LogRecordStreamer::isReady).anyMatch(validationResult -> validationResult.getAction() == StreamerValidationResult.Action.ABORT)).orElse(true);
    }
}

