/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers;

import com.atlassian.confluence.extra.calendar3.caldav.filter.AbstractPropertyFilter;
import com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers.PropertyToDBFieldMapperSupplier;
import com.querydsl.core.BooleanBuilder;
import java.util.function.Function;

public abstract class AbstractPropertyOperationMapper<T extends AbstractPropertyFilter>
implements Function<T, BooleanBuilder> {
    protected final PropertyToDBFieldMapperSupplier propertyToDBFieldMapperSupplier;

    public AbstractPropertyOperationMapper(PropertyToDBFieldMapperSupplier propertyToDBFieldMapperSupplier) {
        this.propertyToDBFieldMapperSupplier = propertyToDBFieldMapperSupplier;
    }
}

