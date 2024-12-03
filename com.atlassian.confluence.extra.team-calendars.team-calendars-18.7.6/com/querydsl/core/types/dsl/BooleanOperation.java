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
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.PredicateOperation;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.List;

public class BooleanOperation
extends BooleanExpression
implements Operation<Boolean> {
    private static final long serialVersionUID = 7432281499861357581L;
    private final PredicateOperation opMixin;

    protected BooleanOperation(PredicateOperation mixin) {
        super(mixin);
        this.opMixin = mixin;
    }

    protected BooleanOperation(Operator op, Expression<?> ... args) {
        this(op, ImmutableList.copyOf((Object[])args));
    }

    protected BooleanOperation(Operator op, ImmutableList<Expression<?>> args) {
        super(ExpressionUtils.predicate(op, args));
        this.opMixin = (PredicateOperation)this.mixin;
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

    @Override
    public BooleanExpression not() {
        if (this.opMixin.getOperator() == Ops.NOT && this.opMixin.getArg(0) instanceof BooleanExpression) {
            return (BooleanExpression)this.opMixin.getArg(0);
        }
        return super.not();
    }
}

