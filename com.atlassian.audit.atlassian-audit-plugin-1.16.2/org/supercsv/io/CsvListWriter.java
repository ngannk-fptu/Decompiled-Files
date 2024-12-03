/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.io;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.AbstractCsvWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.Util;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CsvListWriter
extends AbstractCsvWriter
implements ICsvListWriter {
    private final List<Object> processedColumns = new ArrayList<Object>();

    public CsvListWriter(Writer writer, CsvPreference preference) {
        super(writer, preference);
    }

    @Override
    public void write(List<?> columns, CellProcessor[] processors) throws IOException {
        super.incrementRowAndLineNo();
        Util.executeCellProcessors(this.processedColumns, columns, processors, this.getLineNumber(), this.getRowNumber());
        super.writeRow(this.processedColumns);
    }

    @Override
    public void write(List<?> columns) throws IOException {
        super.incrementRowAndLineNo();
        super.writeRow(columns);
    }

    @Override
    public void write(Object ... columns) throws IOException {
        super.incrementRowAndLineNo();
        super.writeRow(columns);
    }

    @Override
    public void write(String ... columns) throws IOException {
        super.incrementRowAndLineNo();
        super.writeRow(columns);
    }
}

