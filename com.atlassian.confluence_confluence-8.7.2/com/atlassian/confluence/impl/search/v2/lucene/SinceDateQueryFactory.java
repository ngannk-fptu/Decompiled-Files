/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.DateUtils
 *  com.atlassian.core.util.InvalidDurationException
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.TermRangeQuery
 */
package com.atlassian.confluence.impl.search.v2.lucene;

import com.atlassian.confluence.search.v2.lucene.LuceneUtils;
import com.atlassian.core.util.DateUtils;
import com.atlassian.core.util.InvalidDurationException;
import java.util.Calendar;
import java.util.Date;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermRangeQuery;

public class SinceDateQueryFactory {
    public static final String TODAY = "today";
    public static final String YESTERDAY = "yesterday";
    public static final String LAST_WEEK = "lastweek";
    public static final String LAST_MONTH = "lastmonth";
    protected String field;
    private final String period;

    SinceDateQueryFactory(String period, String field) {
        this.period = period;
        this.field = field;
    }

    public static SinceDateQueryFactory getInstance(String period, String field) {
        return new SinceDateQueryFactory(period, field);
    }

    public Query toQuery() {
        Calendar c = Calendar.getInstance();
        c.set(11, 0);
        c.set(12, 0);
        c.set(13, 0);
        c.set(14, 0);
        Date startOfToday = c.getTime();
        if (TODAY.equals(this.period)) {
            return TermRangeQuery.newStringRange((String)this.field, (String)LuceneUtils.dateToString(startOfToday), null, (boolean)true, (boolean)true);
        }
        if (YESTERDAY.equals(this.period)) {
            c.add(6, -1);
            Date startOfYesterday = c.getTime();
            Date beforeMidnightYesterday = new Date(startOfToday.getTime() - 1L);
            return this.createQuery(startOfYesterday, beforeMidnightYesterday);
        }
        if (LAST_WEEK.equals(this.period)) {
            c.add(6, -7);
            Date lastWeekStart = c.getTime();
            return this.createQuery(lastWeekStart, null);
        }
        if (LAST_MONTH.equals(this.period)) {
            c.add(6, -30);
            Date lastMonthStart = c.getTime();
            return this.createQuery(lastMonthStart, null);
        }
        try {
            long duration = DateUtils.getDuration((String)this.period);
            return this.createQuery(new Date(new Date().getTime() - duration * 1000L), new Date());
        }
        catch (InvalidDurationException e) {
            throw new IllegalArgumentException("Invalid date period: " + this.period);
        }
    }

    private Query createQuery(Date startOfRange, Date endOfRange) {
        String lower = startOfRange != null ? LuceneUtils.dateToString(startOfRange) : null;
        String upper = endOfRange != null ? LuceneUtils.dateToString(endOfRange) : null;
        return TermRangeQuery.newStringRange((String)this.field, (String)lower, (String)upper, (boolean)true, (boolean)true);
    }
}

