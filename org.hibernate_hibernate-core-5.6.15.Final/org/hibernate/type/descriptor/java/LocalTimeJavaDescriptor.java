/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.hibernate.type.LocalTimeType;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;

public class LocalTimeJavaDescriptor
extends AbstractTypeDescriptor<LocalTime> {
    public static final LocalTimeJavaDescriptor INSTANCE = new LocalTimeJavaDescriptor();

    public LocalTimeJavaDescriptor() {
        super(LocalTime.class, ImmutableMutabilityPlan.INSTANCE);
    }

    @Override
    public String toString(LocalTime value) {
        return LocalTimeType.FORMATTER.format(value);
    }

    @Override
    public LocalTime fromString(String string) {
        return LocalTime.from(LocalTimeType.FORMATTER.parse(string));
    }

    @Override
    public <X> X unwrap(LocalTime value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (LocalTime.class.isAssignableFrom(type)) {
            return (X)value;
        }
        if (Time.class.isAssignableFrom(type)) {
            return (X)Time.valueOf(value);
        }
        ZonedDateTime zonedDateTime = value.atDate(LocalDate.of(1970, 1, 1)).atZone(ZoneId.systemDefault());
        if (Calendar.class.isAssignableFrom(type)) {
            return (X)GregorianCalendar.from(zonedDateTime);
        }
        Instant instant = zonedDateTime.toInstant();
        if (Timestamp.class.isAssignableFrom(type)) {
            return (X)Timestamp.from(instant);
        }
        if (Date.class.equals(type)) {
            return (X)Date.from(instant);
        }
        if (Long.class.isAssignableFrom(type)) {
            return (X)Long.valueOf(instant.toEpochMilli());
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> LocalTime wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (LocalTime.class.isInstance(value)) {
            return (LocalTime)value;
        }
        if (Timestamp.class.isInstance(value)) {
            Timestamp ts = (Timestamp)value;
            return LocalDateTime.ofInstant(ts.toInstant(), ZoneId.systemDefault()).toLocalTime();
        }
        if (Long.class.isInstance(value)) {
            Instant instant = Instant.ofEpochMilli((Long)value);
            return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalTime();
        }
        if (Calendar.class.isInstance(value)) {
            Calendar calendar = (Calendar)value;
            return LocalDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId()).toLocalTime();
        }
        if (Date.class.isInstance(value)) {
            Date ts = (Date)value;
            Instant instant = Instant.ofEpochMilli(ts.getTime());
            return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalTime();
        }
        throw this.unknownWrap(value.getClass());
    }
}

