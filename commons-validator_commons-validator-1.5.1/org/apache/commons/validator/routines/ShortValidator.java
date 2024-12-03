/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines;

import java.text.Format;
import java.util.Locale;
import org.apache.commons.validator.routines.AbstractNumberValidator;

public class ShortValidator
extends AbstractNumberValidator {
    private static final long serialVersionUID = -5227510699747787066L;
    private static final ShortValidator VALIDATOR = new ShortValidator();

    public static ShortValidator getInstance() {
        return VALIDATOR;
    }

    public ShortValidator() {
        this(true, 0);
    }

    public ShortValidator(boolean strict, int formatType) {
        super(strict, formatType, false);
    }

    public Short validate(String value) {
        return (Short)this.parse(value, null, null);
    }

    public Short validate(String value, String pattern) {
        return (Short)this.parse(value, pattern, null);
    }

    public Short validate(String value, Locale locale) {
        return (Short)this.parse(value, null, locale);
    }

    public Short validate(String value, String pattern, Locale locale) {
        return (Short)this.parse(value, pattern, locale);
    }

    public boolean isInRange(short value, short min, short max) {
        return value >= min && value <= max;
    }

    public boolean isInRange(Short value, short min, short max) {
        return this.isInRange((short)value, min, max);
    }

    public boolean minValue(short value, short min) {
        return value >= min;
    }

    public boolean minValue(Short value, short min) {
        return this.minValue((short)value, min);
    }

    public boolean maxValue(short value, short max) {
        return value <= max;
    }

    public boolean maxValue(Short value, short max) {
        return this.maxValue((short)value, max);
    }

    @Override
    protected Object processParsedValue(Object value, Format formatter) {
        long longValue = ((Number)value).longValue();
        if (longValue < -32768L || longValue > 32767L) {
            return null;
        }
        return (short)longValue;
    }
}

