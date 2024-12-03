/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;

public abstract class DslExpression<T>
implements Expression<T> {
    private static final long serialVersionUID = -3383063447710753290L;
    protected final Expression<T> mixin;
    protected final int hashCode;

    public DslExpression(Expression<T> mixin) {
        this.mixin = mixin;
        this.hashCode = mixin.hashCode();
    }

    @Override
    public final Class<? extends T> getType() {
        return this.mixin.getType();
    }

    public DslExpression<T> as(Path<T> alias) {
        return Expressions.dslOperation(this.getType(), Ops.ALIAS, this.mixin, alias);
    }

    public DslExpression<T> as(String alias) {
        return this.as(ExpressionUtils.path(this.getType(), alias));
    }

    public boolean equals(Object o) {
        return this.mixin.equals(o);
    }

    public final int hashCode() {
        return this.hashCode;
    }

    public final String toString() {
        return this.mixin.toString();
    }
}

