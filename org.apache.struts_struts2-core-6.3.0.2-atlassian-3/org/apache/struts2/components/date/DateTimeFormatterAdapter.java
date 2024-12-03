/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.components.date;

import com.opensymphony.xwork2.ActionContext;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import org.apache.struts2.components.date.DateFormatter;

public class DateTimeFormatterAdapter
implements DateFormatter {
    @Override
    public String format(TemporalAccessor temporal, String format) {
        Locale locale = ActionContext.getContext().getLocale();
        DateTimeFormatter dtf = format == null ? DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(locale) : DateTimeFormatter.ofPattern(format, locale);
        return dtf.format(temporal);
    }
}

