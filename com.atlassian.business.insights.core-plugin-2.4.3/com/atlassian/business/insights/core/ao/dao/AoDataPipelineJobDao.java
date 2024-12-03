/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.ao.dao;

import com.atlassian.business.insights.core.ao.dao.entity.AoDataPipelineJob;
import com.atlassian.business.insights.core.service.api.ExportJobState;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface AoDataPipelineJobDao {
    @Nonnull
    public AoDataPipelineJob add(@Nonnull ExportJobState var1);

    @Nonnull
    public AoDataPipelineJob update(@Nonnull AoDataPipelineJob var1);

    @Nonnull
    public Optional<AoDataPipelineJob> findById(int var1);

    @Nonnull
    public Optional<AoDataPipelineJob> findLastJob();

    @Nonnull
    public Optional<AoDataPipelineJob> findLastRunningJob();

    @Nonnull
    public List<AoDataPipelineJob> getJobs(int var1, int var2) throws IllegalArgumentException;
}

