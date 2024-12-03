/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import org.hibernate.internal.util.compare.CalendarComparator;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.DateTypeDescriptor;
import org.hibernate.type.descriptor.java.MutableMutabilityPlan;

public class CalendarTypeDescriptor
extends AbstractTypeDescriptor<Calendar> {
    public static final CalendarTypeDescriptor INSTANCE = new CalendarTypeDescriptor();

    protected CalendarTypeDescriptor() {
        super(Calendar.class, CalendarMutabilityPlan.INSTANCE);
    }

    @Override
    public String toString(Calendar value) {
        return DateTypeDescriptor.INSTANCE.toString(value.getTime());
    }

    @Override
    public Calendar fromString(String string) {
        GregorianCalendar result = new GregorianCalendar();
        result.setTime(DateTypeDescriptor.INSTANCE.fromString(string));
        return result;
    }

    @Override
    public boolean areEqual(Calendar one, Calendar another) {
        if (one == another) {
            return true;
        }
        if (one == null || another == null) {
            return false;
        }
        return one.get(14) == another.get(14) && one.get(13) == another.get(13) && one.get(12) == another.get(12) && one.get(11) == another.get(11) && one.get(5) == another.get(5) && one.get(2) == another.get(2) && one.get(1) == another.get(1);
    }

    @Override
    public int extractHashCode(Calendar value) {
        int hashCode = 1;
        hashCode = 31 * hashCode + value.get(14);
        hashCode = 31 * hashCode + value.get(13);
        hashCode = 31 * hashCode + value.get(12);
        hashCode = 31 * hashCode + value.get(11);
        hashCode = 31 * hashCode + value.get(5);
        hashCode = 31 * hashCode + value.get(2);
        hashCode = 31 * hashCode + value.get(1);
        return hashCode;
    }

    @Override
    public Comparator<Calendar> getComparator() {
        return CalendarComparator.INSTANCE;
    }

    @Override
    public <X> X unwrap(Calendar value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Calendar.class.isAssignableFrom(type)) {
            return (X)value;
        }
        if (java.sql.Date.class.isAssignableFrom(type)) {
            return (X)new java.sql.Date(value.getTimeInMillis());
        }
        if (Time.class.isAssignableFrom(type)) {
            return (X)new Time(value.getTimeInMillis());
        }
        if (Timestamp.class.isAssignableFrom(type)) {
            return (X)new Timestamp(value.getTimeInMillis());
        }
        if (Date.class.isAssignableFrom(type)) {
            return (X)new Date(value.getTimeInMillis());
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> Calendar wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Calendar.class.isInstance(value)) {
            return (Calendar)value;
        }
        if (!Date.class.isInstance(value)) {
            throw this.unknownWrap(value.getClass());
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime((Date)value);
        return cal;
    }

    public static class CalendarMutabilityPlan
    extends MutableMutabilityPlan<Calendar> {
        public static final CalendarMutabilityPlan INSTANCE = new CalendarMutabilityPlan();

        @Override
        public Calendar deepCopyNotNull(Calendar value) {
            return (Calendar)value.clone();
        }
    }
}

