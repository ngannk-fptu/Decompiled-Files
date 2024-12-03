/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.service.api;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.business.insights.core.service.ExportStatusContext;
import com.atlassian.business.insights.core.service.api.ExportJobState;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

@ExperimentalApi
public interface ExportJobStateService {
    @Nonnull
    public ExportJobState create(int var1, @Nonnull Instant var2, boolean var3, @Nonnull Path var4);

    @Nonnull
    public Optional<ExportJobState> findJobById(int var1);

    @Nonnull
    public Optional<ExportJobState> getLatestExportJobState();

    @Nonnull
    public Optional<ExportJobState> getLatestRunningExportJobState();

    @Nonnull
    public List<ExportJobState> getExportJobStates(int var1, int var2);

    public boolean canJobBeCancelled(@Nonnull ExportJobState var1);

    public boolean shouldJobBeCancelled(@Nonnull ExportJobState var1);

    @Nonnull
    public Optional<ExportJobState> requestCancellation(@Nonnull ExportJobState var1);

    @Nonnull
    public ExportJobState updateExportJobState(@Nonnull ExportJobState var1, @Nonnull ExportStatusContext var2) throws IllegalStateException;
}

