/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 */
package com.querydsl.core.types;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.FactoryExpressionBase;
import com.querydsl.core.types.QTuple;
import com.querydsl.core.types.Visitor;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class MappingProjection<T>
extends FactoryExpressionBase<T> {
    private static final long serialVersionUID = -948494350919774466L;
    private final QTuple qTuple;

    public MappingProjection(Class<? super T> type, Expression<?> ... args) {
        super(type);
        this.qTuple = new QTuple(ExpressionUtils.distinctList(args));
    }

    public MappingProjection(Class<? super T> type, Expression<?>[] ... args) {
        super(type);
        this.qTuple = new QTuple(ExpressionUtils.distinctList(args));
    }

    @Override
    public T newInstance(Object ... values) {
        return this.map(this.qTuple.newInstance(values));
    }

    protected abstract T map(Tuple var1);

    @Override
    public List<Expression<?>> getArgs() {
        return this.qTuple.getArgs();
    }

    @Override
    public <R, C> R accept(Visitor<R, C> v, @Nullable C context) {
        return v.visit(this, context);
    }
}

