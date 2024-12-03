/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;
import org.apache.commons.validator.routines.BigDecimalValidator;

public class PercentValidator
extends BigDecimalValidator {
    private static final long serialVersionUID = -3508241924961535772L;
    private static final PercentValidator VALIDATOR = new PercentValidator();
    private static final char PERCENT_SYMBOL = '%';
    private static final BigDecimal POINT_ZERO_ONE = new BigDecimal("0.01");

    public static BigDecimalValidator getInstance() {
        return VALIDATOR;
    }

    public PercentValidator() {
        this(true);
    }

    public PercentValidator(boolean strict) {
        super(strict, 2, true);
    }

    @Override
    protected Object parse(String value, Format formatter) {
        BigDecimal parsedValue = (BigDecimal)super.parse(value, formatter);
        if (parsedValue != null || !(formatter instanceof DecimalFormat)) {
            return parsedValue;
        }
        DecimalFormat decimalFormat = (DecimalFormat)formatter;
        String pattern = decimalFormat.toPattern();
        if (pattern.indexOf(37) >= 0) {
            StringBuilder buffer = new StringBuilder(pattern.length());
            for (int i = 0; i < pattern.length(); ++i) {
                if (pattern.charAt(i) == '%') continue;
                buffer.append(pattern.charAt(i));
            }
            decimalFormat.applyPattern(buffer.toString());
            parsedValue = (BigDecimal)super.parse(value, decimalFormat);
            if (parsedValue != null) {
                parsedValue = parsedValue.multiply(POINT_ZERO_ONE);
            }
        }
        return parsedValue;
    }
}

