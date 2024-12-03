/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.business.insights.api.cluster.ClusterInfo
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.service;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.audit.api.AuditService;
import com.atlassian.business.insights.api.cluster.ClusterInfo;
import com.atlassian.business.insights.core.ao.dao.AoDataPipelineJobDao;
import com.atlassian.business.insights.core.ao.dao.entity.AoDataPipelineJob;
import com.atlassian.business.insights.core.ao.dao.entity.ExportProgressStatus;
import com.atlassian.business.insights.core.ao.dao.entity.Metadata;
import com.atlassian.business.insights.core.audit.AuditEventFactory;
import com.atlassian.business.insights.core.rest.validation.DiagnosticDescription;
import com.atlassian.business.insights.core.service.DbExportJobState;
import com.atlassian.business.insights.core.service.ExportStatusContext;
import com.atlassian.business.insights.core.service.api.ExportJobState;
import com.atlassian.business.insights.core.service.api.ExportJobStateService;
import com.atlassian.business.insights.core.service.api.OptedOutEntity;
import com.atlassian.business.insights.core.util.DbSerializationUtil;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class DbExportJobStateService
implements ExportJobStateService {
    public static final int NO_JOB = -1;
    @VisibleForTesting
    static final String DEFAULT_NODE_NAME = "defaultNode";
    private static final Duration JOB_CANCELLED_DB_UPDATE_FREQUENCY = Duration.of(15L, ChronoUnit.SECONDS);
    private static final Set<ExportProgressStatus> FINISHED_EXPORT_STATUSES = EnumSet.of(ExportProgressStatus.COMPLETED, ExportProgressStatus.FAILED, ExportProgressStatus.CANCELLED);
    private final AuditService auditService;
    private final AoDataPipelineJobDao dao;
    private final AtomicInteger jobIdShouldBeCancelled = new AtomicInteger(-1);
    private final ClusterInfo clusterInfo;
    private final TransactionTemplate transactionTemplate;
    private Instant lastPolledForDbStatus = Instant.MIN;

    public DbExportJobStateService(@Nonnull AuditService auditService, @Nonnull AoDataPipelineJobDao dao, @Nonnull TransactionTemplate transactionTemplate, @Nonnull ClusterInfo clusterInfo) {
        this.auditService = Objects.requireNonNull(auditService, "auditService must not be null");
        this.dao = Objects.requireNonNull(dao, "dao must not be null");
        this.transactionTemplate = Objects.requireNonNull(transactionTemplate, "transactionTemplate must not be null");
        this.clusterInfo = Objects.requireNonNull(clusterInfo, "clusterInfo must not be null");
    }

    @Override
    @Nonnull
    public ExportJobState create(int schemaVersion, @Nonnull Instant exportFrom, boolean forceExport, @Nonnull Path rootExportPath) {
        Objects.requireNonNull(exportFrom, "exportFrom");
        Objects.requireNonNull(rootExportPath, "rootExportPath");
        Instant currentTime = Instant.now();
        ExportJobState exportJobState = DbExportJobState.builder().status(ExportProgressStatus.STARTED).created(currentTime).updated(currentTime).metadata(this.getMetadataInfo()).from(exportFrom).schemaVersion(schemaVersion).exportForced(forceExport).rootExportPath(rootExportPath).build();
        AoDataPipelineJob createdJob = this.dao.add(exportJobState);
        return (ExportJobState)this.transactionTemplate.execute(() -> DbExportJobState.builder().from(createdJob).build());
    }

    @Override
    @Nonnull
    public Optional<ExportJobState> findJobById(int jobId) {
        return this.dao.findById(jobId).map(foundJob -> DbExportJobState.builder().from((AoDataPipelineJob)foundJob).build());
    }

    @Override
    @Nonnull
    public Optional<ExportJobState> getLatestExportJobState() {
        return this.dao.findLastJob().map(lastJob -> DbExportJobState.builder().from((AoDataPipelineJob)lastJob).build());
    }

    @Override
    @Nonnull
    public Optional<ExportJobState> getLatestRunningExportJobState() {
        return this.dao.findLastRunningJob().map(lastRunningJob -> DbExportJobState.builder().from((AoDataPipelineJob)lastRunningJob).build());
    }

    @Override
    @Nonnull
    public List<ExportJobState> getExportJobStates(int offset, int limit) {
        return this.dao.getJobs(offset, limit).stream().map(job -> DbExportJobState.builder().from((AoDataPipelineJob)job).build()).collect(Collectors.toList());
    }

    @Override
    public boolean canJobBeCancelled(@Nonnull ExportJobState exportJobState) {
        Objects.requireNonNull(exportJobState, "exportJobState");
        return !this.isFinished(exportJobState.getStatus());
    }

    @Override
    public boolean shouldJobBeCancelled(@Nonnull ExportJobState exportJobState) {
        Objects.requireNonNull(exportJobState);
        if (Instant.now().isAfter(this.lastPolledForDbStatus.plus(JOB_CANCELLED_DB_UPDATE_FREQUENCY))) {
            this.dao.findById(exportJobState.getId()).ifPresent(job -> {
                if (ExportProgressStatus.CANCELLATION_REQUESTED.equals((Object)job.getStatus()) || ExportProgressStatus.CANCELLED.equals((Object)job.getStatus())) {
                    this.jobIdShouldBeCancelled.set(job.getID());
                } else {
                    this.jobIdShouldBeCancelled.set(-1);
                }
            });
            this.lastPolledForDbStatus = Instant.now();
        }
        return exportJobState.jobExists() && this.jobIdShouldBeCancelled.get() == exportJobState.getId();
    }

    @Override
    @Nonnull
    public Optional<ExportJobState> requestCancellation(@Nonnull ExportJobState exportJobState) {
        Objects.requireNonNull(exportJobState, "exportJobState");
        if (!exportJobState.jobExists()) {
            return Optional.empty();
        }
        Optional<ExportJobState> cancellationRequestedExportJobState = this.dao.findById(exportJobState.getId()).map(jobToCancel -> this.updateStatus((AoDataPipelineJob)jobToCancel, ExportProgressStatus.CANCELLATION_REQUESTED, jobToCancel.getExportedEntities(), jobToCancel.getWrittenRows(), jobToCancel.isExportForced(), jobToCancel.getErrors(), jobToCancel.getWarnings(), DbSerializationUtil.deserializeOptedOutEntities(jobToCancel.getOptedOutEntityIdentifiers())));
        this.auditService.audit(AuditEventFactory.createFullExportCancelledAuditEvent(exportJobState.getId()));
        return cancellationRequestedExportJobState;
    }

    @Override
    @Nonnull
    public ExportJobState updateExportJobState(@Nonnull ExportJobState exportJobState, @Nonnull ExportStatusContext exportStatusContext) {
        Objects.requireNonNull(exportJobState);
        Objects.requireNonNull(exportStatusContext);
        ArrayList<DiagnosticDescription> errors = new ArrayList<DiagnosticDescription>(exportStatusContext.getErrors());
        if (exportJobState.getErrors() != null) {
            errors.addAll(exportJobState.getErrors());
        }
        ArrayList<DiagnosticDescription> warnings = new ArrayList<DiagnosticDescription>(exportStatusContext.getWarnings());
        if (exportJobState.getWarnings() != null) {
            warnings.addAll(exportJobState.getWarnings());
        }
        return this.updateStatus(this.getJobOrThrow(exportJobState.getId()), exportStatusContext.getExportProgressStatus(), exportStatusContext.getExportedEntities(), exportStatusContext.getRowCount(), exportStatusContext.isForcedExport(), DbSerializationUtil.serializeDiagnosticDescriptions(errors), DbSerializationUtil.serializeDiagnosticDescriptions(warnings), exportStatusContext.getOptedOutEntities());
    }

    private Metadata getMetadataInfo() {
        String nodeId = Optional.ofNullable(this.clusterInfo.getNodeId()).orElse(DEFAULT_NODE_NAME);
        return Metadata.getInstance(nodeId);
    }

    private AoDataPipelineJob getJobOrThrow(int jobId) {
        return this.dao.findById(jobId).orElseThrow(() -> new IllegalStateException(String.format("Could not find AoJobHistory with id [%d]", jobId)));
    }

    private boolean isFinished(ExportProgressStatus exportProgressStatus) {
        return FINISHED_EXPORT_STATUSES.contains((Object)exportProgressStatus);
    }

    private ExportJobState updateStatus(AoDataPipelineJob dataPipelineJob, ExportProgressStatus status, Integer exportedEntities, Integer writtenRows, boolean isExportForced, String serializedErrors, String serializedWarnings, List<OptedOutEntity> optedOutEntities) {
        if (this.isFinished(dataPipelineJob.getStatus())) {
            throw new IllegalStateException(String.format("The terminal status %s of the export state %d can't be set to %s", new Object[]{status, dataPipelineJob.getID(), status}));
        }
        if (ExportProgressStatus.CANCELLATION_REQUESTED.equals((Object)status)) {
            this.jobIdShouldBeCancelled.set(dataPipelineJob.getID());
        }
        dataPipelineJob.setStatus(status);
        dataPipelineJob.setUpdated(Instant.now().toEpochMilli());
        dataPipelineJob.setWrittenRows(writtenRows);
        dataPipelineJob.setExportedEntities(exportedEntities);
        dataPipelineJob.setExportForced(isExportForced);
        dataPipelineJob.setErrors(serializedErrors);
        dataPipelineJob.setWarnings(serializedWarnings);
        dataPipelineJob.setOptedOutEntityIdentifiers(DbSerializationUtil.serializeOptedOutEntities(optedOutEntities));
        AoDataPipelineJob updatedJobHistory = this.dao.update(dataPipelineJob);
        return DbExportJobState.builder().from(updatedJobHistory).build();
    }
}

