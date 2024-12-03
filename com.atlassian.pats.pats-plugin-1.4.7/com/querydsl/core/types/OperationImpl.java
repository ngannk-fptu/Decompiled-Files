/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.primitives.Primitives
 *  javax.annotation.concurrent.Immutable
 */
package com.querydsl.core.types;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Primitives;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionBase;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Visitor;
import java.util.List;
import javax.annotation.concurrent.Immutable;

@Immutable
public class OperationImpl<T>
extends ExpressionBase<T>
implements Operation<T> {
    private static final long serialVersionUID = 4796432056083507588L;
    private final ImmutableList<Expression<?>> args;
    private final Operator operator;

    protected OperationImpl(Class<? extends T> type, Operator operator, Expression<?> ... args) {
        this(type, operator, ImmutableList.copyOf((Object[])args));
    }

    protected OperationImpl(Class<? extends T> type, Operator operator, ImmutableList<Expression<?>> args) {
        super(type);
        Class wrapped = Primitives.wrap(type);
        Preconditions.checkArgument((boolean)operator.getType().isAssignableFrom(wrapped), (Object)operator.name());
        this.operator = operator;
        this.args = args;
    }

    @Override
    public final Expression<?> getArg(int i) {
        return (Expression)this.args.get(i);
    }

    @Override
    public final List<Expression<?>> getArgs() {
        return this.args;
    }

    @Override
    public final Operator getOperator() {
        return this.operator;
    }

    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Operation) {
            Operation op = (Operation)o;
            return op.getOperator() == this.operator && op.getArgs().equals(this.args) && op.getType().equals(this.getType());
        }
        return false;
    }

    @Override
    public final <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this, context);
    }
}

