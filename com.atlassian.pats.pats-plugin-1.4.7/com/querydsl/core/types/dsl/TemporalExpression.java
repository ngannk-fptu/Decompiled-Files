/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.LiteralExpression;

public abstract class TemporalExpression<T extends Comparable>
extends LiteralExpression<T> {
    private static final long serialVersionUID = 1137918766051524298L;

    public TemporalExpression(Expression<T> mixin) {
        super(mixin);
    }

    public BooleanExpression after(T right) {
        return this.gt(right);
    }

    public BooleanExpression after(Expression<T> right) {
        return this.gt(right);
    }

    public BooleanExpression before(T right) {
        return this.lt(right);
    }

    public BooleanExpression before(Expression<T> right) {
        return this.lt(right);
    }
}

