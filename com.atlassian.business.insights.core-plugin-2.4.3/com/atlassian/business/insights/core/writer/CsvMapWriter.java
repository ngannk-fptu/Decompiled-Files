/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.supercsv.cellprocessor.CellProcessorAdaptor
 *  org.supercsv.cellprocessor.Optional
 *  org.supercsv.cellprocessor.ift.CellProcessor
 *  org.supercsv.io.CsvMapWriter
 *  org.supercsv.prefs.CsvPreference
 *  org.supercsv.util.CsvContext
 */
package com.atlassian.business.insights.core.writer;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.business.insights.core.writer.api.MapWriter;
import com.atlassian.business.insights.core.writer.convert.ValueConverter;
import com.atlassian.business.insights.core.writer.exception.ConversionException;
import com.atlassian.business.insights.core.writer.exception.MapWriterWriteException;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.CsvContext;

public class CsvMapWriter
implements MapWriter {
    private static final Logger log = LoggerFactory.getLogger(CsvMapWriter.class);
    @VisibleForTesting
    static final CsvPreference CSV_PREFERENCE = CsvPreference.STANDARD_PREFERENCE;
    private final org.supercsv.io.CsvMapWriter superCsvWriter;
    private final List<ValueConverter> valueConverters;
    private CellProcessor[] cellProcessors = new CellProcessor[0];
    private boolean headerWritten = false;
    private String[] storedHeaders = null;

    public CsvMapWriter(@Nonnull Writer writer, @Nonnull List<ValueConverter> valueConverters) {
        this.superCsvWriter = new org.supercsv.io.CsvMapWriter(writer, CSV_PREFERENCE);
        this.valueConverters = valueConverters;
    }

    @Override
    public boolean writeHeaders(@Nonnull String[] headers) throws MapWriterWriteException {
        Objects.requireNonNull(headers);
        if (this.headerWritten) {
            throw new IllegalStateException("Attempting to write headers multiple times");
        }
        try {
            this.superCsvWriter.writeHeader(headers);
            this.cellProcessors = this.initializeCellProcessors(headers.length);
        }
        catch (IOException e) {
            log.error("Encountered an error while writing headers to a file.", (Throwable)e);
            throw new MapWriterWriteException("Encountered an error while writing headers to a file");
        }
        this.headerWritten = true;
        this.storedHeaders = headers;
        return true;
    }

    @Override
    public boolean write(@Nonnull Map<String, Object> record) throws MapWriterWriteException {
        Objects.requireNonNull(record);
        if (record.isEmpty()) {
            throw new IllegalArgumentException("Empty record.");
        }
        try {
            if (!this.headerWritten) {
                throw new IllegalStateException("Attempting to write a record before headers are written.");
            }
            this.superCsvWriter.write(record, this.storedHeaders, this.cellProcessors);
        }
        catch (ConversionException | IOException e) {
            log.error(String.format("Encountered an error while writing dataset entry '%s' to a file.", record), (Throwable)e);
            throw new MapWriterWriteException("Encountered an error while writing to a file");
        }
        return true;
    }

    @Override
    public int write(@Nonnull List<Map<String, Object>> records) throws MapWriterWriteException {
        Objects.requireNonNull(records);
        int writtenRecords = 0;
        for (Map<String, Object> record : records) {
            boolean success = this.write(record);
            if (!success) continue;
            ++writtenRecords;
        }
        return writtenRecords;
    }

    @Nonnull
    private CellProcessor[] initializeCellProcessors(int length) {
        return (CellProcessor[])IntStream.range(0, length).mapToObj(x -> new Optional((CellProcessor)new CustomProcessor(this.valueConverters))).toArray(CellProcessor[]::new);
    }

    @Override
    public void close() throws IOException {
        this.superCsvWriter.close();
    }

    static class CustomProcessor
    extends CellProcessorAdaptor {
        private final List<ValueConverter> valueConverters;

        CustomProcessor(List<ValueConverter> valueConverters) {
            this.valueConverters = valueConverters;
        }

        public <T> T execute(@Nullable Object value, @Nullable CsvContext context) {
            return (T)this.next.execute(this.applyValueConverters(value), context);
        }

        private Object applyValueConverters(@Nullable Object val) {
            Object convertedVal = val;
            for (ValueConverter v : this.valueConverters) {
                convertedVal = v.convert(convertedVal);
            }
            return convertedVal;
        }
    }
}

