/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.core.util;

import com.thoughtworks.xstream.core.util.Pool;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ThreadSafeSimpleDateFormat {
    private final String formatString;
    private final Pool pool;
    private final TimeZone timeZone;

    public ThreadSafeSimpleDateFormat(String format, TimeZone timeZone, int initialPoolSize, int maxPoolSize, boolean lenient) {
        this(format, timeZone, Locale.ENGLISH, initialPoolSize, maxPoolSize, lenient);
    }

    public ThreadSafeSimpleDateFormat(String format, TimeZone timeZone, final Locale locale, int initialPoolSize, int maxPoolSize, final boolean lenient) {
        this.formatString = format;
        this.timeZone = timeZone;
        this.pool = new Pool(initialPoolSize, maxPoolSize, new Pool.Factory(){

            public Object newInstance() {
                SimpleDateFormat dateFormat = new SimpleDateFormat(ThreadSafeSimpleDateFormat.this.formatString, locale);
                dateFormat.setLenient(lenient);
                return dateFormat;
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String format(Date date) {
        DateFormat format = this.fetchFromPool();
        try {
            String string = format.format(date);
            return string;
        }
        finally {
            this.pool.putInPool(format);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Date parse(String date) throws ParseException {
        DateFormat format = this.fetchFromPool();
        try {
            Date date2 = format.parse(date);
            return date2;
        }
        finally {
            this.pool.putInPool(format);
        }
    }

    private DateFormat fetchFromPool() {
        TimeZone tz;
        DateFormat format = (DateFormat)this.pool.fetchFromPool();
        TimeZone timeZone = tz = this.timeZone != null ? this.timeZone : TimeZone.getDefault();
        if (!tz.equals(format.getTimeZone())) {
            format.setTimeZone(tz);
        }
        return format;
    }

    public String toString() {
        return this.formatString;
    }
}

