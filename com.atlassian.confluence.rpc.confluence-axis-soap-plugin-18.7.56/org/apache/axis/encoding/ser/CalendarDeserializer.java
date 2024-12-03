/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import javax.xml.namespace.QName;
import org.apache.axis.encoding.ser.SimpleDeserializer;
import org.apache.axis.utils.Messages;

public class CalendarDeserializer
extends SimpleDeserializer {
    private static SimpleDateFormat zulu = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    static /* synthetic */ Class class$java$util$Date;

    public CalendarDeserializer(Class javaType, QName xmlType) {
        super(javaType, xmlType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object makeValue(String source) {
        Date date;
        Calendar calendar = Calendar.getInstance();
        boolean bc = false;
        if (source == null || source.length() == 0) {
            throw new NumberFormatException(Messages.getMessage("badDateTime00"));
        }
        if (source.charAt(0) == '+') {
            source = source.substring(1);
        }
        if (source.charAt(0) == '-') {
            source = source.substring(1);
            bc = true;
        }
        if (source.length() < 19) {
            throw new NumberFormatException(Messages.getMessage("badDateTime00"));
        }
        if (source.charAt(4) != '-' || source.charAt(7) != '-' || source.charAt(10) != 'T') {
            throw new NumberFormatException(Messages.getMessage("badDate00"));
        }
        if (source.charAt(13) != ':' || source.charAt(16) != ':') {
            throw new NumberFormatException(Messages.getMessage("badTime00"));
        }
        try {
            SimpleDateFormat simpleDateFormat = zulu;
            synchronized (simpleDateFormat) {
                date = zulu.parse(source.substring(0, 19) + ".000Z");
            }
        }
        catch (Exception e) {
            throw new NumberFormatException(e.toString());
        }
        int pos = 19;
        if (pos < source.length() && source.charAt(pos) == '.') {
            int milliseconds = 0;
            int start = ++pos;
            while (pos < source.length() && Character.isDigit(source.charAt(pos))) {
                ++pos;
            }
            String decimal = source.substring(start, pos);
            if (decimal.length() == 3) {
                milliseconds = Integer.parseInt(decimal);
            } else if (decimal.length() < 3) {
                milliseconds = Integer.parseInt((decimal + "000").substring(0, 3));
            } else {
                milliseconds = Integer.parseInt(decimal.substring(0, 3));
                if (decimal.charAt(3) >= '5') {
                    ++milliseconds;
                }
            }
            date.setTime(date.getTime() + (long)milliseconds);
        }
        if (pos + 5 < source.length() && (source.charAt(pos) == '+' || source.charAt(pos) == '-')) {
            if (!(Character.isDigit(source.charAt(pos + 1)) && Character.isDigit(source.charAt(pos + 2)) && source.charAt(pos + 3) == ':' && Character.isDigit(source.charAt(pos + 4)) && Character.isDigit(source.charAt(pos + 5)))) {
                throw new NumberFormatException(Messages.getMessage("badTimezone00"));
            }
            int hours = (source.charAt(pos + 1) - 48) * 10 + source.charAt(pos + 2) - 48;
            int mins = (source.charAt(pos + 4) - 48) * 10 + source.charAt(pos + 5) - 48;
            int milliseconds = (hours * 60 + mins) * 60 * 1000;
            if (source.charAt(pos) == '+') {
                milliseconds = -milliseconds;
            }
            date.setTime(date.getTime() + (long)milliseconds);
            pos += 6;
        }
        if (pos < source.length() && source.charAt(pos) == 'Z') {
            ++pos;
            calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        }
        if (pos < source.length()) {
            throw new NumberFormatException(Messages.getMessage("badChars00"));
        }
        calendar.setTime(date);
        if (bc) {
            calendar.set(0, 0);
        }
        if (this.javaType == (class$java$util$Date == null ? (class$java$util$Date = CalendarDeserializer.class$("java.util.Date")) : class$java$util$Date)) {
            return date;
        }
        return calendar;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        zulu.setTimeZone(TimeZone.getTimeZone("GMT"));
    }
}

