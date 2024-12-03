/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.business.insights.api.schema.FileSchema
 *  com.atlassian.business.insights.api.writer.FileFormat
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.writer;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.business.insights.api.schema.FileSchema;
import com.atlassian.business.insights.api.writer.FileFormat;
import com.atlassian.business.insights.core.util.DateConversionUtil;
import com.atlassian.business.insights.core.writer.api.FileSystemConfig;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;
import javax.annotation.Nonnull;

public class DatasetFileSystemConfig
implements FileSystemConfig {
    @VisibleForTesting
    static final String DATASET_FILE_TEMPLATE = "%s_job%s_%s.%s";
    static final String DATASET_FILE_VERSION_TEMPLATE = "%s_job%s_v%d_%s.%s";
    static final int LEGACY_VERSION = 1;
    private final FileFormat format;
    private final DateConversionUtil dateConversionUtil;

    public DatasetFileSystemConfig(@Nonnull FileFormat format, @Nonnull DateConversionUtil dateConversionUtil) {
        Objects.requireNonNull(format, "format must not be null");
        Objects.requireNonNull(dateConversionUtil, "dateConversionUtil must not be null");
        this.format = format;
        this.dateConversionUtil = dateConversionUtil;
    }

    @Override
    @Nonnull
    public FileFormat fileFormat() {
        return this.format;
    }

    @Override
    @Nonnull
    public Path generateNewFilePath(@Nonnull FileSchema fileSchema, int jobId, int version, @Nonnull Instant startTime, @Nonnull Path rootExportPath) {
        Objects.requireNonNull(fileSchema);
        Objects.requireNonNull(startTime);
        Objects.requireNonNull(rootExportPath);
        return rootExportPath.resolve(String.valueOf(jobId)).resolve(this.generateFilename(fileSchema, jobId, version, startTime));
    }

    private String generateFilename(FileSchema fileSchema, int jobId, int version, Instant startTime) {
        if (version == 1) {
            return String.format(DATASET_FILE_TEMPLATE, fileSchema.getFileNamePrefix(), jobId, this.dateConversionUtil.formatToSystemTimeZone(startTime), this.format.getFileExtension());
        }
        return String.format(DATASET_FILE_VERSION_TEMPLATE, fileSchema.getFileNamePrefix(), jobId, version, this.dateConversionUtil.formatToSystemTimeZone(startTime), this.format.getFileExtension());
    }
}

