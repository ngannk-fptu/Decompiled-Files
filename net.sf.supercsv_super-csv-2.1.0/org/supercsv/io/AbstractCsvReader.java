/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.io;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.io.ICsvReader;
import org.supercsv.io.ITokenizer;
import org.supercsv.io.Tokenizer;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.Util;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AbstractCsvReader
implements ICsvReader {
    private final ITokenizer tokenizer;
    private final CsvPreference preferences;
    private final List<String> columns = new ArrayList<String>();
    private int rowNumber = 0;

    public AbstractCsvReader(Reader reader, CsvPreference preferences) {
        if (reader == null) {
            throw new NullPointerException("reader should not be null");
        }
        if (preferences == null) {
            throw new NullPointerException("preferences should not be null");
        }
        this.preferences = preferences;
        this.tokenizer = new Tokenizer(reader, preferences);
    }

    public AbstractCsvReader(ITokenizer tokenizer, CsvPreference preferences) {
        if (tokenizer == null) {
            throw new NullPointerException("tokenizer should not be null");
        }
        if (preferences == null) {
            throw new NullPointerException("preferences should not be null");
        }
        this.preferences = preferences;
        this.tokenizer = tokenizer;
    }

    @Override
    public void close() throws IOException {
        this.tokenizer.close();
    }

    @Override
    public String get(int n) {
        return this.columns.get(n - 1);
    }

    @Override
    public String[] getHeader(boolean firstLineCheck) throws IOException {
        if (firstLineCheck && this.tokenizer.getLineNumber() != 0) {
            throw new SuperCsvException(String.format("CSV header must be fetched as the first read operation, but %d lines have already been read", this.tokenizer.getLineNumber()));
        }
        if (this.readRow()) {
            return this.columns.toArray(new String[this.columns.size()]);
        }
        return null;
    }

    @Override
    public int getLineNumber() {
        return this.tokenizer.getLineNumber();
    }

    @Override
    public String getUntokenizedRow() {
        return this.tokenizer.getUntokenizedRow();
    }

    @Override
    public int getRowNumber() {
        return this.rowNumber;
    }

    @Override
    public int length() {
        return this.columns.size();
    }

    protected List<String> getColumns() {
        return this.columns;
    }

    protected CsvPreference getPreferences() {
        return this.preferences;
    }

    protected boolean readRow() throws IOException {
        if (this.tokenizer.readColumns(this.columns)) {
            ++this.rowNumber;
            return true;
        }
        return false;
    }

    protected List<Object> executeProcessors(List<Object> processedColumns, CellProcessor[] processors) {
        Util.executeCellProcessors(processedColumns, this.getColumns(), processors, this.getLineNumber(), this.getRowNumber());
        return processedColumns;
    }
}

