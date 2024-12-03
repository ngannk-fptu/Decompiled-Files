/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines;

import java.io.Serializable;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Locale;

public abstract class AbstractFormatValidator
implements Serializable {
    private static final long serialVersionUID = -4690687565200568258L;
    private final boolean strict;

    public AbstractFormatValidator(boolean strict) {
        this.strict = strict;
    }

    public boolean isStrict() {
        return this.strict;
    }

    public boolean isValid(String value) {
        return this.isValid(value, null, null);
    }

    public boolean isValid(String value, String pattern) {
        return this.isValid(value, pattern, null);
    }

    public boolean isValid(String value, Locale locale) {
        return this.isValid(value, null, locale);
    }

    public abstract boolean isValid(String var1, String var2, Locale var3);

    public String format(Object value) {
        return this.format(value, null, null);
    }

    public String format(Object value, String pattern) {
        return this.format(value, pattern, null);
    }

    public String format(Object value, Locale locale) {
        return this.format(value, null, locale);
    }

    public String format(Object value, String pattern, Locale locale) {
        Format formatter = this.getFormat(pattern, locale);
        return this.format(value, formatter);
    }

    protected String format(Object value, Format formatter) {
        return formatter.format(value);
    }

    protected Object parse(String value, Format formatter) {
        ParsePosition pos = new ParsePosition(0);
        Object parsedValue = formatter.parseObject(value, pos);
        if (pos.getErrorIndex() > -1) {
            return null;
        }
        if (this.isStrict() && pos.getIndex() < value.length()) {
            return null;
        }
        if (parsedValue != null) {
            parsedValue = this.processParsedValue(parsedValue, formatter);
        }
        return parsedValue;
    }

    protected abstract Object processParsedValue(Object var1, Format var2);

    protected abstract Format getFormat(String var1, Locale var2);
}

