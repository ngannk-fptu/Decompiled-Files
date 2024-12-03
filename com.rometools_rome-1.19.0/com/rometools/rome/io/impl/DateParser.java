/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.io.impl.PropertiesLoader;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DateParser {
    private static String[] ADDITIONAL_MASKS;
    private static final String[] RFC822_MASKS;
    private static final String[] W3CDATETIME_MASKS;
    private static final String[] masks;

    private DateParser() {
    }

    private static Date parseUsingMask(String[] masks, String sDate, Locale locale) {
        if (sDate != null) {
            sDate = sDate.trim();
        }
        ParsePosition pp = null;
        Date d = null;
        for (int i = 0; d == null && i < masks.length; ++i) {
            SimpleDateFormat df = new SimpleDateFormat(masks[i].trim(), locale);
            df.setLenient(true);
            try {
                pp = new ParsePosition(0);
                d = ((DateFormat)df).parse(sDate, pp);
                if (pp.getIndex() == sDate.length()) continue;
                d = null;
                continue;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return d;
    }

    public static Date parseRFC822(String sDate, Locale locale) {
        sDate = DateParser.convertUnsupportedTimeZones(sDate);
        return DateParser.parseUsingMask(RFC822_MASKS, sDate, locale);
    }

    private static String convertUnsupportedTimeZones(String sDate) {
        List<String> unsupportedZeroOffsetTimeZones = Arrays.asList("UT", "Z");
        List<String> splitted = Arrays.asList(sDate.split(" "));
        for (String timeZone : unsupportedZeroOffsetTimeZones) {
            if (!splitted.contains(timeZone)) continue;
            return DateParser.replaceLastOccurrence(sDate, timeZone, "UTC");
        }
        return sDate;
    }

    private static String replaceLastOccurrence(String original, String target, String replacement) {
        int lastIndexOfTarget = original.lastIndexOf(target);
        if (lastIndexOfTarget == -1) {
            return original;
        }
        return new StringBuilder(original).replace(lastIndexOfTarget, lastIndexOfTarget + target.length(), replacement).toString();
    }

    public static Date parseW3CDateTime(String sDate, Locale locale) {
        int tIndex = sDate.indexOf("T");
        if (tIndex > -1) {
            int tzdIndex;
            if (sDate.endsWith("Z")) {
                sDate = sDate.substring(0, sDate.length() - 1) + "+00:00";
            }
            if ((tzdIndex = sDate.indexOf("+", tIndex)) == -1) {
                tzdIndex = sDate.indexOf("-", tIndex);
            }
            if (tzdIndex > -1) {
                String pre = sDate.substring(0, tzdIndex);
                int secFraction = pre.indexOf(",");
                if (secFraction > -1) {
                    pre = pre.substring(0, secFraction);
                }
                String post = sDate.substring(tzdIndex);
                sDate = pre + "GMT" + post;
            }
        } else {
            sDate = sDate + "T00:00GMT";
        }
        return DateParser.parseUsingMask(W3CDATETIME_MASKS, sDate, locale);
    }

    public static Date parseDate(String sDate, Locale locale) {
        Date date = null;
        if (ADDITIONAL_MASKS.length > 0 && (date = DateParser.parseUsingMask(ADDITIONAL_MASKS, sDate, locale)) != null) {
            return date;
        }
        date = DateParser.parseW3CDateTime(sDate, locale);
        if (date == null) {
            date = DateParser.parseRFC822(sDate, locale);
        }
        return date;
    }

    public static String formatRFC822(Date date, Locale locale) {
        SimpleDateFormat dateFormater = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", locale);
        dateFormater.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormater.format(date);
    }

    public static String formatW3CDateTime(Date date, Locale locale) {
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", locale);
        dateFormater.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormater.format(date);
    }

    static {
        RFC822_MASKS = new String[]{"EEE, dd MMM yy HH:mm:ss z", "EEE, dd MMM yy HH:mm z", "dd MMM yy HH:mm:ss z", "dd MMM yy HH:mm z"};
        W3CDATETIME_MASKS = new String[]{"yyyy-MM-dd'T'HH:mm:ss.SSSz", "yyyy-MM-dd't'HH:mm:ss.SSSz", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd't'HH:mm:ss.SSS'z'", "yyyy-MM-dd'T'HH:mm:ssz", "yyyy-MM-dd't'HH:mm:ssz", "yyyy-MM-dd'T'HH:mm:ssZ", "yyyy-MM-dd't'HH:mm:ssZ", "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd't'HH:mm:ss'z'", "yyyy-MM-dd'T'HH:mmz", "yyyy-MM'T'HH:mmz", "yyyy'T'HH:mmz", "yyyy-MM-dd't'HH:mmz", "yyyy-MM-dd'T'HH:mm'Z'", "yyyy-MM-dd't'HH:mm'z'", "yyyy-MM-dd", "yyyy-MM", "yyyy"};
        masks = new String[]{"yyyy-MM-dd'T'HH:mm:ss.SSSz", "yyyy-MM-dd't'HH:mm:ss.SSSz", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd't'HH:mm:ss.SSS'z'", "yyyy-MM-dd'T'HH:mm:ssz", "yyyy-MM-dd't'HH:mm:ssz", "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd't'HH:mm:ss'z'", "yyyy-MM-dd'T'HH:mmz", "yyyy-MM-dd't'HH:mmz", "yyyy-MM-dd'T'HH:mm'Z'", "yyyy-MM-dd't'HH:mm'z'", "yyyy-MM-dd", "yyyy-MM", "yyyy"};
        ADDITIONAL_MASKS = PropertiesLoader.getPropertiesLoader().getTokenizedProperty("datetime.extra.masks", "|");
    }
}

