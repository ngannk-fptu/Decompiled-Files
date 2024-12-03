/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.MutableExpressionBase;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.DslExpression;
import com.querydsl.core.types.dsl.EnumExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.TimeExpression;
import java.util.ArrayList;
import java.util.List;

public class Coalesce<T extends Comparable>
extends MutableExpressionBase<T> {
    private static final long serialVersionUID = 445439522266250417L;
    private final List<Expression<? extends T>> exprs = new ArrayList<Expression<? extends T>>();
    private volatile transient ComparableExpression<T> value;

    public Coalesce(Class<? extends T> type, Expression<?> ... exprs) {
        super(type);
        for (Expression<?> expr : exprs) {
            this.add((T)expr);
        }
    }

    public Coalesce(Expression ... exprs) {
        this(exprs.length > 0 ? exprs[0].getType() : Object.class, exprs);
    }

    @Override
    public <R, C> R accept(Visitor<R, C> v, C context) {
        return this.getValue().accept(v, context);
    }

    public ComparableExpression<T> getValue() {
        if (this.value == null) {
            this.value = Expressions.comparableOperation(this.getType(), Ops.COALESCE, this.getExpressionList());
        }
        return this.value;
    }

    public DslExpression<T> as(Path<T> alias) {
        return Expressions.dslOperation(this.getType(), Ops.ALIAS, this, alias);
    }

    public DslExpression<T> as(String alias) {
        return this.as(ExpressionUtils.path(this.getType(), alias));
    }

    public final Coalesce<T> add(Expression<T> expr) {
        this.value = null;
        this.exprs.add(expr);
        return this;
    }

    public OrderSpecifier<T> asc() {
        return this.getValue().asc();
    }

    public OrderSpecifier<T> desc() {
        return this.getValue().desc();
    }

    public final Coalesce<T> add(T constant) {
        return this.add((T)ConstantImpl.create(constant));
    }

    public BooleanExpression asBoolean() {
        return Expressions.booleanOperation(Ops.COALESCE, this.getExpressionList());
    }

    public DateExpression<T> asDate() {
        return Expressions.dateOperation(this.getType(), Ops.COALESCE, this.getExpressionList());
    }

    public DateTimeExpression<T> asDateTime() {
        return Expressions.dateTimeOperation(this.getType(), Ops.COALESCE, this.getExpressionList());
    }

    public EnumExpression<?> asEnum() {
        return Expressions.enumOperation(this.getType(), Ops.COALESCE, this.getExpressionList());
    }

    public NumberExpression<?> asNumber() {
        return Expressions.numberOperation(this.getType(), Ops.COALESCE, this.getExpressionList());
    }

    public StringExpression asString() {
        return Expressions.stringOperation(Ops.COALESCE, this.getExpressionList());
    }

    public TimeExpression<T> asTime() {
        return Expressions.timeOperation(this.getType(), Ops.COALESCE, this.getExpressionList());
    }

    private Expression<?> getExpressionList() {
        return ExpressionUtils.list(this.getType(), this.exprs);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Coalesce) {
            Coalesce c = (Coalesce)o;
            return c.exprs.equals(this.exprs);
        }
        return false;
    }
}

