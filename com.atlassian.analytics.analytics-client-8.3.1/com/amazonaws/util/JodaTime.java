/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 */
package com.amazonaws.util;

import com.amazonaws.log.InternalLogFactory;
import com.amazonaws.util.Classes;
import com.amazonaws.util.DateUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public enum JodaTime {

    private static final boolean expectedBehavior = JodaTime.checkExpectedBehavior();

    public static String getVersion() {
        return LazyHolder.version;
    }

    public static boolean hasExpectedBehavior() {
        return expectedBehavior;
    }

    private static boolean checkExpectedBehavior() {
        try {
            return JodaTime.checkTT0031561767() && JodaTime.checkFormatIso8601Date() && JodaTime.checkFormatRfc822Date() && JodaTime.checkAlternateIso8601DateFormat() && JodaTime.checkInvalidDate() && JodaTime.checkParseCompressedIso8601Date() && JodaTime.checkParseIso8601Date() && JodaTime.checkParseIso8601DateUsingAlternativeFormat() && JodaTime.checkParseRfc822Date();
        }
        catch (Exception exception) {
            return false;
        }
    }

    private static boolean checkTT0031561767() throws ParseException {
        String input = "Fri, 16 May 2014 23:56:46 GMT";
        Date date = new Date(DateUtils.rfc822DateFormat.parseMillis(input));
        return input.equals(DateUtils.rfc822DateFormat.print(date.getTime()));
    }

    private static boolean checkFormatIso8601Date() throws ParseException {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(new SimpleTimeZone(0, "GMT"));
        String expected = sdf.format(date);
        String actual = DateUtils.iso8601DateFormat.print(date.getTime());
        if (expected.equals(actual)) {
            Date expectedDate = sdf.parse(expected);
            Date actualDate = DateUtils.doParseISO8601Date(actual);
            return expectedDate.equals(actualDate);
        }
        return false;
    }

    private static boolean checkFormatRfc822Date() throws ParseException {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        sdf.setTimeZone(new SimpleTimeZone(0, "GMT"));
        String expected = sdf.format(date);
        String actual = DateUtils.rfc822DateFormat.print(date.getTime());
        if (expected.equals(actual)) {
            Date expectedDate = sdf.parse(expected);
            Date actualDate2 = new Date(DateUtils.rfc822DateFormat.parseMillis(actual));
            return expectedDate.equals(actualDate2);
        }
        return false;
    }

    private static boolean checkParseCompressedIso8601Date() throws ParseException {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        sdf.setTimeZone(new SimpleTimeZone(0, "GMT"));
        String formatted = sdf.format(date);
        Date expected = sdf.parse(formatted);
        Date actual = new Date(DateUtils.compressedIso8601DateFormat.parseMillis(formatted));
        return expected.equals(actual);
    }

    private static boolean checkParseRfc822Date() throws ParseException {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        sdf.setTimeZone(new SimpleTimeZone(0, "GMT"));
        String formatted = sdf.format(date);
        Date expected = sdf.parse(formatted);
        Date actual2 = new Date(DateUtils.rfc822DateFormat.parseMillis(formatted));
        return expected.equals(actual2);
    }

    private static boolean checkParseIso8601Date() throws ParseException {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(new SimpleTimeZone(0, "GMT"));
        String formatted = sdf.format(date);
        String alternative = DateUtils.iso8601DateFormat.print(date.getTime());
        if (formatted.equals(alternative)) {
            Date expectedDate = sdf.parse(formatted);
            Date actualDate = DateUtils.doParseISO8601Date(formatted);
            return expectedDate.equals(actualDate);
        }
        return false;
    }

    private static boolean checkParseIso8601DateUsingAlternativeFormat() throws ParseException {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(new SimpleTimeZone(0, "GMT"));
        String formatted = sdf.format(date);
        String alternative = DateUtils.alternateIso8601DateFormat.print(date.getTime());
        if (formatted.equals(alternative)) {
            Date expectedDate = sdf.parse(formatted);
            Date actualDate = DateUtils.parseISO8601Date(formatted);
            return expectedDate.equals(actualDate);
        }
        return false;
    }

    private static boolean checkAlternateIso8601DateFormat() throws ParseException {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(new SimpleTimeZone(0, "GMT"));
        String expected = sdf.format(date);
        String actual = DateUtils.alternateIso8601DateFormat.print(date.getTime());
        if (expected.equals(actual)) {
            Date expectedDate = sdf.parse(expected);
            DateTime actualDateTime = DateUtils.alternateIso8601DateFormat.parseDateTime(actual);
            return expectedDate.getTime() == actualDateTime.getMillis();
        }
        return false;
    }

    private static boolean checkInvalidDate() {
        String input = "2014-03-06T14:28:58.000Z.000Z";
        try {
            DateUtils.doParseISO8601Date("2014-03-06T14:28:58.000Z.000Z");
            return false;
        }
        catch (RuntimeException runtimeException) {
            return true;
        }
    }

    private static class LazyHolder {
        static final String version = LazyHolder.getVersion();

        private LazyHolder() {
        }

        private static String getVersion() {
            try {
                JarFile jf = Classes.jarFileOf(DateTimeZone.class);
                if (jf == null) {
                    return null;
                }
                Manifest mf = jf.getManifest();
                Attributes attrs = mf.getMainAttributes();
                String name = attrs.getValue("Bundle-Name");
                String version = attrs.getValue("Bundle-Version");
                if ("Joda-Time".equals(name) && version != null) {
                    return version;
                }
            }
            catch (Exception e) {
                InternalLogFactory.getLog(JodaTime.class).debug("FYI", e);
            }
            return null;
        }
    }
}

