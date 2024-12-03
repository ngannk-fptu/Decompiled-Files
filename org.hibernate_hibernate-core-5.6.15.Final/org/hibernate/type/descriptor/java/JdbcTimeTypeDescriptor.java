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

public class JdbcTimeTypeDescriptor
extends AbstractTypeDescriptor<Date> {
    public static final JdbcTimeTypeDescriptor INSTANCE = new JdbcTimeTypeDescriptor();
    public static final String TIME_FORMAT = "HH:mm:ss.SSS";

    public JdbcTimeTypeDescriptor() {
        super(Date.class, TimeMutabilityPlan.INSTANCE);
    }

    @Override
    public String toString(Date value) {
        return new SimpleDateFormat(TIME_FORMAT).format(value);
    }

    @Override
    public Date fromString(String string) {
        try {
            return new Time(new SimpleDateFormat(TIME_FORMAT).parse(string).getTime());
        }
        catch (ParseException pe) {
            throw new HibernateException("could not parse time string" + string, pe);
        }
    }

    @Override
    public int extractHashCode(Date value) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(value);
        int hashCode = 1;
        hashCode = 31 * hashCode + calendar.get(11);
        hashCode = 31 * hashCode + calendar.get(12);
        hashCode = 31 * hashCode + calendar.get(13);
        hashCode = 31 * hashCode + calendar.get(14);
        return hashCode;
    }

    @Override
    public boolean areEqual(Date one, Date another) {
        if (one == another) {
            return true;
        }
        if (one == null || another == null) {
            return false;
        }
        if (one.getTime() == another.getTime()) {
            return true;
        }
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(one);
        calendar2.setTime(another);
        return calendar1.get(11) == calendar2.get(11) && calendar1.get(12) == calendar2.get(12) && calendar1.get(13) == calendar2.get(13) && calendar1.get(14) == calendar2.get(14);
    }

    @Override
    public <X> X unwrap(Date value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Time.class.isAssignableFrom(type)) {
            Time rtn = Time.class.isInstance(value) ? (Time)value : new Time(value.getTime());
            return (X)rtn;
        }
        if (java.sql.Date.class.isAssignableFrom(type)) {
            java.sql.Date rtn = java.sql.Date.class.isInstance(value) ? (java.sql.Date)value : new java.sql.Date(value.getTime());
            return (X)rtn;
        }
        if (Timestamp.class.isAssignableFrom(type)) {
            Timestamp rtn = Timestamp.class.isInstance(value) ? (Timestamp)value : new Timestamp(value.getTime());
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
        if (Time.class.isInstance(value)) {
            return (Time)value;
        }
        if (Long.class.isInstance(value)) {
            return new Time((Long)value);
        }
        if (Calendar.class.isInstance(value)) {
            return new Time(((Calendar)value).getTimeInMillis());
        }
        if (Date.class.isInstance(value)) {
            return new Time(((Date)value).getTime());
        }
        throw this.unknownWrap(value.getClass());
    }

    public static class TimeMutabilityPlan
    extends MutableMutabilityPlan<Date> {
        public static final TimeMutabilityPlan INSTANCE = new TimeMutabilityPlan();

        @Override
        public Date deepCopyNotNull(Date value) {
            return Time.class.isInstance(value) ? new Time(value.getTime()) : new Date(value.getTime());
        }
    }
}

