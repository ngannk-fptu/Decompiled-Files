/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.TimeZone;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

@IgnoreJRERequirement
public abstract class AbstractJSR310DateTimeType<T extends Temporal>
extends AbstractType<T> {
    private static final Calendar UTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    protected static final DateTimeFormatter dateFormatter;
    protected static final DateTimeFormatter dateTimeFormatter;
    protected static final DateTimeFormatter timeFormatter;

    protected static Calendar utc() {
        return (Calendar)UTC.clone();
    }

    public AbstractJSR310DateTimeType(int type) {
        super(type);
    }

    static {
        UTC.setTimeInMillis(0L);
        dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    }
}

