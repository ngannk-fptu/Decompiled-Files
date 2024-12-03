/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jwt.util.DateUtils
 */
package com.nimbusds.oauth2.sdk.util.date;

import com.nimbusds.jwt.util.DateUtils;
import com.nimbusds.oauth2.sdk.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateWithTimeZoneOffset {
    private final Date date;
    private final int tzOffsetMinutes;
    private final boolean isUTC;

    public DateWithTimeZoneOffset(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        this.date = date;
        this.tzOffsetMinutes = 0;
        this.isUTC = true;
    }

    public DateWithTimeZoneOffset(Date date, int tzOffsetMinutes) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        this.date = date;
        if (tzOffsetMinutes >= 720 || tzOffsetMinutes <= -720) {
            throw new IllegalArgumentException("The time zone offset must be less than +/- 12 x 60 minutes");
        }
        this.tzOffsetMinutes = tzOffsetMinutes;
        this.isUTC = false;
    }

    public DateWithTimeZoneOffset(Date date, TimeZone tz) {
        this(date, tz.getOffset(date.getTime()) / 60000);
    }

    public Date getDate() {
        return this.date;
    }

    public boolean isUTC() {
        return this.isUTC;
    }

    public int getTimeZoneOffsetMinutes() {
        return this.tzOffsetMinutes;
    }

    public String toISO8601String() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        sdf.setTimeZone(tz);
        long localTimeSeconds = DateUtils.toSecondsSinceEpoch((Date)this.date);
        String out = sdf.format(DateUtils.fromSecondsSinceEpoch((long)(localTimeSeconds += (long)this.tzOffsetMinutes * 60L)));
        if (this.isUTC()) {
            return out + "Z";
        }
        int tzOffsetWholeHours = this.tzOffsetMinutes / 60;
        int tzOffsetRemainderMinutes = this.tzOffsetMinutes - tzOffsetWholeHours * 60;
        if (this.tzOffsetMinutes == 0) {
            return out + "+00:00";
        }
        out = tzOffsetWholeHours > 0 ? out + "+" + (tzOffsetWholeHours < 10 ? "0" : "") + Math.abs(tzOffsetWholeHours) : (tzOffsetWholeHours < 0 ? out + "-" + (tzOffsetWholeHours > -10 ? "0" : "") + Math.abs(tzOffsetWholeHours) : (this.tzOffsetMinutes > 0 ? out + "+00" : out + "-00"));
        out = out + ":";
        out = tzOffsetRemainderMinutes > 0 ? out + (tzOffsetRemainderMinutes < 10 ? "0" : "") + tzOffsetRemainderMinutes : (tzOffsetRemainderMinutes < 0 ? out + (tzOffsetRemainderMinutes > -10 ? "0" : "") + Math.abs(tzOffsetRemainderMinutes) : out + "00");
        return out;
    }

    public String toString() {
        return this.toISO8601String();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DateWithTimeZoneOffset)) {
            return false;
        }
        DateWithTimeZoneOffset that = (DateWithTimeZoneOffset)o;
        return this.tzOffsetMinutes == that.tzOffsetMinutes && this.getDate().equals(that.getDate());
    }

    public int hashCode() {
        return Objects.hash(this.getDate(), this.tzOffsetMinutes);
    }

    public static DateWithTimeZoneOffset parseISO8601String(String s) throws ParseException {
        int tzOffsetMinutes;
        Date date;
        Matcher m;
        String stringToParse = s;
        if (Pattern.compile(".*[\\+\\-][\\d]{2}$").matcher(s).matches()) {
            stringToParse = stringToParse + ":00";
        }
        if ((m = Pattern.compile("(.*[\\+\\-][\\d]{2})(\\d{2})$").matcher(stringToParse)).matches()) {
            stringToParse = m.group(1) + ":" + m.group(2);
        }
        if ((m = Pattern.compile("(.*\\d{2}:\\d{2}:\\d{2})([\\+\\-Z].*)$").matcher(stringToParse)).matches()) {
            stringToParse = m.group(1) + ".000" + m.group(2);
        }
        int colonCount = stringToParse.length() - stringToParse.replace(":", "").length();
        try {
            date = colonCount == 1 ? new SimpleDateFormat("yyyy-MM-dd'T'HH:mmXXX").parse(stringToParse) : new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse(stringToParse);
        }
        catch (java.text.ParseException e) {
            throw new ParseException(e.getMessage());
        }
        if (stringToParse.trim().endsWith("Z") || stringToParse.trim().endsWith("z")) {
            return new DateWithTimeZoneOffset(date);
        }
        try {
            String offsetSpec = stringToParse.substring("2019-11-01T06:19:43.000".length());
            int hoursOffset = Integer.parseInt(offsetSpec.substring(0, 3));
            int minutesOffset = Integer.parseInt(offsetSpec.substring(4));
            tzOffsetMinutes = offsetSpec.startsWith("+") ? hoursOffset * 60 + minutesOffset : hoursOffset * 60 - minutesOffset;
        }
        catch (Exception e) {
            throw new ParseException("Unexpected timezone offset: " + s);
        }
        return new DateWithTimeZoneOffset(date, tzOffsetMinutes);
    }
}

