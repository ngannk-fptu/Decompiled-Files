/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.jdbc.support.JdbcUtils
 *  org.supercsv.io.CsvListWriter
 *  org.supercsv.prefs.CsvPreference
 *  org.supercsv.prefs.CsvPreference$Builder
 */
package com.atlassian.migration.agent.newexport.processor;

import com.atlassian.migration.agent.newexport.Query;
import com.atlassian.migration.agent.newexport.exception.CsvSerializeProcessorException;
import com.atlassian.migration.agent.newexport.processor.RowProcessor;
import java.io.IOException;
import java.io.Writer;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.JdbcUtils;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

public class CsvSerializingProcessor
implements RowProcessor {
    private static final Logger log = LoggerFactory.getLogger(CsvSerializingProcessor.class);
    private static final String NULL_VALUE = "\u0000";
    public static final CsvPreference DEFAULT_PREFERENCE = new CsvPreference.Builder(CsvPreference.EXCEL_PREFERENCE).useQuoteMode((csvColumn, csvContext, csvPreference) -> Objects.nonNull(csvColumn)).build();
    private static final DateTimeFormatter timestampFormatter = new DateTimeFormatterBuilder().append(DateTimeFormatter.ISO_LOCAL_DATE).appendLiteral(' ').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral(':').appendValue(ChronoField.MINUTE_OF_HOUR, 2).optionalStart().appendLiteral(':').appendValue(ChronoField.SECOND_OF_MINUTE, 2).optionalStart().appendFraction(ChronoField.MILLI_OF_SECOND, 3, 3, true).toFormatter();
    private final CsvListWriter csvWriter;
    private final Supplier<Instant> instantSupplier;
    private long rowCount = 0L;
    private long totalContentCharacters = 0L;
    private long timeOfFirstRecord = -1L;

    public CsvSerializingProcessor(Writer writer, Supplier<Instant> instantSupplier) {
        this.csvWriter = new CsvListWriter(writer, DEFAULT_PREFERENCE);
        this.instantSupplier = instantSupplier;
    }

    @Override
    public void initialise(ResultSet rs, Query query) {
        try {
            this.createHeaders(rs, query);
            this.timeOfFirstRecord = this.instantSupplier.get().toEpochMilli();
        }
        catch (IOException | SQLException e) {
            throw new CsvSerializeProcessorException("Failed to initialise CSV Writer: ", e);
        }
    }

    @Override
    public void process(ResultSet rs) {
        ++this.rowCount;
        try {
            int columns = rs.getMetaData().getColumnCount();
            String[] row = new String[columns];
            for (int i = 0; i < columns; ++i) {
                String columnValue = this.asString(rs, i + 1);
                if (columnValue != null && columnValue.contains(NULL_VALUE)) {
                    columnValue = columnValue.replace(NULL_VALUE, "");
                }
                row[i] = columnValue;
                this.totalContentCharacters += columnValue == null ? 1L : (long)columnValue.length();
            }
            this.csvWriter.write(row);
            this.csvWriter.flush();
        }
        catch (IOException | SQLException e) {
            throw new CsvSerializeProcessorException("Error while serializing data: ", e);
        }
    }

    private void createHeaders(ResultSet rs, Query query) throws SQLException, IOException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        String[] columnNames = new String[columnCount];
        for (int i = 0; i < columnCount; ++i) {
            String columnName = JdbcUtils.lookupColumnName((ResultSetMetaData)rsmd, (int)(i + 1));
            columnNames[i] = query.preserveIdentifierCase ? columnName : columnName.toLowerCase();
        }
        this.csvWriter.write(columnNames);
        this.csvWriter.flush();
    }

    public long getRowCount() {
        return this.rowCount;
    }

    public long getTotalContentCharacters() {
        return this.totalContentCharacters;
    }

    public long getTimeOfFirstRecord() {
        return this.timeOfFirstRecord;
    }

    private String asString(ResultSet rs, int i) throws SQLException {
        if (rs.getMetaData().getColumnType(i) == 2005) {
            Clob clob = rs.getClob(i);
            return clob != null ? rs.getClob(i).getSubString(1L, (int)clob.length()) : null;
        }
        if (rs.getMetaData().getColumnType(i) == 93) {
            Timestamp timestamp = rs.getTimestamp(i);
            return timestamp != null ? timestampFormatter.format(rs.getTimestamp(i).toLocalDateTime()) : null;
        }
        Object value = rs.getObject(i);
        return value != null ? value.toString() : null;
    }

    public void addRowCount() {
        ++this.rowCount;
    }

    public void addContentCharacters(List<String> columnValues) {
        this.totalContentCharacters += columnValues.stream().filter(Objects::nonNull).mapToLong(String::length).sum();
    }

    public CsvListWriter getWriter() {
        return this.csvWriter;
    }
}

