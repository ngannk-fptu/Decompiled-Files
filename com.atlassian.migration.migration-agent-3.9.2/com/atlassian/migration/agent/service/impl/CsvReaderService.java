/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  lombok.Generated
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.supercsv.cellprocessor.CellProcessorAdaptor
 *  org.supercsv.cellprocessor.ift.CellProcessor
 *  org.supercsv.exception.SuperCsvCellProcessorException
 *  org.supercsv.exception.SuperCsvConstraintViolationException
 *  org.supercsv.io.CsvBeanReader
 *  org.supercsv.prefs.CsvPreference
 *  org.supercsv.util.CsvContext
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.migration.agent.model.CsvReadResult;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.NoSuchElementException;
import lombok.Generated;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.CsvContext;

public class CsvReaderService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(CsvReaderService.class);
    private static final int PEEK_BUFFER_SIZE = 255;

    public CsvBeanReader getCsvBeanReaderForInputStream(InputStream inputStream) {
        return new CsvBeanReader((Reader)new InputStreamReader(inputStream), this.determineCsvPreference(inputStream));
    }

    public String[] getCsvHeader(CsvBeanReader reader) throws IOException {
        return StringUtils.stripAll((String[])reader.getHeader(true));
    }

    public <T> CsvReadResult<T> readCsvLine(CsvBeanReader reader, Class<T> resultClass, String[] header, CellProcessor ... processors) {
        Object result = null;
        String errorMessage = null;
        try {
            result = reader.read(resultClass, header, processors);
            if (result == null) {
                return null;
            }
        }
        catch (SuperCsvCellProcessorException e) {
            errorMessage = String.format("Message= [%s] row=%s, column=%s", e.getLocalizedMessage(), e.getCsvContext().getRowNumber(), e.getCsvContext().getColumnNumber());
        }
        catch (Exception e) {
            log.error("Failed to read CSV file", (Throwable)e);
            throw new IllegalArgumentException("Failed to read CSV file", e);
        }
        return new CsvReadResult<Object>(result, errorMessage);
    }

    private CsvPreference determineCsvPreference(InputStream inputStream) {
        try {
            String csvHeader = this.peekAtCsvHeader(inputStream);
            ImmutableMap commonDelimiters = ImmutableMap.of((Object)Character.valueOf(','), (Object)CsvPreference.STANDARD_PREFERENCE, (Object)Character.valueOf(';'), (Object)CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE, (Object)Character.valueOf('\t'), (Object)CsvPreference.TAB_PREFERENCE);
            return (CsvPreference)commonDelimiters.entrySet().stream().filter(entry -> {
                String delimiter = "" + entry.getKey();
                return csvHeader.contains(delimiter);
            }).findFirst().orElseThrow(() -> new RuntimeException("No matching delimiter found")).getValue();
        }
        catch (IOException | UnsupportedOperationException | NoSuchElementException e) {
            return CsvPreference.STANDARD_PREFERENCE;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String peekAtCsvHeader(InputStream inputStream) throws IOException {
        boolean bufferIsEmpty;
        int numberOfBytesRead;
        boolean canPeek = inputStream.markSupported();
        if (!canPeek) {
            throw new UnsupportedOperationException("Can't read CSV header since the InputStream doesn't support mark/reset operations.");
        }
        byte[] buffer = new byte[255];
        inputStream.mark(255);
        try {
            numberOfBytesRead = inputStream.read(buffer, 0, 255);
        }
        finally {
            inputStream.reset();
        }
        boolean bl = bufferIsEmpty = numberOfBytesRead == -1;
        if (bufferIsEmpty) {
            return "";
        }
        return new String(buffer).substring(0, numberOfBytesRead).trim();
    }

    private class NotBlank
    extends CellProcessorAdaptor {
        public NotBlank(CellProcessor next2) {
            super(next2);
        }

        public Object execute(Object value, CsvContext context) {
            if (value instanceof String && StringUtils.isBlank((CharSequence)((String)value))) {
                throw new SuperCsvConstraintViolationException("The Domain name should not be blank", context, (CellProcessor)this);
            }
            if (value instanceof String) {
                return this.next.execute((Object)((String)value).toLowerCase(), context);
            }
            throw new SuperCsvCellProcessorException(String.format("Could not parse '%s' as a domain name", value), context, (CellProcessor)this);
        }
    }
}

