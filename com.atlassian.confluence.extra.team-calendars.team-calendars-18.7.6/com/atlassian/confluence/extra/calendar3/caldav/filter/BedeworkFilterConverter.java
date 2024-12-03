/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.caldav.filter;

import com.atlassian.confluence.extra.calendar3.caldav.filter.AndFilter;
import com.atlassian.confluence.extra.calendar3.caldav.filter.EntityTimeRangeFilter;
import com.atlassian.confluence.extra.calendar3.caldav.filter.FilterBase;
import com.atlassian.confluence.extra.calendar3.caldav.filter.NotFilter;
import com.atlassian.confluence.extra.calendar3.caldav.filter.OrFilter;
import com.atlassian.confluence.extra.calendar3.caldav.filter.PresenceFilterAbstract;
import com.atlassian.confluence.extra.calendar3.caldav.filter.PropertyValueFilter;
import com.atlassian.confluence.extra.calendar3.caldav.filter.TimeRangeFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.bedework.caldav.util.TimeRange;
import org.bedework.caldav.util.filter.EntityTypeFilter;
import org.bedework.caldav.util.filter.ObjectFilter;
import org.bedework.caldav.util.filter.PresenceFilter;
import org.springframework.stereotype.Component;

@Component
public class BedeworkFilterConverter {
    private final Map<Class, Function<org.bedework.caldav.util.filter.FilterBase, FilterBase>> bedeworkFilterMapper;
    private final List<Class> bedeworkOperation = new ArrayList<Class>();

    public BedeworkFilterConverter() {
        this.bedeworkOperation.add(org.bedework.caldav.util.filter.AndFilter.class);
        this.bedeworkOperation.add(org.bedework.caldav.util.filter.OrFilter.class);
        this.bedeworkOperation.add(org.bedework.caldav.util.filter.NotFilter.class);
        this.bedeworkFilterMapper = new HashMap<Class, Function<org.bedework.caldav.util.filter.FilterBase, FilterBase>>();
        this.bedeworkFilterMapper.put(org.bedework.caldav.util.filter.AndFilter.class, bedeworkFilterBase -> new AndFilter());
        this.bedeworkFilterMapper.put(org.bedework.caldav.util.filter.OrFilter.class, bedeworkFilterBase -> new OrFilter());
        this.bedeworkFilterMapper.put(org.bedework.caldav.util.filter.NotFilter.class, bedeworkFilterBase -> new NotFilter());
        this.bedeworkFilterMapper.put(org.bedework.caldav.util.filter.EntityTimeRangeFilter.class, bedeworkFilterBase -> {
            org.bedework.caldav.util.filter.EntityTimeRangeFilter entityTimeRangeFilter = (org.bedework.caldav.util.filter.EntityTimeRangeFilter)bedeworkFilterBase;
            EntityTimeRangeFilter returnValue = new EntityTimeRangeFilter((TimeRange)entityTimeRangeFilter.getEntity());
            return returnValue;
        });
        this.bedeworkFilterMapper.put(org.bedework.caldav.util.filter.TimeRangeFilter.class, bedeworkFilterBase -> {
            org.bedework.caldav.util.filter.TimeRangeFilter entityTimeRangeFilter = (org.bedework.caldav.util.filter.TimeRangeFilter)bedeworkFilterBase;
            TimeRangeFilter returnFilter = new TimeRangeFilter(entityTimeRangeFilter.getPropertyIndex(), (TimeRange)entityTimeRangeFilter.getEntity());
            return returnFilter;
        });
        this.bedeworkFilterMapper.put(EntityTypeFilter.class, bedeworkFilterBase -> null);
        this.bedeworkFilterMapper.put(PresenceFilter.class, bedeworkFilterBase -> {
            PresenceFilter presenceFilter = (PresenceFilter)bedeworkFilterBase;
            PresenceFilterAbstract returnFilter = new PresenceFilterAbstract(presenceFilter.getPropertyIndex(), presenceFilter.getTestPresent());
            return returnFilter;
        });
        this.bedeworkFilterMapper.put(ObjectFilter.class, bedeworkFilterBase -> {
            ObjectFilter objectFilter = (ObjectFilter)bedeworkFilterBase;
            PropertyValueFilter returnFilter = new PropertyValueFilter(objectFilter.getName(), objectFilter.getPropertyIndex(), objectFilter.getEntity());
            return returnFilter;
        });
    }

    public FilterBase transform(org.bedework.caldav.util.filter.FilterBase bedeworkFilterBase) {
        FilterBase filterBase;
        if (bedeworkFilterBase == null) {
            return null;
        }
        if (this.isOperation(bedeworkFilterBase)) {
            filterBase = this.bedeworkFilterMapper.get(bedeworkFilterBase.getClass()).apply(bedeworkFilterBase);
            for (org.bedework.caldav.util.filter.FilterBase child : bedeworkFilterBase.getChildren()) {
                FilterBase childFilter = this.transform(child);
                if (childFilter == null) continue;
                filterBase.addChild(childFilter);
            }
        } else {
            Function<org.bedework.caldav.util.filter.FilterBase, FilterBase> converter = this.bedeworkFilterMapper.get(bedeworkFilterBase.getClass());
            filterBase = converter.apply(bedeworkFilterBase);
        }
        return filterBase;
    }

    private boolean isOperation(org.bedework.caldav.util.filter.FilterBase bedeworkFilterBase) {
        return this.bedeworkOperation.contains(bedeworkFilterBase.getClass());
    }
}

