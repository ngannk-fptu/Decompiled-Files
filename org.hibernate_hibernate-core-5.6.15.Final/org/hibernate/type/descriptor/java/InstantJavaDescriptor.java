/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;

public class InstantJavaDescriptor
extends AbstractTypeDescriptor<Instant> {
    public static final InstantJavaDescriptor INSTANCE = new InstantJavaDescriptor();

    public InstantJavaDescriptor() {
        super(Instant.class, ImmutableMutabilityPlan.INSTANCE);
    }

    @Override
    public String toString(Instant value) {
        return DateTimeFormatter.ISO_INSTANT.format(value);
    }

    @Override
    public Instant fromString(String string) {
        return Instant.from(DateTimeFormatter.ISO_INSTANT.parse(string));
    }

    @Override
    public <X> X unwrap(Instant instant, Class<X> type, WrapperOptions options) {
        if (instant == null) {
            return null;
        }
        if (Instant.class.isAssignableFrom(type)) {
            return (X)instant;
        }
        if (Calendar.class.isAssignableFrom(type)) {
            ZoneId zoneId = ZoneId.ofOffset("UTC", ZoneOffset.UTC);
            return (X)GregorianCalendar.from(instant.atZone(zoneId));
        }
        if (Timestamp.class.isAssignableFrom(type)) {
            ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
            if (zonedDateTime.getYear() < 1905) {
                return (X)Timestamp.valueOf(zonedDateTime.toLocalDateTime());
            }
            return (X)Timestamp.from(instant);
        }
        if (java.sql.Date.class.isAssignableFrom(type)) {
            return (X)java.sql.Date.from(instant);
        }
        if (Time.class.isAssignableFrom(type)) {
            return (X)Time.from(instant);
        }
        if (Date.class.isAssignableFrom(type)) {
            return (X)Date.from(instant);
        }
        if (Long.class.isAssignableFrom(type)) {
            return (X)Long.valueOf(instant.toEpochMilli());
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> Instant wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Instant.class.isInstance(value)) {
            return (Instant)value;
        }
        if (Timestamp.class.isInstance(value)) {
            Timestamp ts = (Timestamp)value;
            if (ts.getYear() < 5) {
                return ts.toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant();
            }
            return ts.toInstant();
        }
        if (Long.class.isInstance(value)) {
            return Instant.ofEpochMilli((Long)value);
        }
        if (Calendar.class.isInstance(value)) {
            Calendar calendar = (Calendar)value;
            return ZonedDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId()).toInstant();
        }
        if (Date.class.isInstance(value)) {
            return ((Date)value).toInstant();
        }
        throw this.unknownWrap(value.getClass());
    }
}

