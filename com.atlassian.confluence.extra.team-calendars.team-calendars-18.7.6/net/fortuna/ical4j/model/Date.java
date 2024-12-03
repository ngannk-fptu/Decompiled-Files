/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import net.fortuna.ical4j.model.Iso8601;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.TimeZones;

public class Date
extends Iso8601 {
    private static final long serialVersionUID = 7136072363141363141L;
    private static final String DEFAULT_PATTERN = "yyyyMMdd";
    private static final String VCARD_PATTERN = "yyyy'-'MM'-'dd";

    public Date() {
        super(DEFAULT_PATTERN, 1, TimeZones.getDateTimeZone());
    }

    protected Date(int precision, TimeZone tz) {
        super(DEFAULT_PATTERN, precision, tz);
    }

    public Date(long time) {
        super(time, DEFAULT_PATTERN, 1, TimeZones.getDateTimeZone());
    }

    protected Date(long time, int precision, TimeZone tz) {
        super(time, DEFAULT_PATTERN, precision, tz);
    }

    public Date(Calendar calendar) {
        this(calendar.getTimeInMillis(), 1, TimeZones.getDateTimeZone());
    }

    public Date(java.util.Date date) {
        this(date.getTime(), 1, TimeZones.getDateTimeZone());
    }

    public Date(String value) throws ParseException {
        this();
        try {
            this.setTime(this.getFormat().parse(value).getTime());
        }
        catch (ParseException pe) {
            if (CompatibilityHints.isHintEnabled("ical4j.compatibility.vcard")) {
                SimpleDateFormat parseFormat = new SimpleDateFormat(VCARD_PATTERN);
                parseFormat.setTimeZone(TimeZones.getDateTimeZone());
                this.setTime(parseFormat.parse(value).getTime());
            }
            throw pe;
        }
    }

    public Date(String value, String pattern) throws ParseException {
        super(DEFAULT_PATTERN, 1, TimeZones.getDateTimeZone());
        SimpleDateFormat parseFormat = new SimpleDateFormat(pattern);
        parseFormat.setTimeZone(TimeZones.getDateTimeZone());
        this.setTime(parseFormat.parse(value).getTime());
    }
}

