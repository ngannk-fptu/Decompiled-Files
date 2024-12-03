/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.DDC;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

class GregorianChange {
    static final Date PURE_CHANGE_DATE = new Date(Long.MIN_VALUE);
    static final Date STANDARD_CHANGE_DATE = new GregorianCalendar(Locale.US).getGregorianChange();
    static final int DAYS_SINCE_BASE_DATE_HINT = DDC.daysSinceBaseDate(1583, 1, 1);
    static final int EXTRA_DAYS_TO_BE_ADDED;

    private GregorianChange() {
    }

    static {
        GregorianCalendar cal = new GregorianCalendar(Locale.US);
        cal.clear();
        cal.set(1, 1, 577738, 0, 0, 0);
        EXTRA_DAYS_TO_BE_ADDED = cal.get(5) == 15 ? 2 : 0;
    }
}

