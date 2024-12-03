/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryResults;
import com.querydsl.core.support.FetchableQueryBase;
import com.querydsl.core.support.FetchableSubQueryBase;
import com.querydsl.core.support.QueryBase;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Visitor;
import com.querydsl.sql.ProjectableSQLQuery;
import com.querydsl.sql.Union;
import java.util.List;
import javax.annotation.Nullable;

public class UnionImpl<T, Q extends ProjectableSQLQuery<T, Q>>
implements Union<T> {
    private final Q query;

    public UnionImpl(Q query) {
        this.query = query;
    }

    @Override
    public List<T> list() {
        return ((FetchableQueryBase)this.query).fetch();
    }

    @Override
    public List<T> fetch() {
        return ((FetchableQueryBase)this.query).fetch();
    }

    @Override
    public T fetchFirst() {
        return ((FetchableQueryBase)this.query).fetchFirst();
    }

    @Override
    public T fetchOne() {
        return ((ProjectableSQLQuery)this.query).fetchOne();
    }

    @Override
    public CloseableIterator<T> iterate() {
        return this.query.iterate();
    }

    @Override
    public QueryResults<T> fetchResults() {
        return this.query.fetchResults();
    }

    @Override
    public long fetchCount() {
        return ((ProjectableSQLQuery)this.query).fetchCount();
    }

    @Override
    public Union<T> groupBy(Expression<?> ... o) {
        ((QueryBase)this.query).groupBy(o);
        return this;
    }

    @Override
    public Union<T> having(Predicate ... o) {
        ((QueryBase)this.query).having(o);
        return this;
    }

    @Override
    public Union<T> orderBy(OrderSpecifier<?> ... o) {
        ((QueryBase)this.query).orderBy(o);
        return this;
    }

    @Override
    public Expression<T> as(String alias) {
        return ExpressionUtils.as(this, alias);
    }

    @Override
    public Expression<T> as(Path<T> alias) {
        return ExpressionUtils.as(this, alias);
    }

    public String toString() {
        return ((ProjectableSQLQuery)this.query).toString();
    }

    @Override
    @Nullable
    public <R, C> R accept(Visitor<R, C> v, @Nullable C context) {
        return ((ProjectableSQLQuery)this.query).accept(v, context);
    }

    @Override
    public Class<? extends T> getType() {
        return ((FetchableSubQueryBase)this.query).getType();
    }

    @Override
    public QueryMetadata getMetadata() {
        return ((FetchableSubQueryBase)this.query).getMetadata();
    }
}

