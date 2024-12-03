/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.support;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpressionBase;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.util.MathUtils;
import java.util.Collections;
import java.util.List;

public class NumberConversion<T>
extends FactoryExpressionBase<T> {
    private static final long serialVersionUID = 7840412008633901748L;
    private final List<Expression<?>> exprs;

    public NumberConversion(Expression<T> expr) {
        super(expr.getType());
        this.exprs = Collections.singletonList(expr);
    }

    @Override
    public <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this, context);
    }

    @Override
    public List<Expression<?>> getArgs() {
        return this.exprs;
    }

    @Override
    public T newInstance(Object ... args) {
        if (args[0] != null) {
            if (this.getType().equals(Boolean.class)) {
                return (T)Boolean.valueOf(((Number)args[0]).intValue() > 0);
            }
            return MathUtils.cast((Number)args[0], this.getType());
        }
        return null;
    }
}

