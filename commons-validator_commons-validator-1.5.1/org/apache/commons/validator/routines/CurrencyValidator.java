/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines;

import java.text.DecimalFormat;
import java.text.Format;
import org.apache.commons.validator.routines.BigDecimalValidator;

public class CurrencyValidator
extends BigDecimalValidator {
    private static final long serialVersionUID = -4201640771171486514L;
    private static final CurrencyValidator VALIDATOR = new CurrencyValidator();
    private static final char CURRENCY_SYMBOL = '\u00a4';

    public static BigDecimalValidator getInstance() {
        return VALIDATOR;
    }

    public CurrencyValidator() {
        this(true, true);
    }

    public CurrencyValidator(boolean strict, boolean allowFractions) {
        super(strict, 1, allowFractions);
    }

    @Override
    protected Object parse(String value, Format formatter) {
        Object parsedValue = super.parse(value, formatter);
        if (parsedValue != null || !(formatter instanceof DecimalFormat)) {
            return parsedValue;
        }
        DecimalFormat decimalFormat = (DecimalFormat)formatter;
        String pattern = decimalFormat.toPattern();
        if (pattern.indexOf(164) >= 0) {
            StringBuilder buffer = new StringBuilder(pattern.length());
            for (int i = 0; i < pattern.length(); ++i) {
                if (pattern.charAt(i) == '\u00a4') continue;
                buffer.append(pattern.charAt(i));
            }
            decimalFormat.applyPattern(buffer.toString());
            parsedValue = super.parse(value, decimalFormat);
        }
        return parsedValue;
    }
}

