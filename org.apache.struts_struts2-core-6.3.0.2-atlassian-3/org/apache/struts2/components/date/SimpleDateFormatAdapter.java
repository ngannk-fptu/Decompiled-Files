/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.components.date;

import com.opensymphony.xwork2.ActionContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;
import org.apache.struts2.components.date.DateFormatter;

public class SimpleDateFormatAdapter
implements DateFormatter {
    @Override
    public String format(TemporalAccessor temporal, String format) {
        Locale locale = ActionContext.getContext().getLocale();
        DateFormat df = format == null ? DateFormat.getDateTimeInstance(2, 2, locale) : new SimpleDateFormat(format, locale);
        return df.format(new Date(Instant.from(temporal).toEpochMilli()));
    }
}

