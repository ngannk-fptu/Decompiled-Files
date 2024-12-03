/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.util.Calendar;
import java.util.TimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public abstract class AbstractDateTimeType<T>
extends AbstractType<T> {
    private static final Calendar UTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    protected static final DateTimeFormatter dateFormatter;
    protected static final DateTimeFormatter dateTimeFormatter;
    protected static final DateTimeFormatter timeFormatter;

    protected static Calendar utc() {
        return (Calendar)UTC.clone();
    }

    public AbstractDateTimeType(int type) {
        super(type);
    }

    static {
        UTC.setTimeInMillis(0L);
        dateFormatter = DateTimeFormat.forPattern((String)"yyyy-MM-dd");
        dateTimeFormatter = DateTimeFormat.forPattern((String)"yyyy-MM-dd HH:mm:ss");
        timeFormatter = DateTimeFormat.forPattern((String)"HH:mm:ss");
    }
}

