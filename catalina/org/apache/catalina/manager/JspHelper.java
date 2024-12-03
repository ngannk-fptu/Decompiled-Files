/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.security.Escape
 */
package org.apache.catalina.manager;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.catalina.Session;
import org.apache.catalina.manager.util.SessionUtils;
import org.apache.tomcat.util.security.Escape;

public class JspHelper {
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final int HIGHEST_SPECIAL = 62;
    private static final char[][] specialCharactersRepresentation = new char[63][];

    private JspHelper() {
    }

    public static String guessDisplayLocaleFromSession(Session in_session) {
        return JspHelper.localeToString(SessionUtils.guessLocaleFromSession(in_session));
    }

    private static String localeToString(Locale locale) {
        if (locale != null) {
            return JspHelper.escapeXml(locale.toString());
        }
        return "";
    }

    public static String guessDisplayUserFromSession(Session in_session) {
        Object user = SessionUtils.guessUserFromSession(in_session);
        return JspHelper.escapeXml(user);
    }

    public static String getDisplayCreationTimeForSession(Session in_session) {
        try {
            if (in_session.getCreationTime() == 0L) {
                return "";
            }
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_FORMAT);
            return formatter.format(new Date(in_session.getCreationTime()));
        }
        catch (IllegalStateException ise) {
            return "";
        }
    }

    public static String getDisplayLastAccessedTimeForSession(Session in_session) {
        try {
            if (in_session.getLastAccessedTime() == 0L) {
                return "";
            }
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_FORMAT);
            return formatter.format(new Date(in_session.getLastAccessedTime()));
        }
        catch (IllegalStateException ise) {
            return "";
        }
    }

    public static String getDisplayUsedTimeForSession(Session in_session) {
        try {
            if (in_session.getCreationTime() == 0L) {
                return "";
            }
        }
        catch (IllegalStateException ise) {
            return "";
        }
        return JspHelper.secondsToTimeString(SessionUtils.getUsedTimeForSession(in_session) / 1000L);
    }

    public static String getDisplayTTLForSession(Session in_session) {
        try {
            if (in_session.getCreationTime() == 0L) {
                return "";
            }
        }
        catch (IllegalStateException ise) {
            return "";
        }
        return JspHelper.secondsToTimeString(SessionUtils.getTTLForSession(in_session) / 1000L);
    }

    public static String getDisplayInactiveTimeForSession(Session in_session) {
        try {
            if (in_session.getCreationTime() == 0L) {
                return "";
            }
        }
        catch (IllegalStateException ise) {
            return "";
        }
        return JspHelper.secondsToTimeString(SessionUtils.getInactiveTimeForSession(in_session) / 1000L);
    }

    public static String secondsToTimeString(long in_seconds) {
        StringBuilder buff = new StringBuilder(9);
        if (in_seconds < 0L) {
            buff.append('-');
            in_seconds = -in_seconds;
        }
        long rest = in_seconds;
        long hour = rest / 3600L;
        long minute = (rest %= 3600L) / 60L;
        long second = rest %= 60L;
        if (hour < 10L) {
            buff.append('0');
        }
        buff.append(hour);
        buff.append(':');
        if (minute < 10L) {
            buff.append('0');
        }
        buff.append(minute);
        buff.append(':');
        if (second < 10L) {
            buff.append('0');
        }
        buff.append(second);
        return buff.toString();
    }

    public static String escapeXml(Object obj) {
        String value = null;
        try {
            value = obj == null ? null : obj.toString();
        }
        catch (Exception exception) {
            // empty catch block
        }
        return JspHelper.escapeXml(value);
    }

    public static String escapeXml(String buffer) {
        if (buffer == null) {
            return "";
        }
        return Escape.xml((String)buffer);
    }

    public static String formatNumber(long number) {
        return NumberFormat.getNumberInstance().format(number);
    }

    static {
        JspHelper.specialCharactersRepresentation[38] = "&amp;".toCharArray();
        JspHelper.specialCharactersRepresentation[60] = "&lt;".toCharArray();
        JspHelper.specialCharactersRepresentation[62] = "&gt;".toCharArray();
        JspHelper.specialCharactersRepresentation[34] = "&#034;".toCharArray();
        JspHelper.specialCharactersRepresentation[39] = "&#039;".toCharArray();
    }
}

