/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.AbstractCalendarFormatter;
import java.util.Calendar;

public final class CalendarFormatter
extends AbstractCalendarFormatter {
    private static final CalendarFormatter theInstance = new CalendarFormatter();

    private CalendarFormatter() {
    }

    public static String format(String format, Calendar cal) {
        return theInstance.doFormat(format, cal);
    }

    protected Calendar toCalendar(Object cal) {
        return (Calendar)cal;
    }

    protected void formatYear(Object cal, StringBuffer buf) {
        int year = ((Calendar)cal).get(1);
        String s = year <= 0 ? Integer.toString(1 - year) : Integer.toString(year);
        while (s.length() < 4) {
            s = "0" + s;
        }
        if (year <= 0) {
            s = "-" + s;
        }
        buf.append(s);
    }

    protected void formatMonth(Object cal, StringBuffer buf) {
        this.formatTwoDigits(((Calendar)cal).get(2) + 1, buf);
    }

    protected void formatDays(Object cal, StringBuffer buf) {
        this.formatTwoDigits(((Calendar)cal).get(5), buf);
    }

    protected void formatHours(Object cal, StringBuffer buf) {
        this.formatTwoDigits(((Calendar)cal).get(11), buf);
    }

    protected void formatMinutes(Object cal, StringBuffer buf) {
        this.formatTwoDigits(((Calendar)cal).get(12), buf);
    }

    protected void formatSeconds(Object _cal, StringBuffer buf) {
        int n;
        Calendar cal = (Calendar)_cal;
        this.formatTwoDigits(cal.get(13), buf);
        if (cal.isSet(14) && (n = cal.get(14)) != 0) {
            String ms = Integer.toString(n);
            while (ms.length() < 3) {
                ms = "0" + ms;
            }
            buf.append('.');
            buf.append(ms);
        }
    }
}

