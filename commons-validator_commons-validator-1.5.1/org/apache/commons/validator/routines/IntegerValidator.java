/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines;

import java.text.Format;
import java.util.Locale;
import org.apache.commons.validator.routines.AbstractNumberValidator;

public class IntegerValidator
extends AbstractNumberValidator {
    private static final long serialVersionUID = 422081746310306596L;
    private static final IntegerValidator VALIDATOR = new IntegerValidator();

    public static IntegerValidator getInstance() {
        return VALIDATOR;
    }

    public IntegerValidator() {
        this(true, 0);
    }

    public IntegerValidator(boolean strict, int formatType) {
        super(strict, formatType, false);
    }

    public Integer validate(String value) {
        return (Integer)this.parse(value, null, null);
    }

    public Integer validate(String value, String pattern) {
        return (Integer)this.parse(value, pattern, null);
    }

    public Integer validate(String value, Locale locale) {
        return (Integer)this.parse(value, null, locale);
    }

    public Integer validate(String value, String pattern, Locale locale) {
        return (Integer)this.parse(value, pattern, locale);
    }

    public boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    public boolean isInRange(Integer value, int min, int max) {
        return this.isInRange((int)value, min, max);
    }

    public boolean minValue(int value, int min) {
        return value >= min;
    }

    public boolean minValue(Integer value, int min) {
        return this.minValue((int)value, min);
    }

    public boolean maxValue(int value, int max) {
        return value <= max;
    }

    public boolean maxValue(Integer value, int max) {
        return this.maxValue((int)value, max);
    }

    @Override
    protected Object processParsedValue(Object value, Format formatter) {
        long longValue = ((Number)value).longValue();
        if (longValue < Integer.MIN_VALUE || longValue > Integer.MAX_VALUE) {
            return null;
        }
        return new Integer((int)longValue);
    }
}

