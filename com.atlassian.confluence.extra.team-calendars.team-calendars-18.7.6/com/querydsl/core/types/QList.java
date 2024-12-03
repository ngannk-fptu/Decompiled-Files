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
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.FactoryExpressionBase;
import com.querydsl.core.types.Visitor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class QList
extends FactoryExpressionBase<List<?>> {
    private static final long serialVersionUID = -7545994090073480810L;
    private final ImmutableList<Expression<?>> args;

    protected QList(Expression<?> ... args) {
        super(List.class);
        this.args = ImmutableList.copyOf((Object[])args);
    }

    protected QList(ImmutableList<Expression<?>> args) {
        super(List.class);
        this.args = args;
    }

    protected QList(Expression<?>[] ... args) {
        super(List.class);
        ImmutableList.Builder builder = ImmutableList.builder();
        for (Object[] objectArray : args) {
            builder.add(objectArray);
        }
        this.args = builder.build();
    }

    @Override
    @Nullable
    public <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this, context);
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

    @Override
    @Nullable
    public List<?> newInstance(Object ... args) {
        return Collections.unmodifiableList(Arrays.asList(args));
    }
}

