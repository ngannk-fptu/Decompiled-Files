/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.writer.api;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.business.insights.core.mapper.FileRecord;
import java.io.IOException;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

@ExperimentalApi
public interface DatasetWriter
extends AutoCloseable {
    public void writeHeaders() throws IOException;

    public int write(@Nonnull Stream<FileRecord> var1);
}

