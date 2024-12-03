/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers;

import com.google.common.base.Preconditions;
import com.querydsl.core.BooleanBuilder;
import java.util.List;
import java.util.function.Function;

public abstract class AbstractOperationMapper
implements Function<List<BooleanBuilder>, BooleanBuilder> {
    @Override
    public BooleanBuilder apply(List<BooleanBuilder> booleanBuilders) {
        Preconditions.checkNotNull(booleanBuilders);
        Preconditions.checkArgument((!booleanBuilders.isEmpty() ? 1 : 0) != 0);
        BooleanBuilder operationPredicate = (BooleanBuilder)booleanBuilders.stream().reduce(this::combine).get();
        return operationPredicate;
    }

    protected abstract BooleanBuilder combine(BooleanBuilder var1, BooleanBuilder var2);
}

