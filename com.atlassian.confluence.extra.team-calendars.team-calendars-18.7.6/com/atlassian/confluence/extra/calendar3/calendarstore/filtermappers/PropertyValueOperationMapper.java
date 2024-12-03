/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers;

import com.atlassian.confluence.extra.calendar3.caldav.filter.PropertyValueFilter;
import com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers.AbstractPropertyOperationMapper;
import com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers.PropertyToDBFieldMapperSupplier;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.StringPath;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PropertyValueOperationMapper
extends AbstractPropertyOperationMapper<PropertyValueFilter<?>> {
    @Autowired
    public PropertyValueOperationMapper(PropertyToDBFieldMapperSupplier propertyToDBFieldMapperSupplier) {
        super(propertyToDBFieldMapperSupplier);
    }

    @Override
    public BooleanBuilder apply(PropertyValueFilter<?> propertyValueFilter) {
        Object propertyToDBFieldMapper = this.propertyToDBFieldMapperSupplier.get();
        SimpleExpression dbField = (SimpleExpression)propertyToDBFieldMapper.get((Object)propertyValueFilter.getPropertyInfoIndex());
        Object entity = propertyValueFilter.getEntity();
        PropertyValueFilter.MatchingConfiguration matchingConfiguration = propertyValueFilter.getMatchingConfiguration();
        if (entity instanceof Collection) {
            Collection values = (Collection)entity;
            return new BooleanBuilder(dbField.in(values));
        }
        if (dbField instanceof StringPath && entity instanceof String) {
            StringPath stringDbField = (StringPath)dbField;
            String stringEntity = (String)entity;
            if (matchingConfiguration != null && !matchingConfiguration.isExactMatch()) {
                return new BooleanBuilder(stringDbField.like(stringEntity));
            }
            return matchingConfiguration != null && matchingConfiguration.isCaseSensitive() ? new BooleanBuilder(stringDbField.equalsIgnoreCase(stringEntity)) : new BooleanBuilder(stringDbField.eq(stringEntity));
        }
        return new BooleanBuilder(dbField.eq(entity));
    }
}

