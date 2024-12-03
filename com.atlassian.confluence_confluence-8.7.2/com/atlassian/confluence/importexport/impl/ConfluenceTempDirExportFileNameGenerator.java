/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.confluence.importexport.impl.ExportFileNameGenerator;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.settings.ConfluenceDirectories;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;

public class ConfluenceTempDirExportFileNameGenerator
implements ExportFileNameGenerator {
    private static final AtomicLong uniqueId = new AtomicLong(1L);
    private final Supplier<File> tempDirectoryRef;
    private final String exportDirPrefix;
    private final String extension;
    private final String timeFormat;
    private final String combinedDateTimeFormat;

    public static ExportFileNameGenerator create(ConfluenceDirectories confluenceDirectories, String exportDirPrefix, String extension, String dateFormat, String timeFormat) {
        return new ConfluenceTempDirExportFileNameGenerator(() -> confluenceDirectories.getTempDirectory().toFile(), exportDirPrefix, extension, dateFormat, timeFormat);
    }

    @Deprecated
    public ConfluenceTempDirExportFileNameGenerator(BootstrapManager bootstrapManager, String exportDirPrefix, String extension, String dateFormat, String timeFormat) {
        this(() -> ConfluenceDirectories.getLegacyTempDirectory(Objects.requireNonNull(bootstrapManager)), exportDirPrefix, extension, dateFormat, timeFormat);
    }

    ConfluenceTempDirExportFileNameGenerator(Supplier<File> tempDirectoryRef, String exportDirPrefix, String extension, String dateFormat, String timeFormat) {
        this.tempDirectoryRef = Objects.requireNonNull(tempDirectoryRef);
        this.exportDirPrefix = exportDirPrefix;
        this.extension = extension;
        this.timeFormat = timeFormat;
        this.combinedDateTimeFormat = dateFormat + "-" + timeFormat;
    }

    @Override
    public File createExportDirectory() throws IOException {
        File exportDir = this.getTempDirectoryFileInConfluenceTemp();
        if (exportDir.exists() && exportDir.isFile()) {
            throw new IOException("Export directory '" + exportDir.getAbsolutePath() + "' exists but is a file.");
        }
        if (!exportDir.exists() && !exportDir.mkdirs()) {
            throw new IOException("Couldn't create export directory " + exportDir.getAbsolutePath());
        }
        return exportDir;
    }

    private File getTempDirectoryFileInConfluenceTemp() {
        Date now = new Date();
        String fileNameDateParts = MessageFormat.format(this.combinedDateTimeFormat, now, now);
        String fileName = StringUtils.join((Iterable)ImmutableList.of((Object)this.exportDirPrefix, (Object)fileNameDateParts, (Object)uniqueId.getAndIncrement()), (char)'-');
        return new File(this.tempDirectoryRef.get(), fileName);
    }

    @Override
    public String getExportFileName(String ... differentiators) {
        StringBuilder builder = new StringBuilder();
        for (String differentiator : differentiators) {
            builder.append(differentiator).append('-');
        }
        Date now = new Date();
        builder.append(MessageFormat.format(this.timeFormat, now, now));
        builder.append('-').append(uniqueId.getAndIncrement()).append('.').append(this.extension);
        return builder.toString();
    }
}

