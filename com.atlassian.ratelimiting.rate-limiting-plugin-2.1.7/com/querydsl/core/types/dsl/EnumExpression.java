/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.LiteralExpression;
import com.querydsl.core.types.dsl.NumberExpression;

public abstract class EnumExpression<T extends Enum<T>>
extends LiteralExpression<T> {
    private static final long serialVersionUID = 8819222316513862829L;
    private volatile transient NumberExpression<Integer> ordinal;

    public EnumExpression(Expression<T> mixin) {
        super(mixin);
    }

    @Override
    public EnumExpression<T> as(Path<T> alias) {
        return Expressions.enumOperation(this.getType(), Ops.ALIAS, this.mixin, alias);
    }

    @Override
    public EnumExpression<T> as(String alias) {
        return this.as(ExpressionUtils.path(this.getType(), alias));
    }

    public NumberExpression<Integer> ordinal() {
        if (this.ordinal == null) {
            this.ordinal = Expressions.numberOperation(Integer.class, Ops.ORDINAL, this.mixin);
        }
        return this.ordinal;
    }
}

