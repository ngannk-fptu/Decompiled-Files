/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  javax.annotation.Nonnull
 *  net.java.ao.DBParam
 *  net.java.ao.Query
 */
package com.atlassian.business.insights.core.ao.dao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.business.insights.core.ao.dao.AoDataPipelineJobDao;
import com.atlassian.business.insights.core.ao.dao.entity.AoDataPipelineJob;
import com.atlassian.business.insights.core.ao.dao.entity.ExportProgressStatus;
import com.atlassian.business.insights.core.service.api.ExportJobState;
import com.atlassian.business.insights.core.util.DbSerializationUtil;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.java.ao.DBParam;
import net.java.ao.Query;

public class DefaultAoDataPipelineJobDao
implements AoDataPipelineJobDao {
    private final ActiveObjects ao;

    public DefaultAoDataPipelineJobDao(ActiveObjects ao) {
        this.ao = Objects.requireNonNull(ao);
    }

    @Override
    @Nonnull
    public AoDataPipelineJob add(@Nonnull ExportJobState job) {
        Objects.requireNonNull(job);
        return (AoDataPipelineJob)this.ao.executeInTransaction(() -> {
            AoDataPipelineJob aoJob = (AoDataPipelineJob)this.ao.create(AoDataPipelineJob.class, new DBParam[]{new DBParam("CREATED", (Object)job.getCreatedTime().toEpochMilli()), new DBParam("UPDATED", (Object)job.getUpdatedTime().toEpochMilli()), new DBParam("EXPORT_FROM", (Object)job.getJobConfiguredFromTime().toEpochMilli()), new DBParam("STATUS", (Object)job.getStatus()), new DBParam("SCHEMA_VERSION", (Object)job.getSchemaVersion()), new DBParam("EXPORTED_ENTITIES", (Object)job.getExportedEntities()), new DBParam("WRITTEN_ROWS", (Object)job.getWrittenRows()), new DBParam("EXPORT_FORCED", (Object)job.isExportForced()), new DBParam("WARNINGS", (Object)DbSerializationUtil.serializeDiagnosticDescriptions(job.getWarnings())), new DBParam("ERRORS", (Object)DbSerializationUtil.serializeDiagnosticDescriptions(job.getErrors())), new DBParam("METADATA", (Object)job.getMetadata().toString()), new DBParam("EXPORT_PATH", Optional.ofNullable(job.getRootExportPath()).map(path -> path.toAbsolutePath().toString()).orElse(null)), new DBParam("OPTED_OUT_ENTITY_IDENTIFIERS", (Object)(job.getOptedOutEntities() != null ? DbSerializationUtil.serializeOptedOutEntities(job.getOptedOutEntities()) : "[]"))});
            aoJob.save();
            return aoJob;
        });
    }

    @Override
    @Nonnull
    public AoDataPipelineJob update(@Nonnull AoDataPipelineJob job) {
        Objects.requireNonNull(job);
        return (AoDataPipelineJob)this.ao.executeInTransaction(() -> {
            job.save();
            return job;
        });
    }

    @Override
    @Nonnull
    public Optional<AoDataPipelineJob> findById(int id) {
        return (Optional)this.ao.executeInTransaction(() -> Arrays.stream(this.ao.find(AoDataPipelineJob.class, Query.select().where("ID = ?", new Object[]{id}))).findFirst());
    }

    @Override
    @Nonnull
    public Optional<AoDataPipelineJob> findLastJob() {
        String orderByQuery = "ID DESC";
        return (Optional)this.ao.executeInTransaction(() -> Arrays.stream(this.ao.find(AoDataPipelineJob.class, Query.select().limit(1).order(orderByQuery))).findFirst());
    }

    @Override
    @Nonnull
    public Optional<AoDataPipelineJob> findLastRunningJob() {
        String orderByQuery = "ID DESC";
        String whereQuery = "STATUS = ?";
        return (Optional)this.ao.executeInTransaction(() -> Arrays.stream(this.ao.find(AoDataPipelineJob.class, Query.select().where(whereQuery, new Object[]{ExportProgressStatus.STARTED}).limit(1).order(orderByQuery))).findFirst());
    }

    @Override
    @Nonnull
    public List<AoDataPipelineJob> getJobs(int offset, int limit) throws IllegalArgumentException {
        if (offset < 0 || limit < 0) {
            throw new IllegalArgumentException(String.format("Offset and limit need to be positive but were %d and %d", offset, limit));
        }
        String orderByQuery = "ID DESC";
        return (List)this.ao.executeInTransaction(() -> Arrays.stream(this.ao.find(AoDataPipelineJob.class, Query.select().offset(offset).limit(limit).order(orderByQuery))).collect(Collectors.toList()));
    }
}

