/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers;

import com.atlassian.confluence.extra.calendar3.caldav.filter.EntityTimeRangeFilter;
import com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers.AbstractPropertyOperationMapper;
import com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers.PropertyToDBFieldMapperSupplier;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.NumberPath;
import org.bedework.caldav.util.TimeRange;
import org.bedework.util.calendar.PropertyIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EntityTimeRangeOperationMapper
extends AbstractPropertyOperationMapper<EntityTimeRangeFilter> {
    @Autowired
    public EntityTimeRangeOperationMapper(PropertyToDBFieldMapperSupplier propertyToDBFieldMapperSupplier) {
        super(propertyToDBFieldMapperSupplier);
    }

    @Override
    public BooleanBuilder apply(EntityTimeRangeFilter entityTimeRangeFilter) {
        Object propertyToDBFieldMapper = this.propertyToDBFieldMapperSupplier.get();
        NumberPath startField = (NumberPath)propertyToDBFieldMapper.get((Object)PropertyIndex.PropertyInfoIndex.DTSTART);
        NumberPath endField = (NumberPath)propertyToDBFieldMapper.get((Object)PropertyIndex.PropertyInfoIndex.DTEND);
        TimeRange timeRange = (TimeRange)entityTimeRangeFilter.getEntity();
        long endTimeUTC = 0L;
        long startTimeUTC = 0L;
        if (timeRange.getStart() == null && timeRange.getEnd() != null) {
            endTimeUTC = timeRange.getEnd().getTime();
            return new BooleanBuilder(startField.lt(endTimeUTC));
        }
        if (timeRange.getEnd() == null && timeRange.getStart() != null) {
            startTimeUTC = timeRange.getStart().getTime();
            return new BooleanBuilder(endField.gt(startTimeUTC));
        }
        startTimeUTC = timeRange.getStart().getTime();
        endTimeUTC = timeRange.getEnd().getTime();
        BooleanBuilder booleanBuilder = new BooleanBuilder(startField.lt(endTimeUTC));
        booleanBuilder.and(endField.gt(startTimeUTC));
        booleanBuilder.or(startField.eq(endField).and(endField.goe(startTimeUTC)));
        return booleanBuilder;
    }
}

