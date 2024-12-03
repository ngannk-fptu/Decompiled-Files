/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import org.supercsv.encoder.CsvEncoder;
import org.supercsv.io.ICsvWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.CsvContext;
import org.supercsv.util.Util;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AbstractCsvWriter
implements ICsvWriter {
    private final BufferedWriter writer;
    private final CsvPreference preference;
    private final CsvEncoder encoder;
    private int lineNumber = 0;
    private int rowNumber = 0;
    private int columnNumber = 0;

    public AbstractCsvWriter(Writer writer, CsvPreference preference) {
        if (writer == null) {
            throw new NullPointerException("writer should not be null");
        }
        if (preference == null) {
            throw new NullPointerException("preference should not be null");
        }
        this.writer = new BufferedWriter(writer);
        this.preference = preference;
        this.encoder = preference.getEncoder();
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
    }

    @Override
    public void flush() throws IOException {
        this.writer.flush();
    }

    protected String escapeString(String csvElement) {
        CsvContext context = new CsvContext(this.lineNumber, this.rowNumber, this.columnNumber);
        String escapedCsv = this.encoder.encode(csvElement, context, this.preference);
        this.lineNumber = context.getLineNumber();
        return escapedCsv;
    }

    protected void incrementRowAndLineNo() {
        ++this.lineNumber;
        ++this.rowNumber;
    }

    @Override
    public int getLineNumber() {
        return this.lineNumber;
    }

    @Override
    public int getRowNumber() {
        return this.rowNumber;
    }

    protected void writeRow(List<?> columns) throws IOException {
        this.writeRow(Util.objectListToStringArray(columns));
    }

    protected void writeRow(Object ... columns) throws IOException {
        this.writeRow(Util.objectArrayToStringArray(columns));
    }

    protected void writeRow(String ... columns) throws IOException {
        if (columns == null) {
            throw new NullPointerException(String.format("columns to write should not be null on line %d", this.lineNumber));
        }
        if (columns.length == 0) {
            throw new IllegalArgumentException(String.format("columns to write should not be empty on line %d", this.lineNumber));
        }
        for (int i = 0; i < columns.length; ++i) {
            String csvElement;
            this.columnNumber = i + 1;
            if (i > 0) {
                this.writer.write(this.preference.getDelimiterChar());
            }
            if ((csvElement = columns[i]) == null) continue;
            this.writer.write(this.escapeString(csvElement));
        }
        this.writer.write(this.preference.getEndOfLineSymbols());
    }

    @Override
    public void writeComment(String comment) throws IOException {
        ++this.lineNumber;
        if (comment == null) {
            throw new NullPointerException(String.format("comment to write should not be null on line %d", this.lineNumber));
        }
        this.writer.write(comment);
        this.writer.write(this.preference.getEndOfLineSymbols());
    }

    @Override
    public void writeHeader(String ... header) throws IOException {
        this.incrementRowAndLineNo();
        this.writeRow(header);
    }
}

