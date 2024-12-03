/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import java.text.DateFormat;
import java.util.Date;
import net.fortuna.ical4j.model.CalendarDateFormatFactory;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.Dates;

public abstract class Iso8601
extends Date {
    private static final long serialVersionUID = -4290728005713946811L;
    private static final java.util.TimeZone GMT = TimeZone.getTimeZone("Etc/GMT");
    private DateFormat format;
    private DateFormat gmtFormat;
    private int precision;

    public Iso8601(long time, String pattern, int precision, java.util.TimeZone tz) {
        super(Dates.round(time, precision, tz));
        this.format = CalendarDateFormatFactory.getInstance(pattern);
        this.format.setTimeZone(tz);
        this.format.setLenient(CompatibilityHints.isHintEnabled("ical4j.parsing.relaxed"));
        this.precision = precision;
    }

    public Iso8601(String pattern, int precision, java.util.TimeZone tz) {
        this(Dates.getCurrentTimeRounded(), pattern, precision, tz);
    }

    public Iso8601(Date time, String pattern, int precision, java.util.TimeZone tz) {
        this(time.getTime(), pattern, precision, tz);
    }

    @Override
    public String toString() {
        java.util.TimeZone timeZone = this.format.getTimeZone();
        if (!(timeZone instanceof TimeZone)) {
            if (this.gmtFormat == null) {
                this.gmtFormat = (DateFormat)this.format.clone();
                this.gmtFormat.setTimeZone(GMT);
            }
            if (timeZone.inDaylightTime(this) && timeZone.inDaylightTime(new Date(this.getTime() - 1L))) {
                return this.gmtFormat.format(new Date(this.getTime() + (long)timeZone.getRawOffset() + (long)timeZone.getDSTSavings()));
            }
            return this.gmtFormat.format(new Date(this.getTime() + (long)timeZone.getRawOffset()));
        }
        return this.format.format(this);
    }

    protected final DateFormat getFormat() {
        return this.format;
    }

    @Override
    public void setTime(long time) {
        if (this.format != null) {
            super.setTime(Dates.round(time, this.precision, this.format.getTimeZone()));
        } else {
            super.setTime(time);
        }
    }
}

