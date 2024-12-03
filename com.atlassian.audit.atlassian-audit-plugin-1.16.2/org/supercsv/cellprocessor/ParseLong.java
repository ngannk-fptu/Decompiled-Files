/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.cellprocessor;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

public class ParseLong
extends CellProcessorAdaptor
implements StringCellProcessor {
    public ParseLong() {
    }

    public ParseLong(LongCellProcessor next) {
        super(next);
    }

    public Object execute(Object value, CsvContext context) {
        Long result;
        this.validateInputNotNull(value, context);
        if (value instanceof Long) {
            result = (Long)value;
        } else if (value instanceof String) {
            try {
                result = Long.parseLong((String)value);
            }
            catch (NumberFormatException e) {
                throw new SuperCsvCellProcessorException(String.format("'%s' could not be parsed as an Long", value), context, this, e);
            }
        } else {
            String actualClassName = value.getClass().getName();
            throw new SuperCsvCellProcessorException(String.format("the input value should be of type Long or String but is of type %s", actualClassName), context, this);
        }
        return this.next.execute(result, context);
    }
}

