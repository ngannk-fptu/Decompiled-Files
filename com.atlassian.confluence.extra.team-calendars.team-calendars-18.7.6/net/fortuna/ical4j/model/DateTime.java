/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 */
package net.fortuna.ical4j.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import net.fortuna.ical4j.model.CalendarDateFormatFactory;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Time;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.TimeZones;
import org.apache.commons.lang3.builder.EqualsBuilder;

public class DateTime
extends Date {
    private static final long serialVersionUID = -6407231357919440387L;
    private static final String DEFAULT_PATTERN = "yyyyMMdd'T'HHmmss";
    private static final String UTC_PATTERN = "yyyyMMdd'T'HHmmss'Z'";
    private static final String VCARD_PATTERN = "yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'";
    private static final String RELAXED_PATTERN = "yyyyMMdd";
    private static final DateFormatCache UTC_FORMAT;
    private static final DateFormatCache DEFAULT_FORMAT;
    private static final DateFormatCache LENIENT_DEFAULT_FORMAT;
    private static final DateFormatCache RELAXED_FORMAT;
    private static final DateFormatCache VCARD_FORMAT;
    private Time time;
    private TimeZone timezone;

    public DateTime() {
        super(0, java.util.TimeZone.getDefault());
        this.time = new Time(this.getTime(), this.getFormat().getTimeZone());
    }

    public DateTime(boolean utc) {
        this();
        this.setUtc(utc);
    }

    public DateTime(long time) {
        super(time, 0, java.util.TimeZone.getDefault());
        this.time = new Time(time, this.getFormat().getTimeZone());
    }

    public DateTime(java.util.Date date) {
        super(date.getTime(), 0, java.util.TimeZone.getDefault());
        this.time = new Time(date.getTime(), this.getFormat().getTimeZone());
        if (date instanceof DateTime) {
            DateTime dateTime = (DateTime)date;
            if (dateTime.isUtc()) {
                this.setUtc(true);
            } else {
                this.setTimeZone(dateTime.getTimeZone());
            }
        }
    }

    public DateTime(java.util.Date date, TimeZone timeZone) {
        this(date);
        this.setTimeZone(timeZone);
    }

    public DateTime(String value) throws ParseException {
        this(value, (TimeZone)null);
    }

    public DateTime(String value, TimeZone timezone) throws ParseException {
        block11: {
            super(0L, 0, timezone != null ? timezone : TimeZones.getDefault());
            this.time = new Time(this.getTime(), this.getFormat().getTimeZone());
            try {
                if (value.endsWith("Z")) {
                    this.setTime(value, UTC_FORMAT.get(), null);
                    this.setUtc(true);
                } else {
                    if (timezone != null) {
                        this.setTime(value, DEFAULT_FORMAT.get(), timezone);
                    } else {
                        this.setTime(value, LENIENT_DEFAULT_FORMAT.get(), this.getFormat().getTimeZone());
                    }
                    this.setTimeZone(timezone);
                }
            }
            catch (ParseException pe) {
                if (CompatibilityHints.isHintEnabled("ical4j.compatibility.vcard")) {
                    try {
                        this.setTime(value, VCARD_FORMAT.get(), timezone);
                        this.setTimeZone(timezone);
                    }
                    catch (ParseException pe2) {
                        if (CompatibilityHints.isHintEnabled("ical4j.parsing.relaxed")) {
                            this.setTime(value, RELAXED_FORMAT.get(), timezone);
                            this.setTimeZone(timezone);
                        }
                        break block11;
                    }
                }
                if (CompatibilityHints.isHintEnabled("ical4j.parsing.relaxed")) {
                    this.setTime(value, RELAXED_FORMAT.get(), timezone);
                    this.setTimeZone(timezone);
                }
                throw pe;
            }
        }
    }

    public DateTime(String value, String pattern, TimeZone timezone) throws ParseException {
        super(0L, 0, timezone != null ? timezone : TimeZones.getDefault());
        this.time = new Time(this.getTime(), this.getFormat().getTimeZone());
        DateFormat format = CalendarDateFormatFactory.getInstance(pattern);
        this.setTime(value, format, timezone);
    }

    public DateTime(String value, String pattern, boolean utc) throws ParseException {
        this(0L);
        DateFormat format = CalendarDateFormatFactory.getInstance(pattern);
        if (utc) {
            this.setTime(value, format, UTC_FORMAT.get().getTimeZone());
        } else {
            this.setTime(value, format, null);
        }
        this.setUtc(utc);
    }

    private void setTime(String value, DateFormat format, java.util.TimeZone tz) throws ParseException {
        if (tz != null) {
            format.setTimeZone(tz);
        }
        this.setTime(format.parse(value).getTime());
    }

    @Override
    public final void setTime(long time) {
        super.setTime(time);
        if (this.time != null) {
            this.time.setTime(time);
        }
    }

    public final boolean isUtc() {
        return this.time.isUtc();
    }

    public final void setUtc(boolean utc) {
        this.timezone = null;
        if (utc) {
            this.getFormat().setTimeZone(TimeZones.getUtcTimeZone());
        } else {
            this.resetTimeZone();
        }
        this.time = new Time(this.time, this.getFormat().getTimeZone(), utc);
    }

    public final void setTimeZone(TimeZone timezone) {
        this.timezone = timezone;
        if (timezone != null) {
            this.getFormat().setTimeZone(timezone);
        } else {
            this.resetTimeZone();
        }
        this.time = new Time(this.time, this.getFormat().getTimeZone(), false);
    }

    private void resetTimeZone() {
        this.getFormat().setTimeZone(TimeZones.getDefault());
    }

    public final TimeZone getTimeZone() {
        return this.timezone;
    }

    @Override
    public final String toString() {
        return super.toString() + 'T' + this.time.toString();
    }

    @Override
    public boolean equals(Object arg0) {
        if (arg0 instanceof DateTime) {
            return new EqualsBuilder().append((Object)this.time, (Object)((DateTime)arg0).time).isEquals();
        }
        return super.equals(arg0);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    static {
        SimpleDateFormat format = new SimpleDateFormat(UTC_PATTERN);
        format.setTimeZone(TimeZones.getUtcTimeZone());
        format.setLenient(false);
        UTC_FORMAT = new DateFormatCache(format);
        format = new SimpleDateFormat(DEFAULT_PATTERN);
        format.setLenient(false);
        DEFAULT_FORMAT = new DateFormatCache(format);
        format = new SimpleDateFormat(DEFAULT_PATTERN);
        LENIENT_DEFAULT_FORMAT = new DateFormatCache(format);
        format = new SimpleDateFormat(RELAXED_PATTERN);
        format.setLenient(true);
        RELAXED_FORMAT = new DateFormatCache(format);
        format = new SimpleDateFormat(VCARD_PATTERN);
        VCARD_FORMAT = new DateFormatCache(format);
    }

    private static class DateFormatCache {
        private final Map<Thread, DateFormat> threadMap = Collections.synchronizedMap(new WeakHashMap());
        private final DateFormat templateFormat;

        private DateFormatCache(DateFormat dateFormat) {
            this.templateFormat = dateFormat;
        }

        public DateFormat get() {
            DateFormat dateFormat = this.threadMap.get(Thread.currentThread());
            if (dateFormat == null) {
                dateFormat = (DateFormat)this.templateFormat.clone();
                this.threadMap.put(Thread.currentThread(), dateFormat);
            }
            return dateFormat;
        }
    }
}

