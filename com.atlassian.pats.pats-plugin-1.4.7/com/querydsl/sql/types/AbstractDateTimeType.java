/*
 * Decompiled with CFR 0.152.
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
        dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        timeFormatter = DateTimeFormat.forPattern("HH:mm:ss");
    }
}

