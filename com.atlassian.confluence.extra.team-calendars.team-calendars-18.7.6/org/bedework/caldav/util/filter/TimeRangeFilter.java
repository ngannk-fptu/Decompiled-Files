/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.filter;

import java.util.List;
import org.bedework.caldav.util.TimeRange;
import org.bedework.caldav.util.filter.ObjectFilter;
import org.bedework.util.calendar.PropertyIndex;

public class TimeRangeFilter
extends ObjectFilter<TimeRange> {
    public TimeRangeFilter(String name, PropertyIndex.PropertyInfoIndex propertyIndex, Integer intKey, String strKey) {
        super(name, propertyIndex, intKey, strKey);
    }

    public TimeRangeFilter(String name, List<PropertyIndex.PropertyInfoIndex> propertyIndexes, Integer intKey, String strKey) {
        super(name, propertyIndexes, intKey, strKey);
    }
}

