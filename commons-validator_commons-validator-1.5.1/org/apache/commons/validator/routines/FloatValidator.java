/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines;

import java.text.Format;
import java.util.Locale;
import org.apache.commons.validator.routines.AbstractNumberValidator;

public class FloatValidator
extends AbstractNumberValidator {
    private static final long serialVersionUID = -4513245432806414267L;
    private static final FloatValidator VALIDATOR = new FloatValidator();

    public static FloatValidator getInstance() {
        return VALIDATOR;
    }

    public FloatValidator() {
        this(true, 0);
    }

    public FloatValidator(boolean strict, int formatType) {
        super(strict, formatType, true);
    }

    public Float validate(String value) {
        return (Float)this.parse(value, null, null);
    }

    public Float validate(String value, String pattern) {
        return (Float)this.parse(value, pattern, null);
    }

    public Float validate(String value, Locale locale) {
        return (Float)this.parse(value, null, locale);
    }

    public Float validate(String value, String pattern, Locale locale) {
        return (Float)this.parse(value, pattern, locale);
    }

    public boolean isInRange(float value, float min, float max) {
        return value >= min && value <= max;
    }

    public boolean isInRange(Float value, float min, float max) {
        return this.isInRange(value.floatValue(), min, max);
    }

    public boolean minValue(float value, float min) {
        return value >= min;
    }

    public boolean minValue(Float value, float min) {
        return this.minValue(value.floatValue(), min);
    }

    public boolean maxValue(float value, float max) {
        return value <= max;
    }

    public boolean maxValue(Float value, float max) {
        return this.maxValue(value.floatValue(), max);
    }

    @Override
    protected Object processParsedValue(Object value, Format formatter) {
        double doubleValue = ((Number)value).doubleValue();
        if (doubleValue > 0.0) {
            if (doubleValue < (double)1.4E-45f) {
                return null;
            }
            if (doubleValue > 3.4028234663852886E38) {
                return null;
            }
        } else if (doubleValue < 0.0) {
            double posDouble = doubleValue * -1.0;
            if (posDouble < (double)1.4E-45f) {
                return null;
            }
            if (posDouble > 3.4028234663852886E38) {
                return null;
            }
        }
        return new Float((float)doubleValue);
    }
}

