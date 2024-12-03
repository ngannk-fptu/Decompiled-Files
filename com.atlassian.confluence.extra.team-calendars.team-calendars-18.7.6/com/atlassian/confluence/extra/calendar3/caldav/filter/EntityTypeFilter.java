/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.caldav.filter;

import com.atlassian.confluence.extra.calendar3.caldav.filter.PropertyValueFilter;
import org.bedework.util.calendar.PropertyIndex;

public class EntityTypeFilter
extends PropertyValueFilter<String> {
    public EntityTypeFilter(String type) {
        super("EntityTypeFilter", PropertyIndex.PropertyInfoIndex.ENTITY_TYPE, type);
    }
}

