/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers;

import com.atlassian.confluence.extra.calendar3.caldav.filter.PresenceFilterAbstract;
import com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers.AbstractPropertyOperationMapper;
import com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers.PropertyToDBFieldMapperSupplier;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.SimpleExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PresencePropertyOperationMapper
extends AbstractPropertyOperationMapper<PresenceFilterAbstract> {
    @Autowired
    public PresencePropertyOperationMapper(PropertyToDBFieldMapperSupplier propertyToDBFieldMapperSupplier) {
        super(propertyToDBFieldMapperSupplier);
    }

    @Override
    public BooleanBuilder apply(PresenceFilterAbstract presenceFilter) {
        Object propertyToDBFieldMapper = this.propertyToDBFieldMapperSupplier.get();
        SimpleExpression dbField = (SimpleExpression)propertyToDBFieldMapper.get((Object)presenceFilter.getPropertyInfoIndex());
        return new BooleanBuilder(presenceFilter.exists() ? dbField.isNotNull() : dbField.isNull());
    }
}

