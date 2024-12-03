/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.business.insights.core.service.api;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.business.insights.core.ao.dao.entity.ExportProgressStatus;
import com.atlassian.business.insights.core.ao.dao.entity.Metadata;
import com.atlassian.business.insights.core.rest.validation.DiagnosticDescription;
import com.atlassian.business.insights.core.service.api.OptedOutEntity;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ExperimentalApi
public interface ExportJobState {
    public int getId();

    public boolean isRunning();

    public boolean jobExists();

    @Nonnull
    public ExportProgressStatus getStatus();

    @Nullable
    public List<DiagnosticDescription> getErrors();

    @Nullable
    public List<DiagnosticDescription> getWarnings();

    @Nullable
    public Metadata getMetadata();

    public int getSchemaVersion();

    @Nonnull
    public Instant getCreatedTime();

    @Nullable
    public Instant getFinishedTime();

    @Nullable
    public Instant getCancelledTime();

    @Nonnull
    public Instant getUpdatedTime();

    @Nonnull
    public Instant getJobConfiguredFromTime();

    @Nullable
    public Integer getExportedEntities();

    @Nullable
    public Integer getWrittenRows();

    public boolean isExportForced();

    @Nullable
    public Path getRootExportPath();

    @Nullable
    public List<OptedOutEntity> getOptedOutEntities();
}

