/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.cellprocessor;

import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

public class ParseBigDecimal
extends CellProcessorAdaptor
implements StringCellProcessor {
    private static final char DEFAULT_DECIMAL_SEPARATOR = '.';
    private final char decimalSeparator;

    public ParseBigDecimal() {
        this.decimalSeparator = (char)46;
    }

    public ParseBigDecimal(DecimalFormatSymbols symbols) {
        ParseBigDecimal.checkPreconditions(symbols);
        this.decimalSeparator = symbols.getDecimalSeparator();
    }

    public ParseBigDecimal(CellProcessor next) {
        super(next);
        this.decimalSeparator = (char)46;
    }

    public ParseBigDecimal(DecimalFormatSymbols symbols, CellProcessor next) {
        super(next);
        ParseBigDecimal.checkPreconditions(symbols);
        this.decimalSeparator = symbols.getDecimalSeparator();
    }

    private static void checkPreconditions(DecimalFormatSymbols symbols) {
        if (symbols == null) {
            throw new NullPointerException("symbols should not be null");
        }
    }

    public Object execute(Object value, CsvContext context) {
        BigDecimal result;
        this.validateInputNotNull(value, context);
        if (value instanceof String) {
            String s = (String)value;
            try {
                if (this.decimalSeparator == '.') {
                    result = new BigDecimal(s);
                }
                result = new BigDecimal(s.replace(this.decimalSeparator, '.'));
            }
            catch (NumberFormatException e) {
                throw new SuperCsvCellProcessorException(String.format("'%s' could not be parsed as a BigDecimal", value), context, this, e);
            }
        } else {
            throw new SuperCsvCellProcessorException(String.class, value, context, this);
        }
        return this.next.execute(result, context);
    }
}

