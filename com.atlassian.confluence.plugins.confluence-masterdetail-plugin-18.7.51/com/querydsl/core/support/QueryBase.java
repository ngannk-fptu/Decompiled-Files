/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnegative
 */
package com.querydsl.core.support;

import com.querydsl.core.QueryModifiers;
import com.querydsl.core.support.QueryMixin;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Predicate;
import javax.annotation.Nonnegative;

public abstract class QueryBase<Q extends QueryBase<Q>> {
    public static final String MDC_QUERY = "querydsl.query";
    public static final String MDC_PARAMETERS = "querydsl.parameters";
    protected final QueryMixin<Q> queryMixin;

    public QueryBase(QueryMixin<Q> queryMixin) {
        this.queryMixin = queryMixin;
    }

    public Q distinct() {
        return (Q)((QueryBase)this.queryMixin.distinct());
    }

    public Q groupBy(Expression<?> e) {
        return (Q)((QueryBase)this.queryMixin.groupBy(e));
    }

    public Q groupBy(Expression<?> ... o) {
        return (Q)((QueryBase)this.queryMixin.groupBy(o));
    }

    public Q having(Predicate e) {
        return (Q)((QueryBase)this.queryMixin.having(e));
    }

    public Q having(Predicate ... o) {
        return (Q)((QueryBase)this.queryMixin.having(o));
    }

    public Q orderBy(OrderSpecifier<?> o) {
        return (Q)((QueryBase)this.queryMixin.orderBy(o));
    }

    public Q orderBy(OrderSpecifier<?> ... o) {
        return (Q)((QueryBase)this.queryMixin.orderBy(o));
    }

    public Q where(Predicate o) {
        return (Q)((QueryBase)this.queryMixin.where(o));
    }

    public Q where(Predicate ... o) {
        return (Q)((QueryBase)this.queryMixin.where(o));
    }

    public Q limit(@Nonnegative long limit) {
        return (Q)((QueryBase)this.queryMixin.limit(limit));
    }

    public Q offset(long offset) {
        return (Q)((QueryBase)this.queryMixin.offset(offset));
    }

    public Q restrict(QueryModifiers modifiers) {
        return (Q)((QueryBase)this.queryMixin.restrict(modifiers));
    }

    public <P> Q set(ParamExpression<P> param, P value) {
        return (Q)((QueryBase)this.queryMixin.set(param, value));
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof QueryBase) {
            QueryBase q = (QueryBase)o;
            return q.queryMixin.equals(this.queryMixin);
        }
        return false;
    }

    public int hashCode() {
        return this.queryMixin.hashCode();
    }

    public String toString() {
        return this.queryMixin.toString();
    }
}

