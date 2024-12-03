/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.business.insights.api.schema.FileSchema
 *  com.atlassian.business.insights.api.writer.FileFormat
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.writer.api;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.business.insights.api.schema.FileSchema;
import com.atlassian.business.insights.api.writer.FileFormat;
import java.nio.file.Path;
import java.time.Instant;
import javax.annotation.Nonnull;

@ExperimentalApi
public interface FileSystemConfig {
    @Nonnull
    public FileFormat fileFormat();

    @Nonnull
    public Path generateNewFilePath(@Nonnull FileSchema var1, int var2, int var3, @Nonnull Instant var4, @Nonnull Path var5);
}

