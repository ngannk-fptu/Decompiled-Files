/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines;

import java.math.BigInteger;
import java.text.Format;
import java.util.Locale;
import org.apache.commons.validator.routines.AbstractNumberValidator;

public class BigIntegerValidator
extends AbstractNumberValidator {
    private static final long serialVersionUID = 6713144356347139988L;
    private static final BigIntegerValidator VALIDATOR = new BigIntegerValidator();

    public static BigIntegerValidator getInstance() {
        return VALIDATOR;
    }

    public BigIntegerValidator() {
        this(true, 0);
    }

    public BigIntegerValidator(boolean strict, int formatType) {
        super(strict, formatType, false);
    }

    public BigInteger validate(String value) {
        return (BigInteger)this.parse(value, null, null);
    }

    public BigInteger validate(String value, String pattern) {
        return (BigInteger)this.parse(value, pattern, null);
    }

    public BigInteger validate(String value, Locale locale) {
        return (BigInteger)this.parse(value, null, locale);
    }

    public BigInteger validate(String value, String pattern, Locale locale) {
        return (BigInteger)this.parse(value, pattern, locale);
    }

    public boolean isInRange(BigInteger value, long min, long max) {
        return value.longValue() >= min && value.longValue() <= max;
    }

    public boolean minValue(BigInteger value, long min) {
        return value.longValue() >= min;
    }

    public boolean maxValue(BigInteger value, long max) {
        return value.longValue() <= max;
    }

    @Override
    protected Object processParsedValue(Object value, Format formatter) {
        return BigInteger.valueOf(((Number)value).longValue());
    }
}

