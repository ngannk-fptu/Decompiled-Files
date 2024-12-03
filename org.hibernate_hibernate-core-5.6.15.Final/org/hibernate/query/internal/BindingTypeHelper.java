/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.TemporalType
 */
package org.hibernate.query.internal;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.TemporalType;
import org.hibernate.type.BasicType;
import org.hibernate.type.CalendarDateType;
import org.hibernate.type.CalendarTimeType;
import org.hibernate.type.CalendarType;
import org.hibernate.type.InstantType;
import org.hibernate.type.OffsetDateTimeType;
import org.hibernate.type.OffsetTimeType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;
import org.hibernate.type.ZonedDateTimeType;

public class BindingTypeHelper {
    public static final BindingTypeHelper INSTANCE = new BindingTypeHelper();

    private BindingTypeHelper() {
    }

    public BasicType determineTypeForTemporalType(TemporalType temporalType, Type baseType, Object bindValue) {
        Class<Object> javaType = bindValue != null ? bindValue.getClass() : (baseType != null ? baseType.getReturnedClass() : Timestamp.class);
        switch (temporalType) {
            case TIMESTAMP: {
                return this.resolveTimestampTemporalTypeVariant(javaType, baseType);
            }
            case DATE: {
                return this.resolveDateTemporalTypeVariant(javaType, baseType);
            }
            case TIME: {
                return this.resolveTimeTemporalTypeVariant(javaType, baseType);
            }
        }
        throw new IllegalArgumentException("Unexpected TemporalType [" + temporalType + "]; expecting TIMESTAMP, DATE or TIME");
    }

    public BasicType resolveTimestampTemporalTypeVariant(Class javaType, Type baseType) {
        if (baseType != null && baseType instanceof BasicType) {
            return (BasicType)baseType;
        }
        if (Calendar.class.isAssignableFrom(javaType)) {
            return CalendarType.INSTANCE;
        }
        if (Date.class.isAssignableFrom(javaType)) {
            return TimestampType.INSTANCE;
        }
        if (Instant.class.isAssignableFrom(javaType)) {
            return InstantType.INSTANCE;
        }
        if (OffsetDateTime.class.isAssignableFrom(javaType)) {
            return OffsetDateTimeType.INSTANCE;
        }
        if (ZonedDateTime.class.isAssignableFrom(javaType)) {
            return ZonedDateTimeType.INSTANCE;
        }
        if (OffsetTime.class.isAssignableFrom(javaType)) {
            return OffsetTimeType.INSTANCE;
        }
        throw new IllegalArgumentException("Unsure how to handle given Java type [" + javaType.getName() + "] as TemporalType#TIMESTAMP");
    }

    public BasicType resolveDateTemporalTypeVariant(Class javaType, Type baseType) {
        if (baseType != null && baseType instanceof BasicType && baseType.getReturnedClass().isAssignableFrom(javaType)) {
            return (BasicType)baseType;
        }
        if (Calendar.class.isAssignableFrom(javaType)) {
            return CalendarDateType.INSTANCE;
        }
        if (Date.class.isAssignableFrom(javaType)) {
            return TimestampType.INSTANCE;
        }
        if (Instant.class.isAssignableFrom(javaType)) {
            return OffsetDateTimeType.INSTANCE;
        }
        if (OffsetDateTime.class.isAssignableFrom(javaType)) {
            return OffsetDateTimeType.INSTANCE;
        }
        if (ZonedDateTime.class.isAssignableFrom(javaType)) {
            return ZonedDateTimeType.INSTANCE;
        }
        throw new IllegalArgumentException("Unsure how to handle given Java type [" + javaType.getName() + "] as TemporalType#DATE");
    }

    public BasicType resolveTimeTemporalTypeVariant(Class javaType, Type baseType) {
        if (Calendar.class.isAssignableFrom(javaType)) {
            return CalendarTimeType.INSTANCE;
        }
        if (Date.class.isAssignableFrom(javaType)) {
            return TimestampType.INSTANCE;
        }
        throw new IllegalArgumentException("Unsure how to handle given Java type [" + javaType.getName() + "] as TemporalType#TIME");
    }
}

