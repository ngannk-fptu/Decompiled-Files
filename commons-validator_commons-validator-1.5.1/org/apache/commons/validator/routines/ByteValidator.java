/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines;

import java.text.Format;
import java.util.Locale;
import org.apache.commons.validator.routines.AbstractNumberValidator;

public class ByteValidator
extends AbstractNumberValidator {
    private static final long serialVersionUID = 7001640945881854649L;
    private static final ByteValidator VALIDATOR = new ByteValidator();

    public static ByteValidator getInstance() {
        return VALIDATOR;
    }

    public ByteValidator() {
        this(true, 0);
    }

    public ByteValidator(boolean strict, int formatType) {
        super(strict, formatType, false);
    }

    public Byte validate(String value) {
        return (Byte)this.parse(value, null, null);
    }

    public Byte validate(String value, String pattern) {
        return (Byte)this.parse(value, pattern, null);
    }

    public Byte validate(String value, Locale locale) {
        return (Byte)this.parse(value, null, locale);
    }

    public Byte validate(String value, String pattern, Locale locale) {
        return (Byte)this.parse(value, pattern, locale);
    }

    public boolean isInRange(byte value, byte min, byte max) {
        return value >= min && value <= max;
    }

    public boolean isInRange(Byte value, byte min, byte max) {
        return this.isInRange((byte)value, min, max);
    }

    public boolean minValue(byte value, byte min) {
        return value >= min;
    }

    public boolean minValue(Byte value, byte min) {
        return this.minValue((byte)value, min);
    }

    public boolean maxValue(byte value, byte max) {
        return value <= max;
    }

    public boolean maxValue(Byte value, byte max) {
        return this.maxValue((byte)value, max);
    }

    @Override
    protected Object processParsedValue(Object value, Format formatter) {
        long longValue = ((Number)value).longValue();
        if (longValue < -128L || longValue > 127L) {
            return null;
        }
        return (byte)longValue;
    }
}

