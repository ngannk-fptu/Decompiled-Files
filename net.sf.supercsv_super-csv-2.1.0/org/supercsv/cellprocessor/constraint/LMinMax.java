/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.cellprocessor.constraint;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.util.CsvContext;

public class LMinMax
extends CellProcessorAdaptor {
    public static final long MAX_LONG = Long.MAX_VALUE;
    public static final long MIN_LONG = Long.MIN_VALUE;
    public static final int MAX_INTEGER = Integer.MAX_VALUE;
    public static final int MIN_INTEGER = Integer.MIN_VALUE;
    public static final short MAX_SHORT = Short.MAX_VALUE;
    public static final short MIN_SHORT = Short.MIN_VALUE;
    public static final int MAX_CHAR = 65535;
    public static final int MIN_CHAR = 0;
    public static final int MAX_8_BIT_UNSIGNED = 255;
    public static final int MIN_8_BIT_UNSIGNED = 0;
    public static final int MAX_8_BIT_SIGNED = 127;
    public static final int MIN_8_BIT_SIGNED = -128;
    private final long min;
    private final long max;

    public LMinMax(long min, long max) {
        LMinMax.checkPreconditions(min, max);
        this.min = min;
        this.max = max;
    }

    public LMinMax(long min, long max, LongCellProcessor next) {
        super(next);
        LMinMax.checkPreconditions(min, max);
        this.min = min;
        this.max = max;
    }

    private static void checkPreconditions(long min, long max) {
        if (max < min) {
            throw new IllegalArgumentException(String.format("max (%d) should not be < min (%d)", max, min));
        }
    }

    public Object execute(Object value, CsvContext context) {
        Long result;
        this.validateInputNotNull(value, context);
        if (value instanceof Long) {
            result = (Long)value;
        } else {
            try {
                result = Long.parseLong(value.toString());
            }
            catch (NumberFormatException e) {
                throw new SuperCsvCellProcessorException(String.format("'%s' could not be parsed as a Long", value), context, this, e);
            }
        }
        if (result < this.min || result > this.max) {
            throw new SuperCsvConstraintViolationException(String.format("%d does not lie between the min (%d) and max (%d) values (inclusive)", result, this.min, this.max), context, this);
        }
        return this.next.execute(result, context);
    }
}

