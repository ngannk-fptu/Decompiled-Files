/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import javax.annotation.Nullable;

public abstract class LiteralExpression<T extends Comparable>
extends ComparableExpression<T> {
    @Nullable
    private volatile transient StringExpression stringCast;

    public LiteralExpression(Expression<T> mixin) {
        super(mixin);
    }

    public <A extends Number> NumberExpression<A> castToNum(Class<A> type) {
        return Expressions.numberOperation(type, Ops.NUMCAST, this.mixin, ConstantImpl.create(type));
    }

    public StringExpression stringValue() {
        if (this.stringCast == null) {
            this.stringCast = Expressions.stringOperation(Ops.STRING_CAST, this.mixin);
        }
        return this.stringCast;
    }
}

