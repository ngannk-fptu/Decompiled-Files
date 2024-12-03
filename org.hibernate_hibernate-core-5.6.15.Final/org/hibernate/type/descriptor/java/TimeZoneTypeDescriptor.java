/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import java.util.Comparator;
import java.util.TimeZone;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;

public class TimeZoneTypeDescriptor
extends AbstractTypeDescriptor<TimeZone> {
    public static final TimeZoneTypeDescriptor INSTANCE = new TimeZoneTypeDescriptor();

    public TimeZoneTypeDescriptor() {
        super(TimeZone.class);
    }

    @Override
    public String toString(TimeZone value) {
        return value.getID();
    }

    @Override
    public TimeZone fromString(String string) {
        return TimeZone.getTimeZone(string);
    }

    @Override
    public Comparator<TimeZone> getComparator() {
        return TimeZoneComparator.INSTANCE;
    }

    @Override
    public <X> X unwrap(TimeZone value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (String.class.isAssignableFrom(type)) {
            return (X)this.toString(value);
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> TimeZone wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (String.class.isInstance(value)) {
            return this.fromString((String)value);
        }
        throw this.unknownWrap(value.getClass());
    }

    public static class TimeZoneComparator
    implements Comparator<TimeZone> {
        public static final TimeZoneComparator INSTANCE = new TimeZoneComparator();

        @Override
        public int compare(TimeZone o1, TimeZone o2) {
            return o1.getID().compareTo(o2.getID());
        }
    }
}

