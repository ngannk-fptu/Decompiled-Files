/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package com.querydsl.sql;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.MutableExpressionBase;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.sql.WindowRows;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class WindowFunction<A>
extends MutableExpressionBase<A> {
    private static final String ORDER_BY = "order by ";
    private static final String PARTITION_BY = "partition by ";
    private static final long serialVersionUID = -4130672293308756779L;
    private final List<OrderSpecifier<?>> orderBy = new ArrayList();
    private final List<Expression<?>> partitionBy = new ArrayList();
    private final Expression<A> target;
    @Nullable
    private volatile transient SimpleExpression<A> value;
    private String rowsOrRange;
    private List<Expression<?>> rowsOrRangeArgs;

    public WindowFunction(Expression<A> expr) {
        super(expr.getType());
        this.target = expr;
    }

    public SimpleExpression<A> getValue() {
        if (this.value == null) {
            int size = 0;
            ImmutableList.Builder args = ImmutableList.builder();
            StringBuilder builder = new StringBuilder();
            builder.append("{0} over (");
            args.add(this.target);
            ++size;
            if (!this.partitionBy.isEmpty()) {
                builder.append(PARTITION_BY);
                boolean first = true;
                for (Expression<?> expr : this.partitionBy) {
                    if (!first) {
                        builder.append(", ");
                    }
                    builder.append("{" + size + "}");
                    args.add(expr);
                    ++size;
                    first = false;
                }
            }
            if (!this.orderBy.isEmpty()) {
                if (!this.partitionBy.isEmpty()) {
                    builder.append(" ");
                }
                builder.append(ORDER_BY);
                builder.append("{" + size + "}");
                args.add(ExpressionUtils.orderBy(this.orderBy));
                ++size;
            }
            if (this.rowsOrRange != null) {
                builder.append(this.rowsOrRange);
                args.addAll(this.rowsOrRangeArgs);
                size += this.rowsOrRangeArgs.size();
            }
            builder.append(")");
            this.value = Expressions.template(this.target.getType(), builder.toString(), args.build());
        }
        return this.value;
    }

    public SimpleExpression<A> as(Expression<A> alias) {
        return Expressions.operation(this.getType(), Ops.ALIAS, this, alias);
    }

    public SimpleExpression<A> as(String alias) {
        return Expressions.operation(this.getType(), Ops.ALIAS, this, ExpressionUtils.path(this.getType(), alias));
    }

    @Override
    public <R, C> R accept(Visitor<R, C> v, C context) {
        return this.getValue().accept(v, context);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof WindowFunction) {
            WindowFunction so = (WindowFunction)o;
            return so.target.equals(this.target) && so.partitionBy.equals(this.partitionBy) && so.orderBy.equals(this.orderBy);
        }
        return false;
    }

    public BooleanExpression eq(Expression<A> expr) {
        return this.getValue().eq(expr);
    }

    public BooleanExpression eq(A arg) {
        return this.getValue().eq(arg);
    }

    public BooleanExpression ne(Expression<A> expr) {
        return this.getValue().ne(expr);
    }

    public BooleanExpression ne(A arg) {
        return this.getValue().ne(arg);
    }

    public WindowFunction<A> orderBy(ComparableExpressionBase<?> orderBy) {
        this.value = null;
        this.orderBy.add(orderBy.asc());
        return this;
    }

    public WindowFunction<A> orderBy(ComparableExpressionBase<?> ... orderBy) {
        this.value = null;
        for (ComparableExpressionBase<?> e : orderBy) {
            this.orderBy.add(e.asc());
        }
        return this;
    }

    public WindowFunction<A> orderBy(OrderSpecifier<?> orderBy) {
        this.value = null;
        this.orderBy.add(orderBy);
        return this;
    }

    public WindowFunction<A> orderBy(OrderSpecifier<?> ... orderBy) {
        this.value = null;
        Collections.addAll(this.orderBy, orderBy);
        return this;
    }

    public WindowFunction<A> partitionBy(Expression<?> partitionBy) {
        this.value = null;
        this.partitionBy.add(partitionBy);
        return this;
    }

    public WindowFunction<A> partitionBy(Expression<?> ... partitionBy) {
        this.value = null;
        Collections.addAll(this.partitionBy, partitionBy);
        return this;
    }

    WindowFunction<A> withRowsOrRange(String s, List<Expression<?>> args) {
        this.rowsOrRange = s;
        this.rowsOrRangeArgs = args;
        return this;
    }

    public WindowRows<A> rows() {
        this.value = null;
        int offset = this.orderBy.size() + this.partitionBy.size() + 1;
        return new WindowRows(this, " rows", offset);
    }

    public WindowRows<A> range() {
        this.value = null;
        int offset = this.orderBy.size() + this.partitionBy.size() + 1;
        return new WindowRows(this, " range", offset);
    }
}

