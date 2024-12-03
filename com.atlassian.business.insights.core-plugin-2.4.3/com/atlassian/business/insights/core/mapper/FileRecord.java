/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.business.insights.api.schema.FileSchema
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.mapper;

import com.atlassian.business.insights.api.schema.FileSchema;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

public class FileRecord {
    private final FileSchema fileSchema;
    private final List<Map<String, Object>> rows;

    private FileRecord(@Nonnull List<Map<String, Object>> rows, @Nonnull FileSchema fileSchema) {
        this.rows = Objects.requireNonNull(rows);
        this.fileSchema = Objects.requireNonNull(fileSchema);
    }

    public static FileRecord getInstance(@Nonnull Map<String, Object> logRecordPayload, @Nonnull FileSchema fileSchema) {
        Objects.requireNonNull(logRecordPayload);
        Objects.requireNonNull(fileSchema);
        List rows = fileSchema.computeEntries(logRecordPayload);
        return new FileRecord(rows, fileSchema);
    }

    @Nonnull
    public List<Map<String, Object>> getRows() {
        return this.rows;
    }

    @Nonnull
    public FileSchema getSchemaSpecFile() {
        return this.fileSchema;
    }
}

