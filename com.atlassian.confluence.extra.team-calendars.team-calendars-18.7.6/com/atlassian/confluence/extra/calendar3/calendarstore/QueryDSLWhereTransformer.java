/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.calendarstore;

import com.atlassian.confluence.extra.calendar3.caldav.filter.AbstractPropertyFilter;
import com.atlassian.confluence.extra.calendar3.caldav.filter.AndFilter;
import com.atlassian.confluence.extra.calendar3.caldav.filter.EntityTimeRangeFilter;
import com.atlassian.confluence.extra.calendar3.caldav.filter.FilterBase;
import com.atlassian.confluence.extra.calendar3.caldav.filter.FilterBaseTransformer;
import com.atlassian.confluence.extra.calendar3.caldav.filter.FilterType;
import com.atlassian.confluence.extra.calendar3.caldav.filter.NotFilter;
import com.atlassian.confluence.extra.calendar3.caldav.filter.OrFilter;
import com.atlassian.confluence.extra.calendar3.caldav.filter.PresenceFilterAbstract;
import com.atlassian.confluence.extra.calendar3.caldav.filter.PropertyValueFilter;
import com.atlassian.confluence.extra.calendar3.caldav.filter.TimeRangeFilter;
import com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers.AbstractPropertyOperationMapper;
import com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers.AndOperationMapper;
import com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers.EntityTimeRangeOperationMapper;
import com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers.NotOperationMapper;
import com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers.OrOperationMapper;
import com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers.PresencePropertyOperationMapper;
import com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers.PropertyValueOperationMapper;
import com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers.TimeRangeOperationMapper;
import com.querydsl.core.BooleanBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="queryDSLWhereTransformer")
public class QueryDSLWhereTransformer
implements FilterBaseTransformer<BooleanBuilder> {
    private final Map<Class<? extends FilterBase>, AbstractPropertyOperationMapper> filterMappers = new HashMap<Class<? extends FilterBase>, AbstractPropertyOperationMapper>();
    private final Map<Class<? extends FilterBase>, Function<List<BooleanBuilder>, BooleanBuilder>> operationFilterMapper = new HashMap<Class<? extends FilterBase>, Function<List<BooleanBuilder>, BooleanBuilder>>();
    private AndOperationMapper andOperationMapper;
    private OrOperationMapper orOperationMapper;
    private NotOperationMapper notOperationMapper;
    private PresencePropertyOperationMapper presencePropertyOperationMapper;
    private PropertyValueOperationMapper propertyValueOperationMapper;
    private EntityTimeRangeOperationMapper entityTimeRangeOperationMapper;
    private TimeRangeOperationMapper timeRangeOperationMapper;

    @Autowired
    public QueryDSLWhereTransformer(AndOperationMapper andOperationMapper, OrOperationMapper orOperationMapper, NotOperationMapper notOperationMapper, PresencePropertyOperationMapper presencePropertyOperationMapper, PropertyValueOperationMapper propertyValueOperationMapper, EntityTimeRangeOperationMapper entityTimeRangeOperationMapper, TimeRangeOperationMapper timeRangeOperationMapper) {
        this.andOperationMapper = andOperationMapper;
        this.orOperationMapper = orOperationMapper;
        this.notOperationMapper = notOperationMapper;
        this.presencePropertyOperationMapper = presencePropertyOperationMapper;
        this.propertyValueOperationMapper = propertyValueOperationMapper;
        this.entityTimeRangeOperationMapper = entityTimeRangeOperationMapper;
        this.timeRangeOperationMapper = timeRangeOperationMapper;
        this.init();
    }

    private void init() {
        this.filterMappers.put(PresenceFilterAbstract.class, this.presencePropertyOperationMapper);
        this.filterMappers.put(PropertyValueFilter.class, this.propertyValueOperationMapper);
        this.filterMappers.put(EntityTimeRangeFilter.class, this.entityTimeRangeOperationMapper);
        this.filterMappers.put(TimeRangeFilter.class, this.timeRangeOperationMapper);
        this.operationFilterMapper.put(AndFilter.class, this.andOperationMapper);
        this.operationFilterMapper.put(OrFilter.class, this.orOperationMapper);
        this.operationFilterMapper.put(NotFilter.class, this.notOperationMapper);
    }

    @Override
    public Optional<BooleanBuilder> transform(FilterBase toBeTransformed) {
        FilterType filterType = toBeTransformed.getType();
        if (this.isOperation(filterType)) {
            ArrayList<BooleanBuilder> childPredicates = new ArrayList<BooleanBuilder>();
            for (FilterBase childFilter : toBeTransformed.getChildren()) {
                Optional<BooleanBuilder> childPredicate = this.transform(childFilter);
                if (!childPredicate.isPresent()) continue;
                childPredicates.add(childPredicate.get());
            }
            Function<List<BooleanBuilder>, BooleanBuilder> operationMapper = this.operationFilterMapper.get(toBeTransformed.getClass());
            BooleanBuilder parentPredicate = operationMapper.apply(childPredicates);
            return Optional.of(parentPredicate);
        }
        AbstractPropertyFilter propertyFilter = (AbstractPropertyFilter)toBeTransformed;
        Function mapper = this.filterMappers.get(propertyFilter.getClass());
        if (mapper != null) {
            BooleanBuilder predicate = (BooleanBuilder)mapper.apply(propertyFilter);
            return Optional.of(predicate);
        }
        return Optional.empty();
    }

    private boolean isOperation(FilterType type) {
        return type == FilterType.AND || type == FilterType.OR || type == FilterType.NOT;
    }
}

