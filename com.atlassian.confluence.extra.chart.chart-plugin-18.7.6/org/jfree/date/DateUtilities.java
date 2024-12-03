/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.date;

import java.util.Calendar;
import java.util.Date;

public class DateUtilities {
    private static final Calendar CALENDAR = Calendar.getInstance();

    private DateUtilities() {
    }

    public static synchronized Date createDate(int yyyy, int month, int day) {
        CALENDAR.clear();
        CALENDAR.set(yyyy, month - 1, day);
        return CALENDAR.getTime();
    }

    public static synchronized Date createDate(int yyyy, int month, int day, int hour, int min) {
        CALENDAR.clear();
        CALENDAR.set(yyyy, month - 1, day, hour, min);
        return CALENDAR.getTime();
    }
}

