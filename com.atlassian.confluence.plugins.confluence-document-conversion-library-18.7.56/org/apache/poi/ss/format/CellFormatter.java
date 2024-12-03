/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.format;

import java.util.Locale;
import org.apache.poi.util.LocaleUtil;

public abstract class CellFormatter {
    protected final String format;
    protected final Locale locale;

    public CellFormatter(String format) {
        this(LocaleUtil.getUserLocale(), format);
    }

    public CellFormatter(Locale locale, String format) {
        this.locale = locale;
        this.format = format;
    }

    public abstract void formatValue(StringBuffer var1, Object var2);

    public abstract void simpleValue(StringBuffer var1, Object var2);

    public String format(Object value) {
        StringBuffer sb = new StringBuffer();
        this.formatValue(sb, value);
        return sb.toString();
    }

    public String simpleFormat(Object value) {
        StringBuffer sb = new StringBuffer();
        this.simpleValue(sb, value);
        return sb.toString();
    }

    static String quote(String str) {
        return '\"' + str + '\"';
    }
}

