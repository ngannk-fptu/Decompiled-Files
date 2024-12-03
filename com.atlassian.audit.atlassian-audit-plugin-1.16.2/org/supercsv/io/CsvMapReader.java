/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.io;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.AbstractCsvReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.io.ITokenizer;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.Util;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CsvMapReader
extends AbstractCsvReader
implements ICsvMapReader {
    public CsvMapReader(Reader reader, CsvPreference preferences) {
        super(reader, preferences);
    }

    public CsvMapReader(ITokenizer tokenizer, CsvPreference preferences) {
        super(tokenizer, preferences);
    }

    @Override
    public Map<String, String> read(String ... nameMapping) throws IOException {
        if (nameMapping == null) {
            throw new NullPointerException("nameMapping should not be null");
        }
        if (this.readRow()) {
            HashMap<String, String> destination = new HashMap<String, String>();
            Util.filterListToMap(destination, nameMapping, this.getColumns());
            return destination;
        }
        return null;
    }

    @Override
    public Map<String, Object> read(String[] nameMapping, CellProcessor[] processors) throws IOException {
        if (nameMapping == null) {
            throw new NullPointerException("nameMapping should not be null");
        }
        if (processors == null) {
            throw new NullPointerException("processors should not be null");
        }
        if (this.readRow()) {
            List<Object> processedColumns = this.executeProcessors(new ArrayList<Object>(this.getColumns().size()), processors);
            HashMap<String, Object> destination = new HashMap<String, Object>(processedColumns.size());
            Util.filterListToMap(destination, nameMapping, processedColumns);
            return destination;
        }
        return null;
    }
}

