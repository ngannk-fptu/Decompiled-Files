/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTimeZone
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 *  org.joda.time.format.ISODateTimeFormat
 */
package org.springframework.format.datetime.joda;

import java.util.TimeZone;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

@Deprecated
public class DateTimeFormatterFactory {
    @Nullable
    private String pattern;
    @Nullable
    private DateTimeFormat.ISO iso;
    @Nullable
    private String style;
    @Nullable
    private TimeZone timeZone;

    public DateTimeFormatterFactory() {
    }

    public DateTimeFormatterFactory(String pattern) {
        this.pattern = pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setIso(DateTimeFormat.ISO iso) {
        this.iso = iso;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public DateTimeFormatter createDateTimeFormatter() {
        return this.createDateTimeFormatter(DateTimeFormat.mediumDateTime());
    }

    public DateTimeFormatter createDateTimeFormatter(DateTimeFormatter fallbackFormatter) {
        DateTimeFormatter dateTimeFormatter;
        block8: {
            block9: {
                block7: {
                    dateTimeFormatter = null;
                    if (!StringUtils.hasLength(this.pattern)) break block7;
                    dateTimeFormatter = DateTimeFormat.forPattern((String)this.pattern);
                    break block8;
                }
                if (this.iso == null || this.iso == DateTimeFormat.ISO.NONE) break block9;
                switch (this.iso) {
                    case DATE: {
                        dateTimeFormatter = ISODateTimeFormat.date();
                        break block8;
                    }
                    case TIME: {
                        dateTimeFormatter = ISODateTimeFormat.time();
                        break block8;
                    }
                    case DATE_TIME: {
                        dateTimeFormatter = ISODateTimeFormat.dateTime();
                        break block8;
                    }
                    default: {
                        throw new IllegalStateException("Unsupported ISO format: " + (Object)((Object)this.iso));
                    }
                }
            }
            if (StringUtils.hasLength(this.style)) {
                dateTimeFormatter = DateTimeFormat.forStyle((String)this.style);
            }
        }
        if (dateTimeFormatter != null && this.timeZone != null) {
            dateTimeFormatter = dateTimeFormatter.withZone(DateTimeZone.forTimeZone((TimeZone)this.timeZone));
        }
        return dateTimeFormatter != null ? dateTimeFormatter : fallbackFormatter;
    }
}

