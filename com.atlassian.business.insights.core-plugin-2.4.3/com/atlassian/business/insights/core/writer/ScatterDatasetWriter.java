/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.business.insights.api.schema.FileSchema
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.business.insights.core.writer;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.business.insights.api.schema.FileSchema;
import com.atlassian.business.insights.core.mapper.FileRecord;
import com.atlassian.business.insights.core.writer.api.DatasetWriter;
import com.atlassian.business.insights.core.writer.api.MapWriter;
import com.atlassian.business.insights.core.writer.exception.MapWriterWriteException;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScatterDatasetWriter
implements DatasetWriter {
    private static final Logger log = LoggerFactory.getLogger(ScatterDatasetWriter.class);
    private final Map<FileSchema, MapWriter> fileSchemaMapWriterMap;

    public ScatterDatasetWriter(Map<FileSchema, MapWriter> fileSchemaMapWriterMap) {
        this.fileSchemaMapWriterMap = fileSchemaMapWriterMap;
    }

    private int doWrite(@Nonnull FileRecord data) throws MapWriterWriteException {
        Objects.requireNonNull(data);
        return this.fileSchemaMapWriterMap.get(data.getSchemaSpecFile()).write(data.getRows());
    }

    private String[] getHeaders(@Nonnull FileSchema fileSchema) {
        return fileSchema.getFields().values().toArray(new String[0]);
    }

    @Override
    public void writeHeaders() throws IOException {
        for (Map.Entry<FileSchema, MapWriter> fileSchemaMapWriter : this.fileSchemaMapWriterMap.entrySet()) {
            try {
                fileSchemaMapWriter.getValue().writeHeaders(this.getHeaders(fileSchemaMapWriter.getKey()));
            }
            catch (MapWriterWriteException e) {
                throw new IOException(e.getMessage(), e.getCause());
            }
        }
    }

    @Override
    public int write(@Nonnull Stream<FileRecord> data) {
        Objects.requireNonNull(data);
        return data.mapToInt(fileRecord -> {
            try {
                return this.doWrite((FileRecord)fileRecord);
            }
            catch (MapWriterWriteException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }).sum();
    }

    @Override
    public void close() throws Exception {
        Exception exception = null;
        for (MapWriter writer : this.fileSchemaMapWriterMap.values()) {
            try {
                writer.close();
            }
            catch (Exception e) {
                if (exception == null) {
                    exception = e;
                } else {
                    exception.addSuppressed(e);
                }
                log.error("Failed to close writer {}", (Object)writer, (Object)e);
            }
        }
        if (exception != null) {
            throw exception;
        }
    }

    @VisibleForTesting
    Map<FileSchema, MapWriter> getFileSchemaMapWriterMap() {
        return this.fileSchemaMapWriterMap;
    }
}

