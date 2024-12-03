/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.hibernate.HibernateException;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.MutableMutabilityPlan;

public class JdbcTimestampTypeDescriptor
extends AbstractTypeDescriptor<Date> {
    public static final JdbcTimestampTypeDescriptor INSTANCE = new JdbcTimestampTypeDescriptor();
    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    public JdbcTimestampTypeDescriptor() {
        super(Date.class, TimestampMutabilityPlan.INSTANCE);
    }

    @Override
    public String toString(Date value) {
        return new SimpleDateFormat(TIMESTAMP_FORMAT).format(value);
    }

    @Override
    public Date fromString(String string) {
        try {
            return new Timestamp(new SimpleDateFormat(TIMESTAMP_FORMAT).parse(string).getTime());
        }
        catch (ParseException pe) {
            throw new HibernateException("could not parse timestamp string" + string, pe);
        }
    }

    @Override
    public boolean areEqual(Date one, Date another) {
        int n2;
        if (one == another) {
            return true;
        }
        if (one == null || another == null) {
            return false;
        }
        long t1 = one.getTime();
        long t2 = another.getTime();
        boolean oneIsTimestamp = Timestamp.class.isInstance(one);
        boolean anotherIsTimestamp = Timestamp.class.isInstance(another);
        int n1 = oneIsTimestamp ? ((Timestamp)one).getNanos() : 0;
        int n = n2 = anotherIsTimestamp ? ((Timestamp)another).getNanos() : 0;
        if (t1 != t2) {
            return false;
        }
        if (oneIsTimestamp && anotherIsTimestamp) {
            int nn1 = n1 % 1000000;
            int nn2 = n2 % 1000000;
            return nn1 == nn2;
        }
        return true;
    }

    @Override
    public int extractHashCode(Date value) {
        return Long.valueOf(value.getTime() / 1000L).hashCode();
    }

    @Override
    public <X> X unwrap(Date value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Timestamp.class.isAssignableFrom(type)) {
            Timestamp rtn = Timestamp.class.isInstance(value) ? (Timestamp)value : new Timestamp(value.getTime());
            return (X)rtn;
        }
        if (java.sql.Date.class.isAssignableFrom(type)) {
            java.sql.Date rtn = java.sql.Date.class.isInstance(value) ? (java.sql.Date)value : new java.sql.Date(value.getTime());
            return (X)rtn;
        }
        if (Time.class.isAssignableFrom(type)) {
            Time rtn = Time.class.isInstance(value) ? (Time)value : new Time(value.getTime());
            return (X)rtn;
        }
        if (Date.class.isAssignableFrom(type)) {
            return (X)value;
        }
        if (Calendar.class.isAssignableFrom(type)) {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTimeInMillis(value.getTime());
            return (X)cal;
        }
        if (Long.class.isAssignableFrom(type)) {
            return (X)Long.valueOf(value.getTime());
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> Date wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Timestamp.class.isInstance(value)) {
            return (Timestamp)value;
        }
        if (Long.class.isInstance(value)) {
            return new Timestamp((Long)value);
        }
        if (Calendar.class.isInstance(value)) {
            return new Timestamp(((Calendar)value).getTimeInMillis());
        }
        if (Date.class.isInstance(value)) {
            return new Timestamp(((Date)value).getTime());
        }
        throw this.unknownWrap(value.getClass());
    }

    public static class TimestampMutabilityPlan
    extends MutableMutabilityPlan<Date> {
        public static final TimestampMutabilityPlan INSTANCE = new TimestampMutabilityPlan();

        @Override
        public Date deepCopyNotNull(Date value) {
            if (value instanceof Timestamp) {
                Timestamp orig = (Timestamp)value;
                Timestamp ts = new Timestamp(orig.getTime());
                ts.setNanos(orig.getNanos());
                return ts;
            }
            return new Date(value.getTime());
        }
    }
}

