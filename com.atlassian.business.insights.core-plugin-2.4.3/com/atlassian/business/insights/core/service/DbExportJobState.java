/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.business.insights.core.service;

import com.atlassian.business.insights.core.ao.dao.entity.AoDataPipelineJob;
import com.atlassian.business.insights.core.ao.dao.entity.ExportProgressStatus;
import com.atlassian.business.insights.core.ao.dao.entity.Metadata;
import com.atlassian.business.insights.core.rest.validation.DiagnosticDescription;
import com.atlassian.business.insights.core.service.api.ExportJobState;
import com.atlassian.business.insights.core.service.api.OptedOutEntity;
import com.atlassian.business.insights.core.util.DbSerializationUtil;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DbExportJobState
implements ExportJobState {
    private static final int ID_UNASSIGNED = -2;
    private final int id;
    private final Instant createdTime;
    private final Instant updatedTime;
    private final Instant fromTime;
    private final ExportProgressStatus status;
    private final Metadata metadata;
    private final int schemaVersion;
    private final Integer exportedEntities;
    private final Integer writtenRows;
    private final boolean exportForced;
    private final List<DiagnosticDescription> errors;
    private final List<DiagnosticDescription> warnings;
    private final Path rootExportPath;
    private final List<OptedOutEntity> optedOutEntities;

    private DbExportJobState(int id, @Nonnull Instant created, @Nonnull Instant updated, @Nonnull Instant from, @Nonnull ExportProgressStatus status, Metadata metadata, int schemaVersion, Integer exportedEntities, Integer writtenRows, boolean exportForced, List<DiagnosticDescription> errors, List<DiagnosticDescription> warnings, Path rootExportPath, List<OptedOutEntity> optedOutEntities) {
        this.id = id;
        this.createdTime = Objects.requireNonNull(created);
        this.updatedTime = Objects.requireNonNull(updated);
        this.fromTime = Objects.requireNonNull(from);
        this.status = Objects.requireNonNull(status);
        this.metadata = metadata;
        this.schemaVersion = schemaVersion;
        this.exportedEntities = exportedEntities;
        this.writtenRows = writtenRows;
        this.exportForced = exportForced;
        this.errors = errors;
        this.warnings = warnings;
        this.rootExportPath = rootExportPath;
        this.optedOutEntities = optedOutEntities;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    @Nonnull
    public Instant getUpdatedTime() {
        return this.updatedTime;
    }

    @Override
    @Nonnull
    public ExportProgressStatus getStatus() {
        return this.status;
    }

    @Override
    @Nullable
    public List<DiagnosticDescription> getErrors() {
        return this.errors;
    }

    @Override
    @Nullable
    public List<DiagnosticDescription> getWarnings() {
        return this.warnings;
    }

    @Override
    @Nullable
    public Metadata getMetadata() {
        return this.metadata;
    }

    @Override
    public int getSchemaVersion() {
        return this.schemaVersion;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public boolean isRunning() {
        return ExportProgressStatus.STARTED == this.status;
    }

    @Override
    public boolean jobExists() {
        return this.id != -2;
    }

    @Override
    @Nonnull
    public Instant getCreatedTime() {
        return this.createdTime;
    }

    @Override
    @Nonnull
    public Instant getJobConfiguredFromTime() {
        return this.fromTime;
    }

    @Override
    @Nullable
    public Instant getFinishedTime() {
        return !this.isRunning() && this.status != null ? this.getUpdatedTime() : null;
    }

    @Override
    @Nullable
    public Instant getCancelledTime() {
        return ExportProgressStatus.CANCELLED == this.status ? this.getUpdatedTime() : null;
    }

    @Override
    public Integer getExportedEntities() {
        return this.exportedEntities;
    }

    @Override
    public Integer getWrittenRows() {
        return this.writtenRows;
    }

    @Override
    public boolean isExportForced() {
        return this.exportForced;
    }

    @Override
    @Nullable
    public Path getRootExportPath() {
        return this.rootExportPath;
    }

    @Override
    @Nullable
    public List<OptedOutEntity> getOptedOutEntities() {
        return this.optedOutEntities;
    }

    public static class Builder {
        private int id = -2;
        private Instant created;
        private Instant updated;
        private Instant from;
        private ExportProgressStatus status;
        private Metadata metadata;
        private int schemaVersion;
        private boolean exportForced;
        private Integer exportedEntities;
        private Integer writtenRows;
        private List<DiagnosticDescription> errors;
        private List<DiagnosticDescription> warnings;
        private Path rootExportPath;
        private List<OptedOutEntity> optedOutEntities;

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder created(Instant created) {
            this.created = created;
            return this;
        }

        public Builder updated(Instant updated) {
            this.updated = updated;
            return this;
        }

        public Builder from(Instant from) {
            this.from = from;
            return this;
        }

        public Builder status(ExportProgressStatus status) {
            this.status = status;
            return this;
        }

        public Builder metadata(Metadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder schemaVersion(int schemaVersion) {
            this.schemaVersion = schemaVersion;
            return this;
        }

        public Builder writtenRows(Integer processedRows) {
            this.writtenRows = processedRows;
            return this;
        }

        public Builder exportedEntities(Integer exportedEntities) {
            this.exportedEntities = exportedEntities;
            return this;
        }

        public Builder exportForced(boolean exportForced) {
            this.exportForced = exportForced;
            return this;
        }

        public Builder errors(List<DiagnosticDescription> errors) {
            this.errors = errors;
            return this;
        }

        public Builder warnings(List<DiagnosticDescription> warnings) {
            this.warnings = warnings;
            return this;
        }

        public Builder rootExportPath(Path rootExportPath) {
            this.rootExportPath = rootExportPath;
            return this;
        }

        public Builder optedOutEntities(List<OptedOutEntity> optedOutEntities) {
            this.optedOutEntities = optedOutEntities;
            return this;
        }

        public ExportJobState build() {
            if (this.created == null) {
                this.created = Instant.now();
            }
            if (this.updated == null) {
                this.updated = Instant.now();
            }
            return new DbExportJobState(this.id, this.created, this.updated, this.from, this.status, this.metadata, this.schemaVersion, this.exportedEntities, this.writtenRows, this.exportForced, this.errors, this.warnings, this.rootExportPath, this.optedOutEntities);
        }

        public Builder from(@Nonnull AoDataPipelineJob dataPipelineJob) {
            this.id = dataPipelineJob.getID();
            this.created(Instant.ofEpochMilli(dataPipelineJob.getCreated()));
            this.updated(Instant.ofEpochMilli(dataPipelineJob.getUpdated()));
            this.from(Instant.ofEpochMilli(dataPipelineJob.getExportFrom()));
            this.status(dataPipelineJob.getStatus());
            this.metadata(Metadata.fromSerializedStr(dataPipelineJob.getMetadata()));
            this.schemaVersion(dataPipelineJob.getSchemaVersion());
            this.exportedEntities(dataPipelineJob.getExportedEntities());
            this.writtenRows(dataPipelineJob.getWrittenRows());
            this.exportForced(dataPipelineJob.isExportForced());
            this.errors(DbSerializationUtil.deserializeDiagnosticDescriptions(dataPipelineJob.getErrors()));
            this.warnings(DbSerializationUtil.deserializeDiagnosticDescriptions(dataPipelineJob.getWarnings()));
            this.rootExportPath(Optional.ofNullable(dataPipelineJob.getRootExportPath()).map(x$0 -> Paths.get(x$0, new String[0])).orElse(null));
            this.optedOutEntities(DbSerializationUtil.deserializeOptedOutEntities(dataPipelineJob.getOptedOutEntityIdentifiers()));
            return this;
        }
    }
}

