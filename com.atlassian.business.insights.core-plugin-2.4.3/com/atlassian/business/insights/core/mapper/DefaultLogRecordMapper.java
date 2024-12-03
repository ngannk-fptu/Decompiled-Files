/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.business.insights.api.LogRecord
 *  com.atlassian.business.insights.api.schema.FileSchema
 *  com.atlassian.business.insights.api.schema.Schema
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.mapper;

import com.atlassian.business.insights.api.LogRecord;
import com.atlassian.business.insights.api.schema.FileSchema;
import com.atlassian.business.insights.api.schema.Schema;
import com.atlassian.business.insights.core.mapper.FileRecord;
import com.atlassian.business.insights.core.mapper.api.LogRecordMapper;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public class DefaultLogRecordMapper<K>
implements LogRecordMapper<K> {
    @Override
    @Nonnull
    public Stream<FileRecord> map(@Nonnull Schema schema, @Nonnull LogRecord<K> logRecord) {
        Objects.requireNonNull(logRecord);
        return schema.getFileSchemas().stream().filter(FileSchema::isEnabled).map(fileSchema -> FileRecord.getInstance(logRecord.getPayload(), fileSchema)).filter(fileRecord -> !fileRecord.getRows().isEmpty());
    }
}

