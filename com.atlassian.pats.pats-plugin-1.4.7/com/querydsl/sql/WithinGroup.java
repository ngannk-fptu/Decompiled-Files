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
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.SimpleOperation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class WithinGroup<T>
extends SimpleOperation<T> {
    private static final long serialVersionUID = 464583892898579544L;

    private static Expression<?> merge(Expression<?> ... args) {
        if (args.length == 1) {
            return args[0];
        }
        return ExpressionUtils.list(Object.class, args);
    }

    public WithinGroup(Class<? extends T> type, Operator op) {
        super(type, op, ImmutableList.of());
    }

    public WithinGroup(Class<? extends T> type, Operator op, Expression<?> arg) {
        super(type, op, ImmutableList.of(arg));
    }

    public WithinGroup(Class<? extends T> type, Operator op, Expression<?> arg1, Expression<?> arg2) {
        super(type, op, ImmutableList.of(arg1, arg2));
    }

    public WithinGroup(Class<? extends T> type, Operator op, Expression<?> ... args) {
        super(type, op, WithinGroup.merge(args));
    }

    public OrderBy withinGroup() {
        return new OrderBy();
    }

    public class OrderBy
    extends MutableExpressionBase<T> {
        private static final long serialVersionUID = -4936481493030913621L;
        private static final String ORDER_BY = "order by ";
        @Nullable
        private volatile transient SimpleExpression<T> value;
        private final List<OrderSpecifier<?>> orderBy;

        public OrderBy() {
            super(WithinGroup.this.getType());
            this.orderBy = new ArrayList();
        }

        public SimpleExpression<T> getValue() {
            if (this.value == null) {
                int size = 0;
                ImmutableList.Builder args = ImmutableList.builder();
                StringBuilder builder = new StringBuilder();
                builder.append("{0} within group (");
                args.add((Object)WithinGroup.this);
                ++size;
                if (!this.orderBy.isEmpty()) {
                    builder.append(ORDER_BY);
                    builder.append("{" + size + "}");
                    args.add(ExpressionUtils.orderBy(this.orderBy));
                }
                builder.append(")");
                this.value = Expressions.template(WithinGroup.this.getType(), builder.toString(), args.build());
            }
            return this.value;
        }

        @Override
        public <R, C> R accept(Visitor<R, C> v, C context) {
            return this.getValue().accept(v, context);
        }

        public OrderBy orderBy(ComparableExpressionBase<?> orderBy) {
            this.value = null;
            this.orderBy.add(orderBy.asc());
            return this;
        }

        public OrderBy orderBy(ComparableExpressionBase<?> ... orderBy) {
            this.value = null;
            for (ComparableExpressionBase<?> e : orderBy) {
                this.orderBy.add(e.asc());
            }
            return this;
        }

        public OrderBy orderBy(OrderSpecifier<?> orderBy) {
            this.value = null;
            this.orderBy.add(orderBy);
            return this;
        }

        public OrderBy orderBy(OrderSpecifier<?> ... orderBy) {
            this.value = null;
            Collections.addAll(this.orderBy, orderBy);
            return this;
        }
    }
}

