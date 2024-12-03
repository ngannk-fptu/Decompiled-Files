/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers;

import com.atlassian.confluence.extra.calendar3.caldav.filter.TimeRangeFilter;
import com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers.AbstractPropertyOperationMapper;
import com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers.PropertyToDBFieldMapperSupplier;
import com.google.common.base.Preconditions;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.SimpleExpression;
import org.bedework.caldav.util.TimeRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TimeRangeOperationMapper
extends AbstractPropertyOperationMapper<TimeRangeFilter> {
    @Autowired
    public TimeRangeOperationMapper(PropertyToDBFieldMapperSupplier propertyToDBFieldMapperSupplier) {
        super(propertyToDBFieldMapperSupplier);
    }

    @Override
    public BooleanBuilder apply(TimeRangeFilter timeRangeFilter) {
        Object propertyToDBFieldMapper = this.propertyToDBFieldMapperSupplier.get();
        SimpleExpression dbField = (SimpleExpression)propertyToDBFieldMapper.get((Object)timeRangeFilter.getPropertyInfoIndex());
        BooleanBuilder booleanBuilder = null;
        Preconditions.checkArgument((boolean)(dbField instanceof NumberPath));
        NumberPath dbFieldNumberPath = (NumberPath)dbField;
        TimeRange timeRange = (TimeRange)timeRangeFilter.getEntity();
        if (timeRange.getStart() != null) {
            long startTimeUTC = timeRange.getStart().getTime();
            booleanBuilder = new BooleanBuilder(dbFieldNumberPath.goe(startTimeUTC));
        }
        if (timeRange.getEnd() != null) {
            long endTimeUTC = timeRange.getEnd().getTime();
            if (booleanBuilder == null) {
                booleanBuilder = new BooleanBuilder(dbFieldNumberPath.lt(endTimeUTC));
            } else {
                booleanBuilder.and(dbFieldNumberPath.lt(endTimeUTC));
            }
        }
        return booleanBuilder;
    }
}

