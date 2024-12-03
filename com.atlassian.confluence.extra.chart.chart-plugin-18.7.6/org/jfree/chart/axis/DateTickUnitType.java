/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.axis;

import java.io.ObjectStreamException;
import java.io.Serializable;

public class DateTickUnitType
implements Serializable {
    public static final DateTickUnitType YEAR = new DateTickUnitType("DateTickUnitType.YEAR", 1);
    public static final DateTickUnitType MONTH = new DateTickUnitType("DateTickUnitType.MONTH", 2);
    public static final DateTickUnitType DAY = new DateTickUnitType("DateTickUnitType.DAY", 5);
    public static final DateTickUnitType HOUR = new DateTickUnitType("DateTickUnitType.HOUR", 11);
    public static final DateTickUnitType MINUTE = new DateTickUnitType("DateTickUnitType.MINUTE", 12);
    public static final DateTickUnitType SECOND = new DateTickUnitType("DateTickUnitType.SECOND", 13);
    public static final DateTickUnitType MILLISECOND = new DateTickUnitType("DateTickUnitType.MILLISECOND", 14);
    private String name;
    private int calendarField;

    private DateTickUnitType(String name, int calendarField) {
        this.name = name;
        this.calendarField = calendarField;
    }

    public int getCalendarField() {
        return this.calendarField;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DateTickUnitType)) {
            return false;
        }
        DateTickUnitType t = (DateTickUnitType)obj;
        return this.name.equals(t.toString());
    }

    private Object readResolve() throws ObjectStreamException {
        if (this.equals(YEAR)) {
            return YEAR;
        }
        if (this.equals(MONTH)) {
            return MONTH;
        }
        if (this.equals(DAY)) {
            return DAY;
        }
        if (this.equals(HOUR)) {
            return HOUR;
        }
        if (this.equals(MINUTE)) {
            return MINUTE;
        }
        if (this.equals(SECOND)) {
            return SECOND;
        }
        if (this.equals(MILLISECOND)) {
            return MILLISECOND;
        }
        return null;
    }
}

