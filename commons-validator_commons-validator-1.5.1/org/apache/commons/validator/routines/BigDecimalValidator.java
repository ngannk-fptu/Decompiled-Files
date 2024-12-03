/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines;

import java.math.BigDecimal;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Locale;
import org.apache.commons.validator.routines.AbstractNumberValidator;

public class BigDecimalValidator
extends AbstractNumberValidator {
    private static final long serialVersionUID = -670320911490506772L;
    private static final BigDecimalValidator VALIDATOR = new BigDecimalValidator();

    public static BigDecimalValidator getInstance() {
        return VALIDATOR;
    }

    public BigDecimalValidator() {
        this(true);
    }

    public BigDecimalValidator(boolean strict) {
        this(strict, 0, true);
    }

    protected BigDecimalValidator(boolean strict, int formatType, boolean allowFractions) {
        super(strict, formatType, allowFractions);
    }

    public BigDecimal validate(String value) {
        return (BigDecimal)this.parse(value, null, null);
    }

    public BigDecimal validate(String value, String pattern) {
        return (BigDecimal)this.parse(value, pattern, null);
    }

    public BigDecimal validate(String value, Locale locale) {
        return (BigDecimal)this.parse(value, null, locale);
    }

    public BigDecimal validate(String value, String pattern, Locale locale) {
        return (BigDecimal)this.parse(value, pattern, locale);
    }

    public boolean isInRange(BigDecimal value, double min, double max) {
        return value.doubleValue() >= min && value.doubleValue() <= max;
    }

    public boolean minValue(BigDecimal value, double min) {
        return value.doubleValue() >= min;
    }

    public boolean maxValue(BigDecimal value, double max) {
        return value.doubleValue() <= max;
    }

    @Override
    protected Object processParsedValue(Object value, Format formatter) {
        BigDecimal decimal = null;
        decimal = value instanceof Long ? BigDecimal.valueOf((Long)value) : new BigDecimal(value.toString());
        int scale = this.determineScale((NumberFormat)formatter);
        if (scale >= 0) {
            decimal = decimal.setScale(scale, 1);
        }
        return decimal;
    }
}

