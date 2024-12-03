/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.DateUtil
 *  com.twelvemonkeys.lang.StringUtil
 */
package com.twelvemonkeys.net;

import com.twelvemonkeys.lang.DateUtil;
import com.twelvemonkeys.lang.StringUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class HTTPUtil {
    private static final SimpleDateFormat HTTP_RFC1123_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
    private static final SimpleDateFormat HTTP_RFC850_FORMAT;
    private static final SimpleDateFormat HTTP_ASCTIME_FORMAT;
    private static long sNext50YearWindowChange;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void update50YearWindowIfNeeded() {
        long l = sNext50YearWindowChange;
        if (l < System.currentTimeMillis()) {
            sNext50YearWindowChange = l += 86400000L;
            Date date = new Date(l - 1577847600000L);
            SimpleDateFormat simpleDateFormat = HTTP_RFC850_FORMAT;
            synchronized (simpleDateFormat) {
                HTTP_RFC850_FORMAT.set2DigitYearStart(date);
            }
            simpleDateFormat = HTTP_ASCTIME_FORMAT;
            synchronized (simpleDateFormat) {
                HTTP_ASCTIME_FORMAT.set2DigitYearStart(date);
            }
        }
    }

    private HTTPUtil() {
    }

    public static String formatHTTPDate(long l) {
        return HTTPUtil.formatHTTPDate(new Date(l));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String formatHTTPDate(Date date) {
        SimpleDateFormat simpleDateFormat = HTTP_RFC1123_FORMAT;
        synchronized (simpleDateFormat) {
            return HTTP_RFC1123_FORMAT.format(date);
        }
    }

    public static long parseHTTPDate(String string) throws NumberFormatException {
        return HTTPUtil.parseHTTPDateImpl(string).getTime();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Date parseHTTPDateImpl(String string) throws NumberFormatException {
        Date date;
        SimpleDateFormat simpleDateFormat;
        if (string == null) {
            throw new IllegalArgumentException("date == null");
        }
        if (StringUtil.isEmpty((String)string)) {
            throw new NumberFormatException("Invalid HTTP date: \"" + string + "\"");
        }
        if (string.indexOf(45) >= 0) {
            simpleDateFormat = HTTP_RFC850_FORMAT;
            HTTPUtil.update50YearWindowIfNeeded();
        } else if (string.indexOf(44) < 0) {
            simpleDateFormat = HTTP_ASCTIME_FORMAT;
            HTTPUtil.update50YearWindowIfNeeded();
        } else {
            simpleDateFormat = HTTP_RFC1123_FORMAT;
        }
        try {
            SimpleDateFormat simpleDateFormat2 = simpleDateFormat;
            synchronized (simpleDateFormat2) {
                date = simpleDateFormat.parse(string);
            }
        }
        catch (ParseException parseException) {
            NumberFormatException numberFormatException = new NumberFormatException("Invalid HTTP date: \"" + string + "\"");
            numberFormatException.initCause(parseException);
            throw numberFormatException;
        }
        if (date == null) {
            throw new NumberFormatException("Invalid HTTP date: \"" + string + "\"");
        }
        return date;
    }

    static {
        HTTP_RFC1123_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
        HTTP_RFC850_FORMAT = new SimpleDateFormat("EEE, dd-MMM-yy HH:mm:ss z", Locale.US);
        HTTP_ASCTIME_FORMAT = new SimpleDateFormat("EEE MMM d HH:mm:ss yy", Locale.US);
        sNext50YearWindowChange = DateUtil.currentTimeDay();
        HTTP_RFC850_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
        HTTP_ASCTIME_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
        HTTPUtil.update50YearWindowIfNeeded();
    }
}

