/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.io;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.AbstractCsvWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.Util;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CsvMapWriter
extends AbstractCsvWriter
implements ICsvMapWriter {
    private final List<Object> processedColumns = new ArrayList<Object>();

    public CsvMapWriter(Writer writer, CsvPreference preference) {
        super(writer, preference);
    }

    @Override
    public void write(Map<String, ?> values, String ... nameMapping) throws IOException {
        super.incrementRowAndLineNo();
        super.writeRow(Util.filterMapToObjectArray(values, nameMapping));
    }

    @Override
    public void write(Map<String, ?> values, String[] nameMapping, CellProcessor[] processors) throws IOException {
        super.incrementRowAndLineNo();
        Util.executeCellProcessors(this.processedColumns, Util.filterMapToList(values, nameMapping), processors, this.getLineNumber(), this.getRowNumber());
        super.writeRow(this.processedColumns);
    }
}

