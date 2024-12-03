/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.hibernate.type.ZonedDateTimeType;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;

public class ZonedDateTimeJavaDescriptor
extends AbstractTypeDescriptor<ZonedDateTime> {
    public static final ZonedDateTimeJavaDescriptor INSTANCE = new ZonedDateTimeJavaDescriptor();

    public ZonedDateTimeJavaDescriptor() {
        super(ZonedDateTime.class, ImmutableMutabilityPlan.INSTANCE);
    }

    @Override
    public String toString(ZonedDateTime value) {
        return ZonedDateTimeType.FORMATTER.format(value);
    }

    @Override
    public ZonedDateTime fromString(String string) {
        return ZonedDateTime.from(ZonedDateTimeType.FORMATTER.parse(string));
    }

    @Override
    public <X> X unwrap(ZonedDateTime zonedDateTime, Class<X> type, WrapperOptions options) {
        if (zonedDateTime == null) {
            return null;
        }
        if (ZonedDateTime.class.isAssignableFrom(type)) {
            return (X)zonedDateTime;
        }
        if (Calendar.class.isAssignableFrom(type)) {
            return (X)GregorianCalendar.from(zonedDateTime);
        }
        if (Timestamp.class.isAssignableFrom(type)) {
            if (zonedDateTime.getYear() < 1905) {
                return (X)Timestamp.valueOf(zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
            }
            return (X)Timestamp.from(zonedDateTime.toInstant());
        }
        if (java.sql.Date.class.isAssignableFrom(type)) {
            return (X)java.sql.Date.from(zonedDateTime.toInstant());
        }
        if (Time.class.isAssignableFrom(type)) {
            return (X)Time.from(zonedDateTime.toInstant());
        }
        if (Date.class.isAssignableFrom(type)) {
            return (X)Date.from(zonedDateTime.toInstant());
        }
        if (Long.class.isAssignableFrom(type)) {
            return (X)Long.valueOf(zonedDateTime.toInstant().toEpochMilli());
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> ZonedDateTime wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (ZonedDateTime.class.isInstance(value)) {
            return (ZonedDateTime)value;
        }
        if (Timestamp.class.isInstance(value)) {
            Timestamp ts = (Timestamp)value;
            if (ts.getYear() < 5) {
                return ts.toLocalDateTime().atZone(ZoneId.systemDefault());
            }
            return ts.toInstant().atZone(ZoneId.systemDefault());
        }
        if (Date.class.isInstance(value)) {
            Date date = (Date)value;
            return ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        }
        if (Long.class.isInstance(value)) {
            return ZonedDateTime.ofInstant(Instant.ofEpochMilli((Long)value), ZoneId.systemDefault());
        }
        if (Calendar.class.isInstance(value)) {
            Calendar calendar = (Calendar)value;
            return ZonedDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId());
        }
        throw this.unknownWrap(value.getClass());
    }
}

