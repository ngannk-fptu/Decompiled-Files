/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.business.insights.api.config.PropertiesProvider
 *  com.atlassian.business.insights.api.exceptions.DatasetWriterCreationException
 *  com.atlassian.business.insights.api.schema.FileSchema
 *  com.atlassian.business.insights.api.schema.Schema
 *  com.atlassian.business.insights.api.writer.FileFormat
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.writer;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.business.insights.api.config.PropertiesProvider;
import com.atlassian.business.insights.api.exceptions.DatasetWriterCreationException;
import com.atlassian.business.insights.api.schema.FileSchema;
import com.atlassian.business.insights.api.schema.Schema;
import com.atlassian.business.insights.api.writer.FileFormat;
import com.atlassian.business.insights.core.writer.CsvMapWriter;
import com.atlassian.business.insights.core.writer.ScatterDatasetWriter;
import com.atlassian.business.insights.core.writer.api.DatasetWriter;
import com.atlassian.business.insights.core.writer.api.DatasetWriterFactory;
import com.atlassian.business.insights.core.writer.api.FileSystemConfig;
import com.atlassian.business.insights.core.writer.api.MapWriter;
import com.atlassian.business.insights.core.writer.convert.EmbeddedLineBreakValueConverter;
import com.atlassian.business.insights.core.writer.convert.ValueConverter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class FilesystemDatasetWriterFactory
implements DatasetWriterFactory {
    @VisibleForTesting
    static final String EMBEDDED_LINE_BREAKS_PRESERVE_KEY = "plugin.data.pipeline.embedded.line.break.preserve";
    @VisibleForTesting
    static final String ESCAPE_CHAR_KEY = "plugin.data.pipeline.embedded.line.break.escape.char";
    @VisibleForTesting
    static final String ESCAPE_CHAR_DEFAULT = "\\n";
    private final FileSystemConfig fileSystemConfig;
    private final List<ValueConverter> mandatoryValueConverters;
    private final PropertiesProvider propertiesProvider;

    public FilesystemDatasetWriterFactory(FileSystemConfig fileSystemConfig, List<ValueConverter> mandatoryValueConverters, PropertiesProvider propertiesProvider) {
        this.fileSystemConfig = fileSystemConfig;
        this.mandatoryValueConverters = mandatoryValueConverters;
        this.propertiesProvider = propertiesProvider;
    }

    @Override
    @Nonnull
    public DatasetWriter create(@Nonnull Schema schema, int jobId, int version, @Nonnull Instant startTime, @Nonnull Path rootExportPath) {
        Objects.requireNonNull(startTime);
        Objects.requireNonNull(rootExportPath);
        if (this.fileSystemConfig.fileFormat() == FileFormat.CSV) {
            List<ValueConverter> valueConverters = this.getValueConverters();
            return new ScatterDatasetWriter((Map<FileSchema, MapWriter>)Maps.toMap(this.enabledFileSchemas(schema.getFileSchemas()), fileSchema -> new CsvMapWriter(this.getWriterForFilePath(this.fileSystemConfig.generateNewFilePath((FileSchema)fileSchema, jobId, version, startTime, rootExportPath)), valueConverters)));
        }
        throw new IllegalArgumentException("No writer found for specific fileFormat " + this.fileSystemConfig.fileFormat());
    }

    private List<FileSchema> enabledFileSchemas(@Nonnull List<FileSchema> fileSchemas) {
        return fileSchemas.stream().filter(FileSchema::isEnabled).collect(Collectors.toList());
    }

    private List<ValueConverter> getValueConverters() {
        boolean preserveEmbeddedLineBreaks = this.propertiesProvider.getBoolean(EMBEDDED_LINE_BREAKS_PRESERVE_KEY);
        if (preserveEmbeddedLineBreaks) {
            return this.mandatoryValueConverters;
        }
        String escapeChar = this.propertiesProvider.getProperty(ESCAPE_CHAR_KEY, ESCAPE_CHAR_DEFAULT);
        ArrayList withLineBreakConverter = Lists.newArrayList(this.mandatoryValueConverters);
        withLineBreakConverter.add(new EmbeddedLineBreakValueConverter(escapeChar));
        return withLineBreakConverter;
    }

    private Writer getWriterForFilePath(Path filePath) {
        try {
            if (filePath.toFile().exists()) {
                throw new FileAlreadyExistsException(filePath.toAbsolutePath().toString());
            }
            Files.createDirectories(filePath.getParent(), new FileAttribute[0]);
            return new OutputStreamWriter(Files.newOutputStream(filePath, new OpenOption[0]), this.getUtf8Encoder(CodingErrorAction.REPLACE));
        }
        catch (IOException e) {
            throw new DatasetWriterCreationException((Throwable)e);
        }
    }

    private CharsetEncoder getUtf8Encoder(CodingErrorAction codingErrorAction) {
        return StandardCharsets.UTF_8.newEncoder().onMalformedInput(codingErrorAction).onUnmappableCharacter(codingErrorAction);
    }
}

