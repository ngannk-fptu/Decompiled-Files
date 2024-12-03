/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.business.insights.api.schema.Schema
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.writer.api;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.business.insights.api.schema.Schema;
import com.atlassian.business.insights.core.writer.api.DatasetWriter;
import java.nio.file.Path;
import java.time.Instant;
import javax.annotation.Nonnull;

@ExperimentalApi
public interface DatasetWriterFactory {
    @Nonnull
    public DatasetWriter create(@Nonnull Schema var1, int var2, int var3, @Nonnull Instant var4, @Nonnull Path var5);
}

