/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers;

import com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers.AbstractOperationMapper;
import com.google.common.base.Preconditions;
import com.querydsl.core.BooleanBuilder;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class NotOperationMapper
extends AbstractOperationMapper {
    @Override
    public BooleanBuilder apply(List<BooleanBuilder> booleanBuilders) {
        Preconditions.checkNotNull(booleanBuilders);
        Preconditions.checkArgument((!booleanBuilders.isEmpty() ? 1 : 0) != 0);
        BooleanBuilder operationPredicate = null;
        for (int i = 0; i < booleanBuilders.size(); ++i) {
            if (i == 0) {
                operationPredicate = booleanBuilders.get(i);
                operationPredicate.not();
                continue;
            }
            this.combine(operationPredicate, booleanBuilders.get(i).not());
        }
        return operationPredicate;
    }

    @Override
    protected BooleanBuilder combine(BooleanBuilder leftPredicate, BooleanBuilder rightPredicate) {
        return leftPredicate.and(rightPredicate);
    }
}

