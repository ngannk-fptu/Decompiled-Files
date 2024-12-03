/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.querydsl.core.types.dsl;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OperationImpl;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.CollectionExpressionBase;
import java.util.Collection;
import javax.annotation.Nullable;

public class CollectionOperation<E>
extends CollectionExpressionBase<Collection<E>, E> {
    private static final long serialVersionUID = 3154315192589335574L;
    private final Class<E> elementType;
    private final OperationImpl<Collection<E>> opMixin;

    protected CollectionOperation(Class<? super E> type, Operator op, Expression<?> ... args) {
        this(type, op, ImmutableList.copyOf((Object[])args));
    }

    protected CollectionOperation(Class<? super E> type, Operator op, ImmutableList<Expression<?>> args) {
        super(ExpressionUtils.operation(Collection.class, op, args));
        this.opMixin = (OperationImpl)this.mixin;
        this.elementType = type;
    }

    @Override
    public Class<?> getParameter(int index) {
        if (index == 0) {
            return this.elementType;
        }
        throw new IndexOutOfBoundsException(String.valueOf(index));
    }

    @Override
    @Nullable
    public <R, C> R accept(Visitor<R, C> v, C context) {
        return this.opMixin.accept(v, context);
    }

    @Override
    public Class<E> getElementType() {
        return this.elementType;
    }
}

