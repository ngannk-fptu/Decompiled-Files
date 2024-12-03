/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.support;

import com.querydsl.core.QueryMetadata;
import com.querydsl.core.support.ExtendedSubQuery;
import com.querydsl.core.support.FetchableQueryBase;
import com.querydsl.core.support.QueryMixin;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.SubQueryExpressionImpl;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanOperation;
import com.querydsl.core.types.dsl.Expressions;
import java.util.Arrays;
import java.util.Collection;

public abstract class FetchableSubQueryBase<T, Q extends FetchableSubQueryBase<T, Q>>
extends FetchableQueryBase<T, Q>
implements ExtendedSubQuery<T> {
    private final SubQueryExpression<T> mixin;

    public FetchableSubQueryBase(QueryMixin<Q> queryMixin) {
        super(queryMixin);
        this.mixin = new SubQueryExpressionImpl<Object>(Object.class, queryMixin.getMetadata());
    }

    @Override
    public BooleanExpression contains(Expression<? extends T> right) {
        return Expressions.predicate(Ops.IN, right, this);
    }

    @Override
    public BooleanExpression contains(T constant) {
        return this.contains((T)Expressions.constant(constant));
    }

    @Override
    public BooleanExpression exists() {
        QueryMetadata metadata = this.getMetadata();
        if (metadata.getProjection() == null) {
            this.queryMixin.setProjection(Expressions.ONE);
        }
        return Expressions.predicate(Ops.EXISTS, this);
    }

    @Override
    public BooleanExpression eq(Expression<? extends T> expr) {
        return Expressions.predicate(Ops.EQ, this, expr);
    }

    @Override
    public BooleanExpression eq(T constant) {
        return this.eq((T)Expressions.constant(constant));
    }

    @Override
    public BooleanExpression ne(Expression<? extends T> expr) {
        return Expressions.predicate(Ops.NE, this, expr);
    }

    @Override
    public BooleanExpression ne(T constant) {
        return this.eq((T)Expressions.constant(constant));
    }

    @Override
    public BooleanExpression notExists() {
        return this.exists().not();
    }

    @Override
    public BooleanExpression lt(Expression<? extends T> expr) {
        return Expressions.predicate(Ops.LT, this, expr);
    }

    @Override
    public BooleanExpression lt(T constant) {
        return this.lt((T)Expressions.constant(constant));
    }

    @Override
    public BooleanExpression gt(Expression<? extends T> expr) {
        return Expressions.predicate(Ops.GT, this, expr);
    }

    @Override
    public BooleanExpression gt(T constant) {
        return this.gt((T)Expressions.constant(constant));
    }

    @Override
    public BooleanExpression loe(Expression<? extends T> expr) {
        return Expressions.predicate(Ops.LOE, this, expr);
    }

    @Override
    public BooleanExpression loe(T constant) {
        return this.loe((T)Expressions.constant(constant));
    }

    @Override
    public BooleanExpression goe(Expression<? extends T> expr) {
        return Expressions.predicate(Ops.GOE, this, expr);
    }

    @Override
    public BooleanExpression goe(T constant) {
        return this.goe((T)Expressions.constant(constant));
    }

    @Override
    public BooleanOperation isNull() {
        return Expressions.booleanOperation(Ops.IS_NULL, this.mixin);
    }

    @Override
    public BooleanOperation isNotNull() {
        return Expressions.booleanOperation(Ops.IS_NOT_NULL, this.mixin);
    }

    @Override
    public final int hashCode() {
        return this.mixin.hashCode();
    }

    @Override
    public final QueryMetadata getMetadata() {
        return this.queryMixin.getMetadata();
    }

    @Override
    public <R, C> R accept(Visitor<R, C> v, C context) {
        return this.mixin.accept(v, context);
    }

    @Override
    public Class<T> getType() {
        Expression<?> projection = this.queryMixin.getMetadata().getProjection();
        return projection != null ? projection.getType() : Void.class;
    }

    @Override
    public BooleanExpression in(Collection<? extends T> right) {
        if (right.size() == 1) {
            return this.eq(right.iterator().next());
        }
        return Expressions.booleanOperation(Ops.IN, this.mixin, ConstantImpl.create(right));
    }

    @Override
    public BooleanExpression in(T ... right) {
        return this.in((Collection<? extends T>)Arrays.asList(right));
    }
}

