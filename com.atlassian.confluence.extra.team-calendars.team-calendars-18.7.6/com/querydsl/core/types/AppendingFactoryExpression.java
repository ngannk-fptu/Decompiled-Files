/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package com.querydsl.core.types;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpressionBase;
import com.querydsl.core.types.Visitor;
import java.util.List;
import javax.annotation.Nullable;

class AppendingFactoryExpression<T>
extends FactoryExpressionBase<T> {
    private static final long serialVersionUID = -1337452521648394353L;
    private final Expression<T> base;
    private final List<Expression<?>> args;

    protected AppendingFactoryExpression(Expression<T> base, Expression<?> ... rest) {
        super(base.getType());
        this.base = base;
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.add(base);
        builder.add((Object[])rest);
        this.args = builder.build();
    }

    @Override
    public List<Expression<?>> getArgs() {
        return this.args;
    }

    @Override
    @Nullable
    public T newInstance(Object ... args) {
        return (T)args[0];
    }

    @Override
    @Nullable
    public <R, C> R accept(Visitor<R, C> v, @Nullable C context) {
        return this.base.accept(v, context);
    }
}

