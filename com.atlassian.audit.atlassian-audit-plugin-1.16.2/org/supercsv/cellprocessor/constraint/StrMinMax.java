/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.cellprocessor.constraint;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.util.CsvContext;

public class StrMinMax
extends CellProcessorAdaptor
implements StringCellProcessor {
    private final long min;
    private final long max;

    public StrMinMax(long min, long max) {
        StrMinMax.checkPreconditions(min, max);
        this.min = min;
        this.max = max;
    }

    public StrMinMax(long min, long max, CellProcessor next) {
        super(next);
        StrMinMax.checkPreconditions(min, max);
        this.min = min;
        this.max = max;
    }

    private static void checkPreconditions(long min, long max) {
        if (max < min) {
            throw new IllegalArgumentException(String.format("max (%d) should not be < min (%d)", max, min));
        }
        if (min < 0L) {
            throw new IllegalArgumentException(String.format("min length (%d) should not be < 0", min));
        }
    }

    public Object execute(Object value, CsvContext context) {
        this.validateInputNotNull(value, context);
        String stringValue = value.toString();
        int length = stringValue.length();
        if ((long)length < this.min || (long)length > this.max) {
            throw new SuperCsvConstraintViolationException(String.format("the length (%d) of value '%s' does not lie between the min (%d) and max (%d) values (inclusive)", length, stringValue, this.min, this.max), context, this);
        }
        return this.next.execute(stringValue, context);
    }
}

