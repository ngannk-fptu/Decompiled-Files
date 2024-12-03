/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.cellprocessor.constraint;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.BoolCellProcessor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.util.CsvContext;

public class Equals
extends CellProcessorAdaptor
implements BoolCellProcessor,
DateCellProcessor,
DoubleCellProcessor,
LongCellProcessor,
StringCellProcessor {
    private static final Object UNKNOWN = new Object();
    private Object constantValue;
    private boolean constantSupplied;

    public Equals() {
        this.constantValue = UNKNOWN;
        this.constantSupplied = false;
    }

    public Equals(Object constantValue) {
        this.constantValue = constantValue;
        this.constantSupplied = true;
    }

    public Equals(CellProcessor next) {
        super(next);
        this.constantValue = UNKNOWN;
        this.constantSupplied = false;
    }

    public Equals(Object constantValue, CellProcessor next) {
        super(next);
        this.constantValue = constantValue;
        this.constantSupplied = true;
    }

    public Object execute(Object value, CsvContext context) {
        if (UNKNOWN.equals(this.constantValue)) {
            this.constantValue = value;
        } else if (!Equals.equals(this.constantValue, value)) {
            if (this.constantSupplied) {
                throw new SuperCsvConstraintViolationException(String.format("'%s' is not equal to the supplied constant '%s'", value, this.constantValue), context, this);
            }
            throw new SuperCsvConstraintViolationException(String.format("'%s' is not equal to the previous value(s) of '%s'", value, this.constantValue), context, this);
        }
        return this.next.execute(value, context);
    }

    private static boolean equals(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }
}

