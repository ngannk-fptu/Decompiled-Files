/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.cellprocessor;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.BoolCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

public class FmtBool
extends CellProcessorAdaptor
implements BoolCellProcessor {
    private final String trueValue;
    private final String falseValue;

    public FmtBool(String trueValue, String falseValue) {
        this.trueValue = trueValue;
        this.falseValue = falseValue;
    }

    public FmtBool(String trueValue, String falseValue, StringCellProcessor next) {
        super(next);
        this.trueValue = trueValue;
        this.falseValue = falseValue;
    }

    public Object execute(Object value, CsvContext context) {
        this.validateInputNotNull(value, context);
        if (!(value instanceof Boolean)) {
            throw new SuperCsvCellProcessorException(Boolean.class, value, context, this);
        }
        String result = (Boolean)value != false ? this.trueValue : this.falseValue;
        return this.next.execute(result, context);
    }
}

