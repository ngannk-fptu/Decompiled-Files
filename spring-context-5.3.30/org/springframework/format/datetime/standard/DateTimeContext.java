/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.format.datetime.standard;

import java.time.ZoneId;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.lang.Nullable;

public class DateTimeContext {
    @Nullable
    private Chronology chronology;
    @Nullable
    private ZoneId timeZone;

    public void setChronology(@Nullable Chronology chronology) {
        this.chronology = chronology;
    }

    @Nullable
    public Chronology getChronology() {
        return this.chronology;
    }

    public void setTimeZone(@Nullable ZoneId timeZone) {
        this.timeZone = timeZone;
    }

    @Nullable
    public ZoneId getTimeZone() {
        return this.timeZone;
    }

    public DateTimeFormatter getFormatter(DateTimeFormatter formatter) {
        if (this.chronology != null) {
            formatter = formatter.withChronology(this.chronology);
        }
        if (this.timeZone != null) {
            formatter = formatter.withZone(this.timeZone);
        } else {
            TimeZone timeZone;
            LocaleContext localeContext = LocaleContextHolder.getLocaleContext();
            if (localeContext instanceof TimeZoneAwareLocaleContext && (timeZone = ((TimeZoneAwareLocaleContext)localeContext).getTimeZone()) != null) {
                formatter = formatter.withZone(timeZone.toZoneId());
            }
        }
        return formatter;
    }
}

