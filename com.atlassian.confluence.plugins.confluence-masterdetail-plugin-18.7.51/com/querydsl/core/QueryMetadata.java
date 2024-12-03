/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.querydsl.core;

import com.querydsl.core.JoinExpression;
import com.querydsl.core.JoinFlag;
import com.querydsl.core.JoinType;
import com.querydsl.core.QueryFlag;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Predicate;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public interface QueryMetadata
extends Serializable {
    public void addGroupBy(Expression<?> var1);

    public void addHaving(Predicate var1);

    public void addJoin(JoinType var1, Expression<?> var2);

    public void addJoinFlag(JoinFlag var1);

    public void addJoinCondition(Predicate var1);

    public void addOrderBy(OrderSpecifier<?> var1);

    public void addWhere(Predicate var1);

    public void clearOrderBy();

    public void clearWhere();

    public QueryMetadata clone();

    public List<Expression<?>> getGroupBy();

    @Nullable
    public Predicate getHaving();

    public List<JoinExpression> getJoins();

    public QueryModifiers getModifiers();

    public List<OrderSpecifier<?>> getOrderBy();

    @Nullable
    public Expression<?> getProjection();

    public Map<ParamExpression<?>, Object> getParams();

    @Nullable
    public Predicate getWhere();

    public boolean isDistinct();

    public boolean isUnique();

    public void reset();

    public void setDistinct(boolean var1);

    public void setLimit(@Nullable Long var1);

    public void setModifiers(QueryModifiers var1);

    public void setOffset(@Nullable Long var1);

    public void setUnique(boolean var1);

    public <T> void setParam(ParamExpression<T> var1, T var2);

    public void setProjection(Expression<?> var1);

    public void addFlag(QueryFlag var1);

    public boolean hasFlag(QueryFlag var1);

    public void removeFlag(QueryFlag var1);

    public Set<QueryFlag> getFlags();

    public void setValidate(boolean var1);
}

