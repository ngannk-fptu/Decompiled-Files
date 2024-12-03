/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Coalesce;
import com.querydsl.core.types.dsl.SimpleExpression;
import javax.annotation.Nullable;

public abstract class ComparableExpressionBase<T extends Comparable>
extends SimpleExpression<T> {
    private static final long serialVersionUID = 1460921109546656911L;
    @Nullable
    private volatile transient OrderSpecifier<T> asc;
    @Nullable
    private volatile transient OrderSpecifier<T> desc;

    public ComparableExpressionBase(Expression<T> mixin) {
        super(mixin);
    }

    public final OrderSpecifier<T> asc() {
        if (this.asc == null) {
            this.asc = new OrderSpecifier(Order.ASC, this.mixin);
        }
        return this.asc;
    }

    public final Coalesce<T> coalesce(Expression<?> ... exprs) {
        Coalesce coalesce = new Coalesce(this.getType(), this.mixin);
        for (Expression<?> expr : exprs) {
            coalesce.add(expr);
        }
        return coalesce;
    }

    public final Coalesce<T> coalesce(T ... args) {
        Coalesce<T> coalesce = new Coalesce<T>(this.getType(), this.mixin);
        for (T arg : args) {
            coalesce.add(arg);
        }
        return coalesce;
    }

    public final OrderSpecifier<T> desc() {
        if (this.desc == null) {
            this.desc = new OrderSpecifier(Order.DESC, this.mixin);
        }
        return this.desc;
    }
}

