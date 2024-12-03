/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers;

import com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers.AbstractOperationMapper;
import com.querydsl.core.BooleanBuilder;
import org.springframework.stereotype.Component;

@Component
public class AndOperationMapper
extends AbstractOperationMapper {
    @Override
    protected BooleanBuilder combine(BooleanBuilder leftPredicate, BooleanBuilder rightPredicate) {
        return leftPredicate.and(rightPredicate);
    }
}

