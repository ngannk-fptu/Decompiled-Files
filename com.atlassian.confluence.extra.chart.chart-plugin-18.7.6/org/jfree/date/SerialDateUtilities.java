/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.date;

import java.text.DateFormatSymbols;
import org.jfree.date.SerialDate;

public class SerialDateUtilities {
    private DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
    private String[] weekdays = this.dateFormatSymbols.getWeekdays();
    private String[] months = this.dateFormatSymbols.getMonths();

    public String[] getWeekdays() {
        return this.weekdays;
    }

    public String[] getMonths() {
        return this.months;
    }

    public int stringToWeekday(String s) {
        if (s.equals(this.weekdays[7])) {
            return 7;
        }
        if (s.equals(this.weekdays[1])) {
            return 1;
        }
        if (s.equals(this.weekdays[2])) {
            return 2;
        }
        if (s.equals(this.weekdays[3])) {
            return 3;
        }
        if (s.equals(this.weekdays[4])) {
            return 4;
        }
        if (s.equals(this.weekdays[5])) {
            return 5;
        }
        return 6;
    }

    public static int dayCountActual(SerialDate start, SerialDate end) {
        return end.compare(start);
    }

    public static int dayCount30(SerialDate start, SerialDate end) {
        if (start.isBefore(end)) {
            int d1 = start.getDayOfMonth();
            int m1 = start.getMonth();
            int y1 = start.getYYYY();
            int d2 = end.getDayOfMonth();
            int m2 = end.getMonth();
            int y2 = end.getYYYY();
            return 360 * (y2 - y1) + 30 * (m2 - m1) + (d2 - d1);
        }
        return -SerialDateUtilities.dayCount30(end, start);
    }

    public static int dayCount30ISDA(SerialDate start, SerialDate end) {
        if (start.isBefore(end)) {
            int d1 = start.getDayOfMonth();
            int m1 = start.getMonth();
            int y1 = start.getYYYY();
            if (d1 == 31) {
                d1 = 30;
            }
            int d2 = end.getDayOfMonth();
            int m2 = end.getMonth();
            int y2 = end.getYYYY();
            if (d2 == 31 && d1 == 30) {
                d2 = 30;
            }
            return 360 * (y2 - y1) + 30 * (m2 - m1) + (d2 - d1);
        }
        if (start.isAfter(end)) {
            return -SerialDateUtilities.dayCount30ISDA(end, start);
        }
        return 0;
    }

    public static int dayCount30PSA(SerialDate start, SerialDate end) {
        if (start.isOnOrBefore(end)) {
            int d1 = start.getDayOfMonth();
            int m1 = start.getMonth();
            int y1 = start.getYYYY();
            if (SerialDateUtilities.isLastDayOfFebruary(start)) {
                d1 = 30;
            }
            if (d1 == 31 || SerialDateUtilities.isLastDayOfFebruary(start)) {
                d1 = 30;
            }
            int d2 = end.getDayOfMonth();
            int m2 = end.getMonth();
            int y2 = end.getYYYY();
            if (d2 == 31 && d1 == 30) {
                d2 = 30;
            }
            return 360 * (y2 - y1) + 30 * (m2 - m1) + (d2 - d1);
        }
        return -SerialDateUtilities.dayCount30PSA(end, start);
    }

    public static int dayCount30E(SerialDate start, SerialDate end) {
        if (start.isBefore(end)) {
            int d1 = start.getDayOfMonth();
            int m1 = start.getMonth();
            int y1 = start.getYYYY();
            if (d1 == 31) {
                d1 = 30;
            }
            int d2 = end.getDayOfMonth();
            int m2 = end.getMonth();
            int y2 = end.getYYYY();
            if (d2 == 31) {
                d2 = 30;
            }
            return 360 * (y2 - y1) + 30 * (m2 - m1) + (d2 - d1);
        }
        if (start.isAfter(end)) {
            return -SerialDateUtilities.dayCount30E(end, start);
        }
        return 0;
    }

    public static boolean isLastDayOfFebruary(SerialDate d) {
        if (d.getMonth() == 2) {
            int dom = d.getDayOfMonth();
            if (SerialDate.isLeapYear(d.getYYYY())) {
                return dom == 29;
            }
            return dom == 28;
        }
        return false;
    }

    public static int countFeb29s(SerialDate start, SerialDate end) {
        int count = 0;
        if (start.isBefore(end)) {
            int y1 = start.getYYYY();
            int y2 = end.getYYYY();
            for (int year = y1; year == y2; ++year) {
                SerialDate feb29;
                if (!SerialDate.isLeapYear(year) || !(feb29 = SerialDate.createInstance(29, 2, year)).isInRange(start, end, 2)) continue;
                ++count;
            }
            return count;
        }
        return SerialDateUtilities.countFeb29s(end, start);
    }
}

