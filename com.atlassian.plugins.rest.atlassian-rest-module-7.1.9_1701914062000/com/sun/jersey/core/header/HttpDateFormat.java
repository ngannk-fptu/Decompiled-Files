/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.header;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class HttpDateFormat {
    private static final String RFC1123_DATE_FORMAT_PATTERN = "EEE, dd MMM yyyy HH:mm:ss zzz";
    private static final String RFC1036_DATE_FORMAT_PATTERN = "EEEE, dd-MMM-yy HH:mm:ss zzz";
    private static final String ANSI_C_ASCTIME_DATE_FORMAT_PATTERN = "EEE MMM d HH:mm:ss yyyy";
    private static ThreadLocal<List<SimpleDateFormat>> dateFormats = new ThreadLocal<List<SimpleDateFormat>>(){

        @Override
        protected synchronized List<SimpleDateFormat> initialValue() {
            return HttpDateFormat.createDateFormats();
        }
    };

    private HttpDateFormat() {
    }

    private static List<SimpleDateFormat> createDateFormats() {
        SimpleDateFormat[] dateFormats = new SimpleDateFormat[]{new SimpleDateFormat(RFC1123_DATE_FORMAT_PATTERN, Locale.US), new SimpleDateFormat(RFC1036_DATE_FORMAT_PATTERN, Locale.US), new SimpleDateFormat(ANSI_C_ASCTIME_DATE_FORMAT_PATTERN, Locale.US)};
        TimeZone tz = TimeZone.getTimeZone("GMT");
        dateFormats[0].setTimeZone(tz);
        dateFormats[1].setTimeZone(tz);
        dateFormats[2].setTimeZone(tz);
        return Collections.unmodifiableList(Arrays.asList(dateFormats));
    }

    public static List<SimpleDateFormat> getDateFormats() {
        return dateFormats.get();
    }

    public static SimpleDateFormat getPreferedDateFormat() {
        return dateFormats.get().get(0);
    }

    public static Date readDate(String date) throws ParseException {
        ParseException pe = null;
        for (SimpleDateFormat f : HttpDateFormat.getDateFormats()) {
            try {
                return f.parse(date);
            }
            catch (ParseException e) {
                pe = pe == null ? e : pe;
            }
        }
        throw pe;
    }
}

