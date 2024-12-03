/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.filter;

import org.bedework.caldav.util.TimeRange;
import org.bedework.caldav.util.filter.ObjectFilter;
import org.bedework.util.calendar.PropertyIndex;

public class EntityTimeRangeFilter
extends ObjectFilter<TimeRange> {
    private int entityType;

    public EntityTimeRangeFilter(String name, int entityType, TimeRange tr) {
        super(name, PropertyIndex.PropertyInfoIndex.ENTITY_TYPE);
        this.entityType = entityType;
        this.setEntity(tr);
    }

    public int getEntityType() {
        return this.entityType;
    }
}

