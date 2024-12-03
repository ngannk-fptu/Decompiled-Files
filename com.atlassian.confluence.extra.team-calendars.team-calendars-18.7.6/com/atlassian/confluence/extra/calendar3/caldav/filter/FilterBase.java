/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.caldav.filter;

import com.atlassian.confluence.extra.calendar3.caldav.filter.AndFilter;
import com.atlassian.confluence.extra.calendar3.caldav.filter.EntityTimeRangeFilter;
import com.atlassian.confluence.extra.calendar3.caldav.filter.EntityTypeFilter;
import com.atlassian.confluence.extra.calendar3.caldav.filter.FilterType;
import com.atlassian.confluence.extra.calendar3.caldav.filter.NotFilter;
import com.atlassian.confluence.extra.calendar3.caldav.filter.OrFilter;
import com.atlassian.confluence.extra.calendar3.caldav.filter.PresenceFilterAbstract;
import com.atlassian.confluence.extra.calendar3.caldav.filter.PropertyValueFilter;
import com.atlassian.confluence.extra.calendar3.caldav.filter.TimeRangeFilter;
import java.util.ArrayList;
import java.util.List;
import org.bedework.caldav.util.TimeRange;
import org.bedework.caldav.util.filter.ObjectFilter;
import org.bedework.caldav.util.filter.PresenceFilter;
import org.bedework.util.calendar.IcalDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FilterBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(FilterBase.class);
    protected String name;
    protected List<FilterBase> children;
    protected FilterType type;

    public FilterBase(String name) {
        this.name = name;
        this.children = new ArrayList<FilterBase>();
    }

    public FilterType getType() {
        return this.type;
    }

    public void setType(FilterType type) {
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FilterBase> getChildren() {
        return this.children;
    }

    public void addChild(FilterBase val) {
        if (val == null) {
            return;
        }
        List<FilterBase> c = this.getChildren();
        c.add(val);
    }

    public static FilterBase convert(org.bedework.caldav.util.filter.FilterBase filterBase) {
        boolean isAndFilter = filterBase instanceof org.bedework.caldav.util.filter.AndFilter;
        boolean isOrFilter = filterBase instanceof org.bedework.caldav.util.filter.OrFilter;
        boolean isNotFilter = filterBase instanceof org.bedework.caldav.util.filter.NotFilter;
        if (isAndFilter || isOrFilter || isNotFilter) {
            FilterBase returnFilter = null;
            returnFilter = isAndFilter & returnFilter == null ? new AndFilter() : null;
            returnFilter = isOrFilter & returnFilter == null ? new OrFilter() : null;
            returnFilter = isNotFilter & returnFilter == null ? new NotFilter() : null;
            for (org.bedework.caldav.util.filter.FilterBase child : filterBase.getChildren()) {
                FilterBase childFilter = FilterBase.convert(child);
                if (childFilter == null) continue;
                returnFilter.addChild(childFilter);
            }
            return returnFilter;
        }
        if (filterBase instanceof org.bedework.caldav.util.filter.EntityTimeRangeFilter) {
            org.bedework.caldav.util.filter.EntityTimeRangeFilter entityTimeRangeFilter = (org.bedework.caldav.util.filter.EntityTimeRangeFilter)filterBase;
            EntityTimeRangeFilter returnFilter = new EntityTimeRangeFilter((TimeRange)entityTimeRangeFilter.getEntity());
            return returnFilter;
        }
        if (filterBase instanceof org.bedework.caldav.util.filter.TimeRangeFilter) {
            org.bedework.caldav.util.filter.TimeRangeFilter entityTimeRangeFilter = (org.bedework.caldav.util.filter.TimeRangeFilter)filterBase;
            TimeRangeFilter returnFilter = new TimeRangeFilter(entityTimeRangeFilter.getPropertyIndex(), (TimeRange)entityTimeRangeFilter.getEntity());
            return returnFilter;
        }
        if (filterBase instanceof org.bedework.caldav.util.filter.EntityTypeFilter) {
            org.bedework.caldav.util.filter.EntityTypeFilter entityTypeFilter = (org.bedework.caldav.util.filter.EntityTypeFilter)filterBase;
            EntityTypeFilter returnFilter = new EntityTypeFilter(IcalDefs.fromEntityType((Integer)entityTypeFilter.getEntity()));
            return returnFilter;
        }
        if (filterBase instanceof PresenceFilter) {
            PresenceFilter presenceFilter = (PresenceFilter)filterBase;
            PresenceFilterAbstract returnFilter = new PresenceFilterAbstract(presenceFilter.getPropertyIndex(), presenceFilter.getTestPresent());
            return returnFilter;
        }
        if (filterBase instanceof ObjectFilter) {
            ObjectFilter objectFilter = (ObjectFilter)filterBase;
            PropertyValueFilter returnFilter = new PropertyValueFilter(objectFilter.getName(), objectFilter.getPropertyIndex(), objectFilter.getEntity());
            return returnFilter;
        }
        LOGGER.warn("Could not convert class {} ", (Object)filterBase);
        return null;
    }

    protected abstract FilterBase clone();

    public String toString() {
        StringBuilder sb = new StringBuilder(this.name).append(" {");
        List<FilterBase> c = this.getChildren();
        if (c != null) {
            for (FilterBase f : c) {
                sb.append("\n");
                sb.append(f);
            }
        }
        return sb.toString();
    }
}

