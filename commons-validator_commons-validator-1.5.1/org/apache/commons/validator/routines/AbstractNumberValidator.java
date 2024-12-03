/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Locale;
import org.apache.commons.validator.routines.AbstractFormatValidator;

public abstract class AbstractNumberValidator
extends AbstractFormatValidator {
    private static final long serialVersionUID = -3088817875906765463L;
    public static final int STANDARD_FORMAT = 0;
    public static final int CURRENCY_FORMAT = 1;
    public static final int PERCENT_FORMAT = 2;
    private final boolean allowFractions;
    private final int formatType;

    public AbstractNumberValidator(boolean strict, int formatType, boolean allowFractions) {
        super(strict);
        this.allowFractions = allowFractions;
        this.formatType = formatType;
    }

    public boolean isAllowFractions() {
        return this.allowFractions;
    }

    public int getFormatType() {
        return this.formatType;
    }

    @Override
    public boolean isValid(String value, String pattern, Locale locale) {
        Object parsedValue = this.parse(value, pattern, locale);
        return parsedValue != null;
    }

    public boolean isInRange(Number value, Number min, Number max) {
        return this.minValue(value, min) && this.maxValue(value, max);
    }

    public boolean minValue(Number value, Number min) {
        if (this.isAllowFractions()) {
            return value.doubleValue() >= min.doubleValue();
        }
        return value.longValue() >= min.longValue();
    }

    public boolean maxValue(Number value, Number max) {
        if (this.isAllowFractions()) {
            return value.doubleValue() <= max.doubleValue();
        }
        return value.longValue() <= max.longValue();
    }

    protected Object parse(String value, String pattern, Locale locale) {
        String string = value = value == null ? null : value.trim();
        if (value == null || value.length() == 0) {
            return null;
        }
        Format formatter = this.getFormat(pattern, locale);
        return this.parse(value, formatter);
    }

    @Override
    protected abstract Object processParsedValue(Object var1, Format var2);

    @Override
    protected Format getFormat(String pattern, Locale locale) {
        boolean usePattern;
        NumberFormat formatter = null;
        boolean bl = usePattern = pattern != null && pattern.length() > 0;
        if (!usePattern) {
            formatter = (NumberFormat)this.getFormat(locale);
        } else if (locale == null) {
            formatter = new DecimalFormat(pattern);
        } else {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
            formatter = new DecimalFormat(pattern, symbols);
        }
        if (this.determineScale(formatter) == 0) {
            formatter.setParseIntegerOnly(true);
        }
        return formatter;
    }

    protected int determineScale(NumberFormat format) {
        int maximumFraction;
        if (!this.isStrict()) {
            return -1;
        }
        if (!this.isAllowFractions() || format.isParseIntegerOnly()) {
            return 0;
        }
        int minimumFraction = format.getMinimumFractionDigits();
        if (minimumFraction != (maximumFraction = format.getMaximumFractionDigits())) {
            return -1;
        }
        int scale = minimumFraction;
        if (format instanceof DecimalFormat) {
            int multiplier = ((DecimalFormat)format).getMultiplier();
            if (multiplier == 100) {
                scale += 2;
            } else if (multiplier == 1000) {
                scale += 3;
            }
        } else if (this.formatType == 2) {
            scale += 2;
        }
        return scale;
    }

    protected Format getFormat(Locale locale) {
        NumberFormat formatter = null;
        switch (this.formatType) {
            case 1: {
                if (locale == null) {
                    formatter = NumberFormat.getCurrencyInstance();
                    break;
                }
                formatter = NumberFormat.getCurrencyInstance(locale);
                break;
            }
            case 2: {
                if (locale == null) {
                    formatter = NumberFormat.getPercentInstance();
                    break;
                }
                formatter = NumberFormat.getPercentInstance(locale);
                break;
            }
            default: {
                formatter = locale == null ? NumberFormat.getInstance() : NumberFormat.getInstance(locale);
            }
        }
        return formatter;
    }
}

