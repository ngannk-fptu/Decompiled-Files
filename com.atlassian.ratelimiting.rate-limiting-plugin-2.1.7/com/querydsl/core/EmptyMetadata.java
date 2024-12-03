/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core;

import com.querydsl.core.JoinExpression;
import com.querydsl.core.JoinFlag;
import com.querydsl.core.JoinType;
import com.querydsl.core.QueryFlag;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Predicate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class EmptyMetadata
implements QueryMetadata {
    private static final long serialVersionUID = 134750105981272499L;
    public static final QueryMetadata DEFAULT = new EmptyMetadata();

    @Override
    public void addGroupBy(Expression<?> o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addHaving(Predicate o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addJoin(JoinType joinType, Expression<?> expr) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addJoinCondition(Predicate o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addOrderBy(OrderSpecifier<?> o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setProjection(Expression<?> o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addWhere(Predicate o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearOrderBy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearWhere() {
        throw new UnsupportedOperationException();
    }

    @Override
    public QueryMetadata clone() {
        return this;
    }

    @Override
    public List<Expression<?>> getGroupBy() {
        return Collections.emptyList();
    }

    @Override
    public Predicate getHaving() {
        return null;
    }

    @Override
    public List<JoinExpression> getJoins() {
        return Collections.emptyList();
    }

    @Override
    public QueryModifiers getModifiers() {
        return null;
    }

    @Override
    public List<OrderSpecifier<?>> getOrderBy() {
        return Collections.emptyList();
    }

    @Override
    public Expression<?> getProjection() {
        return null;
    }

    @Override
    public Map<ParamExpression<?>, Object> getParams() {
        return Collections.emptyMap();
    }

    @Override
    public Predicate getWhere() {
        return null;
    }

    @Override
    public boolean isDistinct() {
        return false;
    }

    @Override
    public boolean isUnique() {
        return false;
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDistinct(boolean distinct) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLimit(Long limit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setModifiers(QueryModifiers restriction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setOffset(Long offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setUnique(boolean unique) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> void setParam(ParamExpression<T> param, T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addFlag(QueryFlag flag) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasFlag(QueryFlag flag) {
        return false;
    }

    @Override
    public Set<QueryFlag> getFlags() {
        return Collections.emptySet();
    }

    @Override
    public void setValidate(boolean v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addJoinFlag(JoinFlag flag) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeFlag(QueryFlag flag) {
    }
}

