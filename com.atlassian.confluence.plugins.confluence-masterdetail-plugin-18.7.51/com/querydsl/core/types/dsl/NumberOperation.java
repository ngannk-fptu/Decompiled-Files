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
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.NumberExpression;
import java.util.List;

public class NumberOperation<T extends Number>
extends NumberExpression<T>
implements Operation<T> {
    private static final long serialVersionUID = -3593040852095778453L;
    private final OperationImpl<T> opMixin;

    protected NumberOperation(OperationImpl<T> mixin) {
        super(mixin);
        this.opMixin = mixin;
    }

    protected NumberOperation(Class<? extends T> type, Operator op, Expression<?> ... args) {
        this(type, op, ImmutableList.copyOf((Object[])args));
    }

    protected NumberOperation(Class<? extends T> type, Operator op, ImmutableList<Expression<?>> args) {
        super(ExpressionUtils.operation(type, op, args));
        this.opMixin = (OperationImpl)this.mixin;
    }

    @Override
    public NumberExpression<T> negate() {
        if (this.opMixin.getOperator() == Ops.NEGATE) {
            return (NumberExpression)this.opMixin.getArg(0);
        }
        return super.negate();
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

