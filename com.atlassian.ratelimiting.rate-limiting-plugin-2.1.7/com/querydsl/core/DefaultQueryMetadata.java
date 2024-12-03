/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package com.querydsl.core;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.JoinExpression;
import com.querydsl.core.JoinFlag;
import com.querydsl.core.JoinType;
import com.querydsl.core.QueryException;
import com.querydsl.core.QueryFlag;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.ParamsVisitor;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.ValidatingVisitor;
import com.querydsl.core.util.CollectionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public class DefaultQueryMetadata
implements QueryMetadata,
Cloneable {
    private static final long serialVersionUID = 317736313966701232L;
    private boolean distinct;
    private Set<Expression<?>> exprInJoins = ImmutableSet.of();
    private List<Expression<?>> groupBy = ImmutableList.of();
    @Nullable
    private Predicate having;
    private List<JoinExpression> joins = ImmutableList.of();
    private Expression<?> joinTarget;
    private JoinType joinType;
    @Nullable
    private Predicate joinCondition;
    private Set<JoinFlag> joinFlags = ImmutableSet.of();
    private QueryModifiers modifiers = QueryModifiers.EMPTY;
    private List<OrderSpecifier<?>> orderBy = ImmutableList.of();
    @Nullable
    private Expression<?> projection;
    private Map<ParamExpression<?>, Object> params = ImmutableMap.of();
    private boolean unique;
    @Nullable
    private Predicate where;
    private Set<QueryFlag> flags = ImmutableSet.of();
    private boolean extractParams = true;
    private boolean validate = false;
    private ValidatingVisitor validatingVisitor = ValidatingVisitor.DEFAULT;

    private static Predicate and(Predicate lhs, Predicate rhs) {
        if (lhs == null) {
            return rhs;
        }
        return ExpressionUtils.and(lhs, rhs);
    }

    public DefaultQueryMetadata noValidate() {
        this.validate = false;
        return this;
    }

    @Override
    public void addFlag(QueryFlag flag) {
        this.flags = CollectionUtils.addSorted(this.flags, flag);
    }

    @Override
    public void addJoinFlag(JoinFlag flag) {
        this.joinFlags = CollectionUtils.addSorted(this.joinFlags, flag);
    }

    @Override
    public void addGroupBy(Expression<?> o) {
        this.groupBy = CollectionUtils.add(this.groupBy, o);
    }

    @Override
    public void addHaving(Predicate e) {
        if (e == null) {
            return;
        }
        if ((e = (Predicate)ExpressionUtils.extract(e)) != null) {
            this.having = DefaultQueryMetadata.and(this.having, e);
        }
    }

    private void addLastJoin() {
        if (this.joinTarget != null) {
            this.joins = CollectionUtils.add(this.joins, new JoinExpression(this.joinType, this.joinTarget, this.joinCondition, this.joinFlags));
            this.joinType = null;
            this.joinTarget = null;
            this.joinCondition = null;
            this.joinFlags = ImmutableSet.of();
        }
    }

    @Override
    public void addJoin(JoinType joinType, Expression<?> expr) {
        this.addLastJoin();
        if (!this.exprInJoins.contains(expr)) {
            if (expr instanceof Path && ((Path)expr).getMetadata().isRoot()) {
                this.exprInJoins = CollectionUtils.add(this.exprInJoins, expr);
            } else {
                this.validate(expr);
            }
            this.joinType = joinType;
            this.joinTarget = expr;
        } else if (this.validate) {
            throw new IllegalStateException(expr + " is already used");
        }
    }

    @Override
    public void addJoinCondition(Predicate o) {
        this.validate(o);
        this.joinCondition = DefaultQueryMetadata.and(this.joinCondition, o);
    }

    @Override
    public void addOrderBy(OrderSpecifier<?> o) {
        this.orderBy = CollectionUtils.add(this.orderBy, o);
    }

    @Override
    public void setProjection(Expression<?> o) {
        this.validate(o);
        this.projection = o;
    }

    @Override
    public void addWhere(Predicate e) {
        if (e == null) {
            return;
        }
        if ((e = (Predicate)ExpressionUtils.extract(e)) != null) {
            this.validate(e);
            this.where = DefaultQueryMetadata.and(this.where, e);
        }
    }

    @Override
    public void clearOrderBy() {
        this.orderBy = ImmutableList.of();
    }

    @Override
    public void clearWhere() {
        this.where = new BooleanBuilder();
    }

    @Override
    public QueryMetadata clone() {
        try {
            DefaultQueryMetadata clone = (DefaultQueryMetadata)super.clone();
            clone.exprInJoins = CollectionUtils.copyOf(this.exprInJoins);
            clone.groupBy = CollectionUtils.copyOf(this.groupBy);
            clone.having = this.having;
            clone.joins = CollectionUtils.copyOf(this.joins);
            clone.joinTarget = this.joinTarget;
            clone.joinCondition = this.joinCondition;
            clone.joinFlags = CollectionUtils.copyOf(this.joinFlags);
            clone.joinType = this.joinType;
            clone.modifiers = this.modifiers;
            clone.orderBy = CollectionUtils.copyOf(this.orderBy);
            clone.projection = this.projection;
            clone.params = CollectionUtils.copyOf(this.params);
            clone.where = this.where;
            clone.flags = CollectionUtils.copyOfSorted(this.flags);
            return clone;
        }
        catch (CloneNotSupportedException e) {
            throw new QueryException(e);
        }
    }

    @Override
    public List<Expression<?>> getGroupBy() {
        return this.groupBy;
    }

    @Override
    public Predicate getHaving() {
        return this.having;
    }

    @Override
    public List<JoinExpression> getJoins() {
        if (this.joinTarget == null) {
            return this.joins;
        }
        ArrayList j = Lists.newArrayList(this.joins);
        j.add(new JoinExpression(this.joinType, this.joinTarget, this.joinCondition, this.joinFlags));
        return j;
    }

    @Override
    public QueryModifiers getModifiers() {
        return this.modifiers;
    }

    @Override
    public Map<ParamExpression<?>, Object> getParams() {
        return this.params;
    }

    @Override
    public List<OrderSpecifier<?>> getOrderBy() {
        return this.orderBy;
    }

    @Override
    public Expression<?> getProjection() {
        return this.projection;
    }

    @Override
    public Predicate getWhere() {
        return this.where;
    }

    @Override
    public boolean isDistinct() {
        return this.distinct;
    }

    @Override
    public boolean isUnique() {
        return this.unique;
    }

    @Override
    public void reset() {
        this.params = ImmutableMap.of();
        this.modifiers = QueryModifiers.EMPTY;
    }

    @Override
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public void setLimit(Long limit) {
        this.modifiers = this.modifiers == null || this.modifiers.getOffset() == null ? QueryModifiers.limit(limit) : new QueryModifiers(limit, this.modifiers.getOffset());
    }

    @Override
    public void setModifiers(QueryModifiers restriction) {
        if (restriction == null) {
            throw new NullPointerException();
        }
        this.modifiers = restriction;
    }

    @Override
    public void setOffset(Long offset) {
        this.modifiers = this.modifiers == null || this.modifiers.getLimit() == null ? QueryModifiers.offset(offset) : new QueryModifiers(this.modifiers.getLimit(), offset);
    }

    @Override
    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    @Override
    public <T> void setParam(ParamExpression<T> param, T value) {
        this.params = CollectionUtils.put(this.params, param, value);
    }

    @Override
    public Set<QueryFlag> getFlags() {
        return this.flags;
    }

    @Override
    public boolean hasFlag(QueryFlag flag) {
        return this.flags.contains(flag);
    }

    @Override
    public void removeFlag(QueryFlag flag) {
        this.flags = CollectionUtils.removeSorted(this.flags, flag);
    }

    private void validate(Expression<?> expr) {
        if (this.extractParams) {
            expr.accept(ParamsVisitor.DEFAULT, this);
        }
        if (this.validate) {
            this.exprInJoins = (Set)expr.accept(this.validatingVisitor, this.exprInJoins);
        }
    }

    @Override
    public void setValidate(boolean v) {
        this.validate = v;
    }

    public void setValidatingVisitor(ValidatingVisitor visitor) {
        this.validatingVisitor = visitor;
    }

    public boolean equals(Object o) {
        if (o instanceof QueryMetadata) {
            QueryMetadata q = (QueryMetadata)o;
            return q.getFlags().equals(this.flags) && q.getGroupBy().equals(this.groupBy) && Objects.equal((Object)q.getHaving(), (Object)this.having) && q.isDistinct() == this.distinct && q.isUnique() == this.unique && q.getJoins().equals(this.getJoins()) && q.getModifiers().equals(this.modifiers) && q.getOrderBy().equals(this.orderBy) && q.getParams().equals(this.params) && Objects.equal(q.getProjection(), this.projection) && Objects.equal((Object)q.getWhere(), (Object)this.where);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.flags, this.groupBy, this.having, this.getJoins(), this.modifiers, this.orderBy, this.params, this.projection, this.unique, this.where});
    }
}

