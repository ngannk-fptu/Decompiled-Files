/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.hibernate.type.OffsetTimeType;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;

public class OffsetTimeJavaDescriptor
extends AbstractTypeDescriptor<OffsetTime> {
    public static final OffsetTimeJavaDescriptor INSTANCE = new OffsetTimeJavaDescriptor();

    public OffsetTimeJavaDescriptor() {
        super(OffsetTime.class, ImmutableMutabilityPlan.INSTANCE);
    }

    @Override
    public String toString(OffsetTime value) {
        return OffsetTimeType.FORMATTER.format(value);
    }

    @Override
    public OffsetTime fromString(String string) {
        return OffsetTime.from(OffsetTimeType.FORMATTER.parse(string));
    }

    @Override
    public <X> X unwrap(OffsetTime offsetTime, Class<X> type, WrapperOptions options) {
        if (offsetTime == null) {
            return null;
        }
        if (OffsetTime.class.isAssignableFrom(type)) {
            return (X)offsetTime;
        }
        if (Time.class.isAssignableFrom(type)) {
            return (X)Time.valueOf(offsetTime.toLocalTime());
        }
        ZonedDateTime zonedDateTime = offsetTime.atDate(LocalDate.of(1970, 1, 1)).toZonedDateTime();
        if (Timestamp.class.isAssignableFrom(type)) {
            return (X)Timestamp.valueOf(zonedDateTime.toLocalDateTime());
        }
        if (Calendar.class.isAssignableFrom(type)) {
            return (X)GregorianCalendar.from(zonedDateTime);
        }
        Instant instant = zonedDateTime.toInstant();
        if (Long.class.isAssignableFrom(type)) {
            return (X)Long.valueOf(instant.toEpochMilli());
        }
        if (Date.class.isAssignableFrom(type)) {
            return (X)Date.from(instant);
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> OffsetTime wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (OffsetTime.class.isInstance(value)) {
            return (OffsetTime)value;
        }
        ZoneOffset offset = OffsetDateTime.now().getOffset();
        if (Time.class.isInstance(value)) {
            return ((Time)value).toLocalTime().atOffset(offset);
        }
        if (Timestamp.class.isInstance(value)) {
            Timestamp ts = (Timestamp)value;
            return ts.toLocalDateTime().toLocalTime().atOffset(offset);
        }
        if (Date.class.isInstance(value)) {
            Date date = (Date)value;
            return OffsetTime.ofInstant(date.toInstant(), offset);
        }
        if (Long.class.isInstance(value)) {
            return OffsetTime.ofInstant(Instant.ofEpochMilli((Long)value), offset);
        }
        if (Calendar.class.isInstance(value)) {
            Calendar calendar = (Calendar)value;
            return OffsetTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId());
        }
        throw this.unknownWrap(value.getClass());
    }
}

