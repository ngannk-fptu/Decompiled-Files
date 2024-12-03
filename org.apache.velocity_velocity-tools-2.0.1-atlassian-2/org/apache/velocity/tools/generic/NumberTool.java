/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.generic;

import java.text.NumberFormat;
import java.util.Locale;
import org.apache.velocity.tools.ConversionUtils;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.generic.FormatConfig;

@DefaultKey(value="number")
public class NumberTool
extends FormatConfig {
    @Deprecated
    public static final String DEFAULT_FORMAT_KEY = "format";
    @Deprecated
    public static final String DEFAULT_LOCALE_KEY = "locale";

    public String format(Object obj) {
        return this.format(this.getFormat(), obj);
    }

    public String currency(Object obj) {
        return this.format("currency", obj);
    }

    public String integer(Object obj) {
        return this.format("integer", obj);
    }

    public String number(Object obj) {
        return this.format("number", obj);
    }

    public String percent(Object obj) {
        return this.format("percent", obj);
    }

    public String format(String format, Object obj) {
        return this.format(format, obj, this.getLocale());
    }

    public String format(String format, Object obj, Locale locale) {
        Number number = this.toNumber(obj);
        NumberFormat nf = this.getNumberFormat(format, locale);
        if (number == null || nf == null) {
            return null;
        }
        return nf.format(number);
    }

    public NumberFormat getNumberFormat(String format, Locale locale) {
        return ConversionUtils.getNumberFormat(format, locale);
    }

    @Deprecated
    protected NumberFormat getNumberFormat(int numberStyle, Locale locale) {
        return ConversionUtils.getNumberFormat(numberStyle, locale);
    }

    @Deprecated
    protected int getStyleAsInt(String style) {
        return ConversionUtils.getNumberStyleAsInt(style);
    }

    public Number toNumber(Object obj) {
        return this.toNumber(this.getFormat(), obj, this.getLocale());
    }

    public Number toNumber(String format, Object obj) {
        return this.toNumber(format, obj, this.getLocale());
    }

    public Number toNumber(String format, Object obj, Locale locale) {
        return ConversionUtils.toNumber(obj, format, locale);
    }
}

