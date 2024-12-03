/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package com.querydsl.core.types;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.FactoryExpressionBase;
import com.querydsl.core.types.Visitor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public class QMap
extends FactoryExpressionBase<Map<Expression<?>, ?>> {
    private static final long serialVersionUID = -7545994090073480810L;
    private final ImmutableList<Expression<?>> args;

    protected QMap(Expression<?> ... args) {
        super(Map.class);
        this.args = ImmutableList.copyOf((Object[])args);
    }

    protected QMap(ImmutableList<Expression<?>> args) {
        super(Map.class);
        this.args = args;
    }

    protected QMap(Expression<?>[] ... args) {
        super(Map.class);
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
    public Map<Expression<?>, ?> newInstance(Object ... args) {
        HashMap map = Maps.newHashMap();
        for (int i = 0; i < args.length; ++i) {
            map.put(this.args.get(i), args[i]);
        }
        return map;
    }
}

