/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.querydsl.core.types;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.FactoryExpressionBase;
import com.querydsl.core.types.Visitor;
import java.lang.reflect.Array;
import java.util.List;
import javax.annotation.concurrent.Immutable;

@Immutable
public class ArrayConstructorExpression<T>
extends FactoryExpressionBase<T[]> {
    private static final long serialVersionUID = 8667880104290226505L;
    private final Class<T> elementType;
    private final ImmutableList<Expression<?>> args;

    public ArrayConstructorExpression(Expression<?> ... args) {
        this(Object[].class, args);
    }

    public ArrayConstructorExpression(Class<T[]> type, Expression<T> ... args) {
        super(type);
        this.elementType = type.getComponentType();
        this.args = ImmutableList.copyOf((Object[])args);
    }

    public final Class<T> getElementType() {
        return this.elementType;
    }

    @Override
    public <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this, context);
    }

    @Override
    public T[] newInstance(Object ... a) {
        if (a.getClass().getComponentType().equals(this.elementType)) {
            return a;
        }
        Object[] rv = (Object[])Array.newInstance(this.elementType, a.length);
        System.arraycopy(a, 0, rv, 0, a.length);
        return rv;
    }

    @Override
    public List<Expression<?>> getArgs() {
        return this.args;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof FactoryExpression) {
            FactoryExpression c = (FactoryExpression)obj;
            return this.args.equals(c.getArgs()) && this.getType().equals(c.getType());
        }
        return false;
    }
}

