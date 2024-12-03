/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.service;

import com.atlassian.confluence.search.v2.query.DateRangeQuery;
import java.util.Date;

public enum DateRangeEnum {
    LASTDAY(1L),
    LASTTWODAYS(2L),
    LASTWEEK(7L),
    LASTMONTH(31L),
    LASTSIXMONTHS(182L),
    LASTYEAR(365L),
    LASTTWOYEARS(730L);

    private final long millis;

    private DateRangeEnum(long days) {
        this.millis = days * 1000L * 60L * 60L * 24L;
    }

    public DateRangeQuery.DateRange dateRange() {
        Date startDate = new Date(System.currentTimeMillis() - this.millis);
        return new DateRangeQuery.DateRange(startDate, null, true, false);
    }
}

