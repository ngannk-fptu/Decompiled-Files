/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.util.Calendar;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.CalendarDateTypeDescriptor;
import org.hibernate.type.descriptor.sql.DateTypeDescriptor;

public class CalendarDateType
extends AbstractSingleColumnStandardBasicType<Calendar> {
    public static final CalendarDateType INSTANCE = new CalendarDateType();

    public CalendarDateType() {
        super(DateTypeDescriptor.INSTANCE, CalendarDateTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "calendar_date";
    }
}

