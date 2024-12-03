/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import net.fortuna.ical4j.model.Iso8601;
import net.fortuna.ical4j.util.TimeZones;

public class Time
extends Iso8601 {
    private static final long serialVersionUID = -8401010870773304348L;
    private boolean utc = false;
    private static final String DEFAULT_PATTERN = "HHmmss";
    private static final String UTC_PATTERN = "HHmmss'Z'";

    public Time(TimeZone timezone) {
        this(timezone, TimeZones.isUtc(timezone));
    }

    public Time(TimeZone timezone, boolean utc) {
        super(utc ? UTC_PATTERN : DEFAULT_PATTERN, 0, timezone);
        this.getFormat().setTimeZone(timezone);
        this.utc = utc;
    }

    public Time(long time, TimeZone timezone) {
        this(time, timezone, TimeZones.isUtc(timezone));
    }

    public Time(long time, TimeZone timezone, boolean utc) {
        super(time, utc ? UTC_PATTERN : DEFAULT_PATTERN, 0, timezone);
        this.getFormat().setTimeZone(timezone);
        this.utc = utc;
    }

    public Time(Date time, TimeZone timezone) {
        this(time, timezone, TimeZones.isUtc(timezone));
    }

    public Time(Date time, TimeZone timezone, boolean utc) {
        super(time.getTime(), utc ? UTC_PATTERN : DEFAULT_PATTERN, 0, timezone);
        this.getFormat().setTimeZone(timezone);
        this.utc = utc;
    }

    public Time(String value, TimeZone timezone) throws ParseException {
        this(value, timezone, TimeZones.isUtc(timezone));
    }

    public Time(String value, TimeZone timezone, boolean utc) throws ParseException {
        this(Time.parseDate(value, timezone), timezone, utc);
    }

    private static Date parseDate(String value, TimeZone timezone) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(DEFAULT_PATTERN);
        df.setTimeZone(timezone);
        try {
            return df.parse(value);
        }
        catch (ParseException e) {
            df = new SimpleDateFormat(UTC_PATTERN);
            df.setTimeZone(timezone);
            return df.parse(value);
        }
    }

    public final boolean isUtc() {
        return this.utc;
    }
}

