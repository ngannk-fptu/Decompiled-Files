/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines;

import java.text.Format;
import java.util.Locale;
import org.apache.commons.validator.routines.AbstractNumberValidator;

public class LongValidator
extends AbstractNumberValidator {
    private static final long serialVersionUID = -5117231731027866098L;
    private static final LongValidator VALIDATOR = new LongValidator();

    public static LongValidator getInstance() {
        return VALIDATOR;
    }

    public LongValidator() {
        this(true, 0);
    }

    public LongValidator(boolean strict, int formatType) {
        super(strict, formatType, false);
    }

    public Long validate(String value) {
        return (Long)this.parse(value, null, null);
    }

    public Long validate(String value, String pattern) {
        return (Long)this.parse(value, pattern, null);
    }

    public Long validate(String value, Locale locale) {
        return (Long)this.parse(value, null, locale);
    }

    public Long validate(String value, String pattern, Locale locale) {
        return (Long)this.parse(value, pattern, locale);
    }

    public boolean isInRange(long value, long min, long max) {
        return value >= min && value <= max;
    }

    public boolean isInRange(Long value, long min, long max) {
        return this.isInRange((long)value, min, max);
    }

    public boolean minValue(long value, long min) {
        return value >= min;
    }

    public boolean minValue(Long value, long min) {
        return this.minValue((long)value, min);
    }

    public boolean maxValue(long value, long max) {
        return value <= max;
    }

    public boolean maxValue(Long value, long max) {
        return this.maxValue((long)value, max);
    }

    @Override
    protected Object processParsedValue(Object value, Format formatter) {
        if (value instanceof Long) {
            return value;
        }
        return ((Number)value).longValue();
    }
}

