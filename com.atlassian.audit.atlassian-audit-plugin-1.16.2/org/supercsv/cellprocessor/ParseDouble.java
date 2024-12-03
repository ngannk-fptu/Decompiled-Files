/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.cellprocessor;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

public class ParseDouble
extends CellProcessorAdaptor
implements StringCellProcessor {
    public ParseDouble() {
    }

    public ParseDouble(DoubleCellProcessor next) {
        super(next);
    }

    public Object execute(Object value, CsvContext context) {
        Double result;
        this.validateInputNotNull(value, context);
        if (value instanceof Double) {
            result = (Double)value;
        } else if (value instanceof String) {
            try {
                result = new Double((String)value);
            }
            catch (NumberFormatException e) {
                throw new SuperCsvCellProcessorException(String.format("'%s' could not be parsed as a Double", value), context, this, e);
            }
        } else {
            String actualClassName = value.getClass().getName();
            throw new SuperCsvCellProcessorException(String.format("the input value should be of type Double or String but is of type %s", actualClassName), context, this);
        }
        return this.next.execute(result, context);
    }
}

