/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.hibernate.type.OffsetDateTimeType;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;

public class OffsetDateTimeJavaDescriptor
extends AbstractTypeDescriptor<OffsetDateTime> {
    public static final OffsetDateTimeJavaDescriptor INSTANCE = new OffsetDateTimeJavaDescriptor();

    public OffsetDateTimeJavaDescriptor() {
        super(OffsetDateTime.class, ImmutableMutabilityPlan.INSTANCE);
    }

    @Override
    public String toString(OffsetDateTime value) {
        return OffsetDateTimeType.FORMATTER.format(value);
    }

    @Override
    public OffsetDateTime fromString(String string) {
        return OffsetDateTime.from(OffsetDateTimeType.FORMATTER.parse(string));
    }

    @Override
    public <X> X unwrap(OffsetDateTime offsetDateTime, Class<X> type, WrapperOptions options) {
        if (offsetDateTime == null) {
            return null;
        }
        if (OffsetDateTime.class.isAssignableFrom(type)) {
            return (X)offsetDateTime;
        }
        if (Calendar.class.isAssignableFrom(type)) {
            return (X)GregorianCalendar.from(offsetDateTime.toZonedDateTime());
        }
        if (Timestamp.class.isAssignableFrom(type)) {
            if (offsetDateTime.getYear() < 1905) {
                return (X)Timestamp.valueOf(offsetDateTime.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
            }
            return (X)Timestamp.from(offsetDateTime.toInstant());
        }
        if (java.sql.Date.class.isAssignableFrom(type)) {
            return (X)java.sql.Date.from(offsetDateTime.toInstant());
        }
        if (Time.class.isAssignableFrom(type)) {
            return (X)Time.from(offsetDateTime.toInstant());
        }
        if (Date.class.isAssignableFrom(type)) {
            return (X)Date.from(offsetDateTime.toInstant());
        }
        if (Long.class.isAssignableFrom(type)) {
            return (X)Long.valueOf(offsetDateTime.toInstant().toEpochMilli());
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> OffsetDateTime wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (OffsetDateTime.class.isInstance(value)) {
            return (OffsetDateTime)value;
        }
        if (Timestamp.class.isInstance(value)) {
            Timestamp ts = (Timestamp)value;
            if (ts.getYear() < 5) {
                return ts.toLocalDateTime().atZone(ZoneId.systemDefault()).toOffsetDateTime();
            }
            return OffsetDateTime.ofInstant(ts.toInstant(), ZoneId.systemDefault());
        }
        if (Date.class.isInstance(value)) {
            Date date = (Date)value;
            return OffsetDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        }
        if (Long.class.isInstance(value)) {
            return OffsetDateTime.ofInstant(Instant.ofEpochMilli((Long)value), ZoneId.systemDefault());
        }
        if (Calendar.class.isInstance(value)) {
            Calendar calendar = (Calendar)value;
            return OffsetDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId());
        }
        throw this.unknownWrap(value.getClass());
    }
}

