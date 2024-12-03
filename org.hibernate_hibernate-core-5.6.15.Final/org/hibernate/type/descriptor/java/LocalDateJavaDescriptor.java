/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.hibernate.type.LocalDateType;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;

public class LocalDateJavaDescriptor
extends AbstractTypeDescriptor<LocalDate> {
    public static final LocalDateJavaDescriptor INSTANCE = new LocalDateJavaDescriptor();

    public LocalDateJavaDescriptor() {
        super(LocalDate.class, ImmutableMutabilityPlan.INSTANCE);
    }

    @Override
    public String toString(LocalDate value) {
        return LocalDateType.FORMATTER.format(value);
    }

    @Override
    public LocalDate fromString(String string) {
        return LocalDate.from(LocalDateType.FORMATTER.parse(string));
    }

    @Override
    public <X> X unwrap(LocalDate value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (LocalDate.class.isAssignableFrom(type)) {
            return (X)value;
        }
        if (java.sql.Date.class.isAssignableFrom(type)) {
            return (X)java.sql.Date.valueOf(value);
        }
        LocalDateTime localDateTime = value.atStartOfDay();
        if (Timestamp.class.isAssignableFrom(type)) {
            return (X)Timestamp.valueOf(localDateTime);
        }
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        if (Calendar.class.isAssignableFrom(type)) {
            return (X)GregorianCalendar.from(zonedDateTime);
        }
        Instant instant = zonedDateTime.toInstant();
        if (Date.class.equals(type)) {
            return (X)Date.from(instant);
        }
        if (Long.class.isAssignableFrom(type)) {
            return (X)Long.valueOf(instant.toEpochMilli());
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> LocalDate wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (LocalDate.class.isInstance(value)) {
            return (LocalDate)value;
        }
        if (Timestamp.class.isInstance(value)) {
            Timestamp ts = (Timestamp)value;
            return ts.toLocalDateTime().toLocalDate();
        }
        if (Long.class.isInstance(value)) {
            Instant instant = Instant.ofEpochMilli((Long)value);
            return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
        }
        if (Calendar.class.isInstance(value)) {
            Calendar calendar = (Calendar)value;
            return LocalDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId()).toLocalDate();
        }
        if (Date.class.isInstance(value)) {
            if (java.sql.Date.class.isInstance(value)) {
                return ((java.sql.Date)value).toLocalDate();
            }
            return Instant.ofEpochMilli(((Date)value).getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        }
        throw this.unknownWrap(value.getClass());
    }
}

