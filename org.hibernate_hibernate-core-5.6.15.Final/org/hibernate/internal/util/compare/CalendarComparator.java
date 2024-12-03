/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util.compare;

import java.util.Calendar;
import java.util.Comparator;

public class CalendarComparator
implements Comparator<Calendar> {
    public static final CalendarComparator INSTANCE = new CalendarComparator();

    @Override
    public int compare(Calendar x, Calendar y) {
        if (x.before(y)) {
            return -1;
        }
        if (x.after(y)) {
            return 1;
        }
        return 0;
    }
}

