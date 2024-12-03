/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;

public final class Wildcard {
    public static final Expression<Object[]> all = ExpressionUtils.template(Object[].class, "*", new Object[0]);
    public static final NumberExpression<Long> count = Expressions.numberOperation(Long.class, Ops.AggOps.COUNT_ALL_AGG, new Expression[0]);
    public static final NumberExpression<Long> countDistinct = Expressions.numberOperation(Long.class, Ops.AggOps.COUNT_DISTINCT_ALL_AGG, new Expression[0]);
    public static final NumberExpression<Integer> countAsInt = Expressions.numberOperation(Integer.class, Ops.AggOps.COUNT_ALL_AGG, new Expression[0]);

    private Wildcard() {
    }
}

