/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.io;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.AbstractCsvReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.io.ITokenizer;
import org.supercsv.prefs.CsvPreference;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CsvListReader
extends AbstractCsvReader
implements ICsvListReader {
    public CsvListReader(Reader reader, CsvPreference preferences) {
        super(reader, preferences);
    }

    public CsvListReader(ITokenizer tokenizer, CsvPreference preferences) {
        super(tokenizer, preferences);
    }

    @Override
    public List<String> read() throws IOException {
        if (this.readRow()) {
            return new ArrayList<String>(this.getColumns());
        }
        return null;
    }

    @Override
    public List<Object> read(CellProcessor ... processors) throws IOException {
        if (processors == null) {
            throw new NullPointerException("processors should not be null");
        }
        if (this.readRow()) {
            return this.executeProcessors(processors);
        }
        return null;
    }

    @Override
    public List<Object> executeProcessors(CellProcessor ... processors) {
        return super.executeProcessors(new ArrayList<Object>(this.getColumns().size()), processors);
    }
}

