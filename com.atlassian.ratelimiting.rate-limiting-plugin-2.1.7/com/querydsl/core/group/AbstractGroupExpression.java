/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.group;

import com.querydsl.core.group.GroupExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.DslExpression;
import com.querydsl.core.types.dsl.Expressions;

public abstract class AbstractGroupExpression<T, R>
implements GroupExpression<T, R> {
    private static final long serialVersionUID = 1509709546966783160L;
    private final Class<? extends R> type;
    private final Expression<T> expr;

    public AbstractGroupExpression(Class<? super R> type, Expression<T> expr) {
        this.type = type;
        this.expr = expr;
    }

    public DslExpression<R> as(Path<R> alias) {
        return Expressions.dslOperation(this.getType(), Ops.ALIAS, this, alias);
    }

    public DslExpression<R> as(String alias) {
        return this.as(ExpressionUtils.path(this.getType(), alias));
    }

    @Override
    public Expression<T> getExpression() {
        return this.expr;
    }

    @Override
    public <R, C> R accept(Visitor<R, C> v, C context) {
        return this.expr.accept(v, context);
    }

    public boolean equals(Object o) {
        if (o != null && this.getClass().equals(o.getClass())) {
            return ((GroupExpression)o).getExpression().equals(this.expr);
        }
        return false;
    }

    @Override
    public Class<? extends R> getType() {
        return this.type;
    }

    public int hashCode() {
        return this.expr.hashCode();
    }

    public String toString() {
        return this.expr.toString();
    }
}

