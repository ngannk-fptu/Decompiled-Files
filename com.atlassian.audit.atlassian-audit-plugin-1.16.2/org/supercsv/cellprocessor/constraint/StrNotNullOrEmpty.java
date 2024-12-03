/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.cellprocessor.constraint;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.util.CsvContext;

public class StrNotNullOrEmpty
extends CellProcessorAdaptor
implements StringCellProcessor {
    public StrNotNullOrEmpty() {
    }

    public StrNotNullOrEmpty(CellProcessor next) {
        super(next);
    }

    public Object execute(Object value, CsvContext context) {
        if (value == null) {
            throw new SuperCsvConstraintViolationException("the String should not be null", context, this);
        }
        if (value instanceof String) {
            String stringValue = (String)value;
            if (stringValue.length() == 0) {
                throw new SuperCsvConstraintViolationException("the String should not be empty", context, this);
            }
        } else {
            throw new SuperCsvCellProcessorException(String.class, value, context, this);
        }
        return this.next.execute(value, context);
    }
}

