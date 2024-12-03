/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.caldav.filter;

import com.atlassian.confluence.extra.calendar3.caldav.filter.PropertyValueFilter;
import org.bedework.caldav.util.TimeRange;
import org.bedework.util.calendar.PropertyIndex;

public class EntityTimeRangeFilter
extends PropertyValueFilter<TimeRange> {
    public EntityTimeRangeFilter(TimeRange timeRange) {
        super("EntityTimeRangeFilter", PropertyIndex.PropertyInfoIndex.ENTITY_TYPE, timeRange);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.name).append(" {");
        sb.append(this.getEntity());
        return super.toString();
    }
}

