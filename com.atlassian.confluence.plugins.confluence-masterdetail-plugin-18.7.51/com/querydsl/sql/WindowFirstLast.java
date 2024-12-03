/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  javax.annotation.Nullable
 */
package com.querydsl.sql;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.MutableExpressionBase;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.sql.WindowFunction;
import com.querydsl.sql.WindowOver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class WindowFirstLast<T>
extends MutableExpressionBase<T> {
    private static final long serialVersionUID = 4107262569593794721L;
    private static final String ORDER_BY = "order by ";
    private final List<OrderSpecifier<?>> orderBy = new ArrayList();
    @Nullable
    private volatile transient SimpleExpression<T> value;
    private final Expression<T> target;
    private final boolean first;

    public WindowFirstLast(WindowOver<T> target, boolean first) {
        super(target.getType());
        this.target = target;
        this.first = first;
    }

    @Override
    public <R, C> R accept(Visitor<R, C> v, C context) {
        return this.getValue().accept(v, context);
    }

    public WindowFirstLast<T> orderBy(ComparableExpressionBase<?> orderBy) {
        this.value = null;
        this.orderBy.add(orderBy.asc());
        return this;
    }

    public WindowFirstLast<T> orderBy(ComparableExpressionBase<?> ... orderBy) {
        this.value = null;
        for (ComparableExpressionBase<?> e : orderBy) {
            this.orderBy.add(e.asc());
        }
        return this;
    }

    public WindowFirstLast<T> orderBy(OrderSpecifier<?> orderBy) {
        this.value = null;
        this.orderBy.add(orderBy);
        return this;
    }

    public WindowFirstLast<T> orderBy(OrderSpecifier<?> ... orderBy) {
        this.value = null;
        Collections.addAll(this.orderBy, orderBy);
        return this;
    }

    SimpleExpression<T> getValue() {
        if (this.value == null) {
            if (this.orderBy.isEmpty()) {
                throw new IllegalStateException("No order by arguments given");
            }
            ImmutableList.Builder args = ImmutableList.builder();
            StringBuilder builder = new StringBuilder();
            builder.append("{0} keep (dense_rank ");
            args.add(this.target);
            builder.append(this.first ? "first " : "last ");
            builder.append(ORDER_BY);
            builder.append("{1}");
            args.add(ExpressionUtils.orderBy(this.orderBy));
            builder.append(")");
            this.value = Expressions.template(this.target.getType(), builder.toString(), args.build());
        }
        return this.value;
    }

    public WindowFunction<T> over() {
        return new WindowFunction<T>(this.getValue());
    }
}

