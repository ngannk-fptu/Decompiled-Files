/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.cellprocessor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.BoolCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

public class ParseBool
extends CellProcessorAdaptor
implements StringCellProcessor {
    private static final String[] DEFAULT_TRUE_VALUES = new String[]{"1", "true", "t", "y"};
    private static final String[] DEFAULT_FALSE_VALUES = new String[]{"0", "false", "f", "n"};
    private final Set<String> trueValues = new HashSet<String>();
    private final Set<String> falseValues = new HashSet<String>();

    public ParseBool() {
        this(DEFAULT_TRUE_VALUES, DEFAULT_FALSE_VALUES);
    }

    public ParseBool(BoolCellProcessor next) {
        this(DEFAULT_TRUE_VALUES, DEFAULT_FALSE_VALUES, next);
    }

    public ParseBool(String trueValue, String falseValue) {
        ParseBool.checkPreconditions(trueValue, falseValue);
        this.trueValues.add(trueValue);
        this.falseValues.add(falseValue);
    }

    public ParseBool(String[] trueValues, String[] falseValues) {
        ParseBool.checkPreconditions(trueValues, falseValues);
        Collections.addAll(this.trueValues, trueValues);
        Collections.addAll(this.falseValues, falseValues);
    }

    public ParseBool(String trueValue, String falseValue, BoolCellProcessor next) {
        super(next);
        ParseBool.checkPreconditions(trueValue, falseValue);
        this.trueValues.add(trueValue);
        this.falseValues.add(falseValue);
    }

    public ParseBool(String[] trueValues, String[] falseValues, BoolCellProcessor next) {
        super(next);
        ParseBool.checkPreconditions(trueValues, falseValues);
        Collections.addAll(this.trueValues, trueValues);
        Collections.addAll(this.falseValues, falseValues);
    }

    private static void checkPreconditions(String trueValue, String falseValue) {
        if (trueValue == null) {
            throw new NullPointerException("trueValue should not be null");
        }
        if (falseValue == null) {
            throw new NullPointerException("falseValue should not be null");
        }
    }

    private static void checkPreconditions(String[] trueValues, String[] falseValues) {
        if (trueValues == null) {
            throw new NullPointerException("trueValues should not be null");
        }
        if (trueValues.length == 0) {
            throw new IllegalArgumentException("trueValues should not be empty");
        }
        if (falseValues == null) {
            throw new NullPointerException("falseValues should not be null");
        }
        if (falseValues.length == 0) {
            throw new IllegalArgumentException("falseValues should not be empty");
        }
    }

    public Object execute(Object value, CsvContext context) {
        Boolean result;
        this.validateInputNotNull(value, context);
        if (!(value instanceof String)) {
            throw new SuperCsvCellProcessorException(String.class, value, context, this);
        }
        String stringValue = ((String)value).toLowerCase();
        if (this.trueValues.contains(stringValue)) {
            result = Boolean.TRUE;
        } else if (this.falseValues.contains(stringValue)) {
            result = Boolean.FALSE;
        } else {
            throw new SuperCsvCellProcessorException(String.format("'%s' could not be parsed as a Boolean", value), context, this);
        }
        return this.next.execute(result, context);
    }
}

