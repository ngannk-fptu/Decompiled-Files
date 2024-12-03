/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.util.Calendar;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.CalendarTimeTypeDescriptor;
import org.hibernate.type.descriptor.sql.TimeTypeDescriptor;

public class CalendarTimeType
extends AbstractSingleColumnStandardBasicType<Calendar> {
    public static final CalendarTimeType INSTANCE = new CalendarTimeType();

    public CalendarTimeType() {
        super(TimeTypeDescriptor.INSTANCE, CalendarTimeTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "calendar_time";
    }
}

