/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.hibernate.type.LocalDateTimeType;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;

public class LocalDateTimeJavaDescriptor
extends AbstractTypeDescriptor<LocalDateTime> {
    public static final LocalDateTimeJavaDescriptor INSTANCE = new LocalDateTimeJavaDescriptor();

    public LocalDateTimeJavaDescriptor() {
        super(LocalDateTime.class, ImmutableMutabilityPlan.INSTANCE);
    }

    @Override
    public String toString(LocalDateTime value) {
        return LocalDateTimeType.FORMATTER.format(value);
    }

    @Override
    public LocalDateTime fromString(String string) {
        return LocalDateTime.from(LocalDateTimeType.FORMATTER.parse(string));
    }

    @Override
    public <X> X unwrap(LocalDateTime value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (LocalDateTime.class.isAssignableFrom(type)) {
            return (X)value;
        }
        if (Timestamp.class.isAssignableFrom(type)) {
            return (X)Timestamp.valueOf(value);
        }
        if (java.sql.Date.class.isAssignableFrom(type)) {
            Instant instant = value.atZone(ZoneId.systemDefault()).toInstant();
            return (X)java.sql.Date.from(instant);
        }
        if (Time.class.isAssignableFrom(type)) {
            Instant instant = value.atZone(ZoneId.systemDefault()).toInstant();
            return (X)Time.from(instant);
        }
        if (Date.class.isAssignableFrom(type)) {
            Instant instant = value.atZone(ZoneId.systemDefault()).toInstant();
            return (X)Date.from(instant);
        }
        if (Calendar.class.isAssignableFrom(type)) {
            return (X)GregorianCalendar.from(value.atZone(ZoneId.systemDefault()));
        }
        if (Long.class.isAssignableFrom(type)) {
            Instant instant = value.atZone(ZoneId.systemDefault()).toInstant();
            return (X)Long.valueOf(instant.toEpochMilli());
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> LocalDateTime wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (LocalDateTime.class.isInstance(value)) {
            return (LocalDateTime)value;
        }
        if (Timestamp.class.isInstance(value)) {
            Timestamp ts = (Timestamp)value;
            return ts.toLocalDateTime();
        }
        if (Long.class.isInstance(value)) {
            Instant instant = Instant.ofEpochMilli((Long)value);
            return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        }
        if (Calendar.class.isInstance(value)) {
            Calendar calendar = (Calendar)value;
            return LocalDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId());
        }
        if (Date.class.isInstance(value)) {
            Timestamp ts = (Timestamp)value;
            Instant instant = Instant.ofEpochMilli(ts.getTime());
            return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        }
        throw this.unknownWrap(value.getClass());
    }
}

