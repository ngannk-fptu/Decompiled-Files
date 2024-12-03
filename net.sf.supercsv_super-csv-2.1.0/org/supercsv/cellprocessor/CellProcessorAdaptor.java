/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.cellprocessor;

import org.supercsv.cellprocessor.ift.BoolCellProcessor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

public abstract class CellProcessorAdaptor
implements CellProcessor {
    protected final CellProcessor next;

    protected CellProcessorAdaptor() {
        this.next = NullObjectPattern.INSTANCE;
    }

    protected CellProcessorAdaptor(CellProcessor next) {
        if (next == null) {
            throw new NullPointerException("next CellProcessor should not be null");
        }
        this.next = next;
    }

    protected final void validateInputNotNull(Object value, CsvContext context) {
        if (value == null) {
            throw new SuperCsvCellProcessorException("this processor does not accept null input - if the column is optional then chain an Optional() processor before this one", context, this);
        }
    }

    public String toString() {
        return this.getClass().getName();
    }

    private static final class NullObjectPattern
    implements BoolCellProcessor,
    DateCellProcessor,
    DoubleCellProcessor,
    LongCellProcessor,
    StringCellProcessor {
        private static final NullObjectPattern INSTANCE = new NullObjectPattern();

        private NullObjectPattern() {
        }

        public Object execute(Object value, CsvContext context) {
            return value;
        }
    }
}

