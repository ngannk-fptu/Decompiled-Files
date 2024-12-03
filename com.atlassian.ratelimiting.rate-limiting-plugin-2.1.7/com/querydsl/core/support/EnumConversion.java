/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.querydsl.core.support;

import com.google.common.base.Preconditions;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpressionBase;
import com.querydsl.core.types.Visitor;
import java.util.Collections;
import java.util.List;

public class EnumConversion<T>
extends FactoryExpressionBase<T> {
    private static final long serialVersionUID = 7840412008633901748L;
    private final List<Expression<?>> exprs;
    private final T[] values;

    public EnumConversion(Expression<T> expr) {
        super(expr.getType());
        Class type = this.getType();
        Preconditions.checkArgument((boolean)type.isEnum(), (String)"%s is not an enum", (Object[])new Object[]{type});
        this.exprs = Collections.singletonList(expr);
        this.values = type.getEnumConstants();
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
            if (args[0] instanceof String) {
                Enum rv = Enum.valueOf(this.getType().asSubclass(Enum.class), (String)args[0]);
                return (T)rv;
            }
            if (args[0] instanceof Number) {
                return this.values[((Number)args[0]).intValue()];
            }
            Object rv = args[0];
            return (T)rv;
        }
        return null;
    }
}

