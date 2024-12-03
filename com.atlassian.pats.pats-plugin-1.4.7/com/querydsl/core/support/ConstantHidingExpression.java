/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package com.querydsl.core.support;

import com.google.common.collect.Lists;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.FactoryExpressionBase;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.Expressions;
import java.util.List;
import javax.annotation.Nullable;

public class ConstantHidingExpression<T>
extends FactoryExpressionBase<T> {
    private static final long serialVersionUID = -7834053123363933721L;
    private final FactoryExpression<T> expr;
    private final List<Expression<?>> args;
    private final Object[] template;

    public ConstantHidingExpression(FactoryExpression<T> expr) {
        super(expr.getType());
        this.expr = expr;
        this.args = Lists.newArrayList();
        this.template = new Object[expr.getArgs().size()];
        for (int i = 0; i < this.template.length; ++i) {
            Expression<?> arg = expr.getArgs().get(i);
            Expression<?> unwrapped = ConstantHidingExpression.unwrap(arg);
            if (unwrapped instanceof Constant) {
                this.template[i] = ((Constant)unwrapped).getConstant();
                continue;
            }
            if (unwrapped.equals(Expressions.TRUE)) {
                this.template[i] = Boolean.TRUE;
                continue;
            }
            if (unwrapped.equals(Expressions.FALSE)) {
                this.template[i] = Boolean.FALSE;
                continue;
            }
            this.args.add(arg);
        }
    }

    private static Expression<?> unwrap(Expression<?> expr) {
        if ((expr = ExpressionUtils.extract(expr)) instanceof Operation && ((Operation)expr).getOperator() == Ops.ALIAS) {
            return ((Operation)expr).getArg(0);
        }
        return expr;
    }

    @Override
    public <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this, context);
    }

    @Override
    public List<Expression<?>> getArgs() {
        return this.args;
    }

    @Override
    @Nullable
    public T newInstance(Object ... args) {
        Object[] expanded = new Object[this.template.length];
        System.arraycopy(this.template, 0, expanded, 0, this.template.length);
        int j = 0;
        for (int i = 0; i < expanded.length; ++i) {
            if (expanded[i] != null) continue;
            expanded[i] = args[j++];
        }
        return this.expr.newInstance(expanded);
    }
}

