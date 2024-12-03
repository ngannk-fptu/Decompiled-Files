/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines;

import java.text.Format;
import java.util.Locale;
import org.apache.commons.validator.routines.AbstractNumberValidator;

public class DoubleValidator
extends AbstractNumberValidator {
    private static final long serialVersionUID = 5867946581318211330L;
    private static final DoubleValidator VALIDATOR = new DoubleValidator();

    public static DoubleValidator getInstance() {
        return VALIDATOR;
    }

    public DoubleValidator() {
        this(true, 0);
    }

    public DoubleValidator(boolean strict, int formatType) {
        super(strict, formatType, true);
    }

    public Double validate(String value) {
        return (Double)this.parse(value, null, null);
    }

    public Double validate(String value, String pattern) {
        return (Double)this.parse(value, pattern, null);
    }

    public Double validate(String value, Locale locale) {
        return (Double)this.parse(value, null, locale);
    }

    public Double validate(String value, String pattern, Locale locale) {
        return (Double)this.parse(value, pattern, locale);
    }

    public boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    public boolean isInRange(Double value, double min, double max) {
        return this.isInRange((double)value, min, max);
    }

    public boolean minValue(double value, double min) {
        return value >= min;
    }

    public boolean minValue(Double value, double min) {
        return this.minValue((double)value, min);
    }

    public boolean maxValue(double value, double max) {
        return value <= max;
    }

    public boolean maxValue(Double value, double max) {
        return this.maxValue((double)value, max);
    }

    @Override
    protected Object processParsedValue(Object value, Format formatter) {
        if (value instanceof Double) {
            return value;
        }
        return new Double(((Number)value).doubleValue());
    }
}

