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

public class JdbcDateTypeDescriptor
extends AbstractTypeDescriptor<Date> {
    public static final JdbcDateTypeDescriptor INSTANCE = new JdbcDateTypeDescriptor();
    public static final String DATE_FORMAT = "dd MMMM yyyy";

    public JdbcDateTypeDescriptor() {
        super(Date.class, DateMutabilityPlan.INSTANCE);
    }

    @Override
    public String toString(Date value) {
        return new SimpleDateFormat(DATE_FORMAT).format(value);
    }

    @Override
    public Date fromString(String string) {
        try {
            return new Date(new SimpleDateFormat(DATE_FORMAT).parse(string).getTime());
        }
        catch (ParseException pe) {
            throw new HibernateException("could not parse date string" + string, pe);
        }
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
        return calendar1.get(2) == calendar2.get(2) && calendar1.get(5) == calendar2.get(5) && calendar1.get(1) == calendar2.get(1);
    }

    @Override
    public int extractHashCode(Date value) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(value);
        int hashCode = 1;
        hashCode = 31 * hashCode + calendar.get(2);
        hashCode = 31 * hashCode + calendar.get(5);
        hashCode = 31 * hashCode + calendar.get(1);
        return hashCode;
    }

    @Override
    public <X> X unwrap(Date value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (java.sql.Date.class.isAssignableFrom(type)) {
            java.sql.Date rtn = java.sql.Date.class.isInstance(value) ? (java.sql.Date)value : new java.sql.Date(value.getTime());
            return (X)rtn;
        }
        if (Time.class.isAssignableFrom(type)) {
            Time rtn = Time.class.isInstance(value) ? (Time)value : new Time(value.getTime());
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
        if (java.sql.Date.class.isInstance(value)) {
            return (Date)value;
        }
        if (Long.class.isInstance(value)) {
            return new java.sql.Date((Long)value);
        }
        if (Calendar.class.isInstance(value)) {
            return new java.sql.Date(((Calendar)value).getTimeInMillis());
        }
        if (Date.class.isInstance(value)) {
            return new java.sql.Date(((Date)value).getTime());
        }
        throw this.unknownWrap(value.getClass());
    }

    public static class DateMutabilityPlan
    extends MutableMutabilityPlan<Date> {
        public static final DateMutabilityPlan INSTANCE = new DateMutabilityPlan();

        @Override
        public Date deepCopyNotNull(Date value) {
            return java.sql.Date.class.isInstance(value) ? new java.sql.Date(value.getTime()) : new Date(value.getTime());
        }
    }
}

