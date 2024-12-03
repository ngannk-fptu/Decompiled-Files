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
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.OperationImpl;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.DslExpression;
import java.util.List;

public class DslOperation<T>
extends DslExpression<T>
implements Operation<T> {
    private static final long serialVersionUID = -285668548371034230L;
    private final OperationImpl<T> opMixin;

    protected DslOperation(Class<? extends T> type, Operator op, Expression<?> ... args) {
        this(type, op, ImmutableList.copyOf((Object[])args));
    }

    protected DslOperation(Class<? extends T> type, Operator op, ImmutableList<Expression<?>> args) {
        super(ExpressionUtils.operation(type, op, args));
        this.opMixin = (OperationImpl)this.mixin;
    }

    @Override
    public final <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this.opMixin, context);
    }

    @Override
    public Expression<?> getArg(int index) {
        return this.opMixin.getArg(index);
    }

    @Override
    public List<Expression<?>> getArgs() {
        return this.opMixin.getArgs();
    }

    @Override
    public Operator getOperator() {
        return this.opMixin.getOperator();
    }
}

