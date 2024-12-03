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
import com.querydsl.core.types.dsl.StringExpression;
import java.util.List;

public class StringOperation
extends StringExpression
implements Operation<String> {
    private static final long serialVersionUID = 6846556373847139549L;
    private final OperationImpl<String> opMixin;

    protected StringOperation(OperationImpl<String> mixin) {
        super((Expression<String>)mixin);
        this.opMixin = mixin;
    }

    protected StringOperation(Operator op, Expression<?> ... args) {
        this(op, ImmutableList.copyOf((Object[])args));
    }

    protected StringOperation(Operator op, ImmutableList<Expression<?>> args) {
        super((Expression<String>)ExpressionUtils.operation(String.class, op, args));
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

