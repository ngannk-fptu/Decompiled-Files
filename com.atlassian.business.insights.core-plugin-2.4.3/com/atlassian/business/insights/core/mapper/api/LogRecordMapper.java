/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.business.insights.api.LogRecord
 *  com.atlassian.business.insights.api.schema.Schema
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.mapper.api;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.business.insights.api.LogRecord;
import com.atlassian.business.insights.api.schema.Schema;
import com.atlassian.business.insights.core.mapper.FileRecord;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

@ExperimentalApi
public interface LogRecordMapper<K> {
    @Nonnull
    public Stream<FileRecord> map(@Nonnull Schema var1, @Nonnull LogRecord<K> var2);
}

