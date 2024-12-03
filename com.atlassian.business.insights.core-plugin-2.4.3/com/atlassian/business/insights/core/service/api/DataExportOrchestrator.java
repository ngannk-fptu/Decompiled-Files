/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.business.insights.api.dataset.Dataset
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.service.api;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.business.insights.api.dataset.Dataset;
import com.atlassian.business.insights.core.service.api.ExportJobState;
import java.time.Instant;
import javax.annotation.Nonnull;

@ExperimentalApi
public interface DataExportOrchestrator {
    @Nonnull
    public ExportJobState runFullExport(@Nonnull Dataset var1, @Nonnull Instant var2, boolean var3);

    @Nonnull
    public ExportJobState runScheduledExport(int var1, @Nonnull Instant var2, boolean var3);
}

