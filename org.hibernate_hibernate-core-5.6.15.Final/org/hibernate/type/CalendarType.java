/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.VersionType;
import org.hibernate.type.descriptor.java.CalendarTypeDescriptor;
import org.hibernate.type.descriptor.sql.TimestampTypeDescriptor;

public class CalendarType
extends AbstractSingleColumnStandardBasicType<Calendar>
implements VersionType<Calendar> {
    public static final CalendarType INSTANCE = new CalendarType();

    public CalendarType() {
        super(TimestampTypeDescriptor.INSTANCE, CalendarTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "calendar";
    }

    @Override
    public String[] getRegistrationKeys() {
        return new String[]{this.getName(), Calendar.class.getName(), GregorianCalendar.class.getName()};
    }

    @Override
    public Calendar next(Calendar current, SharedSessionContractImplementor session) {
        return this.seed(session);
    }

    @Override
    public Calendar seed(SharedSessionContractImplementor session) {
        return Calendar.getInstance();
    }

    @Override
    public Comparator<Calendar> getComparator() {
        return this.getJavaTypeDescriptor().getComparator();
    }
}

