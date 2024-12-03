/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.support;

import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.JoinFlag;
import com.querydsl.core.JoinType;
import com.querydsl.core.QueryFlag;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.support.CollectionAnyVisitor;
import com.querydsl.core.support.Context;
import com.querydsl.core.support.ReplaceVisitor;
import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.FactoryExpressionUtils;
import com.querydsl.core.types.MapExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.ProjectionRole;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.SubQueryExpression;
import javax.annotation.Nullable;

public class QueryMixin<T> {
    private final QueryMetadata metadata;
    private final boolean expandAnyPaths;
    private final ReplaceVisitor<Void> replaceVisitor = new ReplaceVisitor<Void>(){

        @Override
        public Expression<?> visit(Path<?> expr, @Nullable Void context) {
            return QueryMixin.this.normalizePath(expr);
        }
    };
    protected final CollectionAnyVisitor collectionAnyVisitor = new CollectionAnyVisitor();
    private T self;

    public QueryMixin() {
        this(null, new DefaultQueryMetadata(), true);
    }

    public QueryMixin(QueryMetadata metadata) {
        this(null, metadata, true);
    }

    public QueryMixin(QueryMetadata metadata, boolean expandAnyPaths) {
        this(null, metadata, expandAnyPaths);
    }

    public QueryMixin(T self) {
        this(self, new DefaultQueryMetadata(), true);
    }

    public QueryMixin(T self, QueryMetadata metadata) {
        this(self, metadata, true);
    }

    public QueryMixin(T self, QueryMetadata metadata, boolean expandAnyPaths) {
        this.self = self;
        this.metadata = metadata;
        this.expandAnyPaths = expandAnyPaths;
    }

    public T addJoin(JoinType joinType, Expression<?> target) {
        this.metadata.addJoin(joinType, target);
        return this.self;
    }

    public T addFlag(QueryFlag queryFlag) {
        this.metadata.addFlag(queryFlag);
        return this.self;
    }

    public T addJoinFlag(JoinFlag flag) {
        this.metadata.addJoinFlag(flag);
        return this.self;
    }

    public T removeFlag(QueryFlag queryFlag) {
        this.metadata.removeFlag(queryFlag);
        return this.self;
    }

    public <E> Expression<E> setProjection(Expression<E> e) {
        e = this.convert(e, Role.SELECT);
        this.metadata.setProjection(e);
        return e;
    }

    public Expression<?> setProjection(Expression<?> ... o) {
        return this.setProjection(Projections.tuple(o));
    }

    private <P extends Path<?>> P assertRoot(P p) {
        if (!p.getRoot().equals(p)) {
            throw new IllegalArgumentException(p + " is not a root path");
        }
        return p;
    }

    private Path<?> normalizePath(Path<?> expr) {
        Context context = new Context();
        Path replaced = (Path)expr.accept(this.collectionAnyVisitor, context);
        if (!replaced.equals(expr)) {
            for (int i = 0; i < context.paths.size(); ++i) {
                Path<?> path = context.paths.get(i).getMetadata().getParent();
                Path replacement = context.replacements.get(i);
                this.innerJoin(path, replacement);
            }
            return replaced;
        }
        return expr;
    }

    public <RT> Expression<RT> convert(Expression<RT> expr, Role role) {
        if (this.expandAnyPaths) {
            if (expr instanceof Path) {
                expr = this.normalizePath((Path)expr);
            } else if (expr != null) {
                expr = (Expression)expr.accept(this.replaceVisitor, null);
            }
        }
        if (expr instanceof ProjectionRole) {
            return this.convert(((ProjectionRole)((Object)expr)).getProjection(), role);
        }
        if (expr instanceof FactoryExpression && !(expr instanceof FactoryExpressionUtils.FactoryExpressionAdapter)) {
            return FactoryExpressionUtils.wrap((FactoryExpression)expr);
        }
        return expr;
    }

    protected Predicate convert(Predicate condition, Role role) {
        return condition;
    }

    protected <D> Expression<D> createAlias(Expression<?> expr, Path<D> alias) {
        this.assertRoot(alias);
        return ExpressionUtils.as(expr, alias);
    }

    public final T distinct() {
        this.metadata.setDistinct(true);
        return this.self;
    }

    public final T from(Expression<?> arg) {
        this.metadata.addJoin(JoinType.DEFAULT, arg);
        return this.self;
    }

    public final T from(Expression<?> ... args) {
        for (Expression<?> arg : args) {
            this.metadata.addJoin(JoinType.DEFAULT, arg);
        }
        return this.self;
    }

    public final T fullJoin(Expression<?> target) {
        this.metadata.addJoin(JoinType.FULLJOIN, target);
        return this.self;
    }

    public final <P> T fullJoin(Expression<P> target, Path<P> alias) {
        this.metadata.addJoin(JoinType.FULLJOIN, this.createAlias(target, alias));
        return this.self;
    }

    public final <P> T fullJoin(CollectionExpression<?, P> target, Path<P> alias) {
        this.metadata.addJoin(JoinType.FULLJOIN, this.createAlias(target, alias));
        return this.self;
    }

    public final <P> T fullJoin(MapExpression<?, P> target, Path<P> alias) {
        this.metadata.addJoin(JoinType.FULLJOIN, this.createAlias(target, alias));
        return this.self;
    }

    public final <P> T fullJoin(SubQueryExpression<P> target, Path<?> alias) {
        this.metadata.addJoin(JoinType.FULLJOIN, this.createAlias(target, alias));
        return this.self;
    }

    public final QueryMetadata getMetadata() {
        return this.metadata;
    }

    public final T getSelf() {
        return this.self;
    }

    public final T groupBy(Expression<?> e) {
        e = this.convert(e, Role.GROUP_BY);
        this.metadata.addGroupBy(e);
        return this.self;
    }

    public final T groupBy(Expression<?> ... o) {
        for (Expression<?> e : o) {
            this.groupBy(e);
        }
        return this.self;
    }

    public final T having(Predicate e) {
        this.metadata.addHaving(this.convert(e, Role.HAVING));
        return this.self;
    }

    public final T having(Predicate ... o) {
        for (Predicate e : o) {
            this.metadata.addHaving(this.convert(e, Role.HAVING));
        }
        return this.self;
    }

    public final <P> T innerJoin(Expression<P> target) {
        this.metadata.addJoin(JoinType.INNERJOIN, target);
        return this.self;
    }

    public final <P> T innerJoin(Expression<P> target, Path<P> alias) {
        this.metadata.addJoin(JoinType.INNERJOIN, this.createAlias(target, alias));
        return this.self;
    }

    public final <P> T innerJoin(CollectionExpression<?, P> target, Path<P> alias) {
        this.metadata.addJoin(JoinType.INNERJOIN, this.createAlias(target, alias));
        return this.self;
    }

    public final <P> T innerJoin(MapExpression<?, P> target, Path<P> alias) {
        this.metadata.addJoin(JoinType.INNERJOIN, this.createAlias(target, alias));
        return this.self;
    }

    public final <P> T innerJoin(SubQueryExpression<P> target, Path<?> alias) {
        this.metadata.addJoin(JoinType.INNERJOIN, this.createAlias(target, alias));
        return this.self;
    }

    public final boolean isDistinct() {
        return this.metadata.isDistinct();
    }

    public final boolean isUnique() {
        return this.metadata.isUnique();
    }

    public final <P> T join(Expression<P> target) {
        this.metadata.addJoin(JoinType.JOIN, target);
        return this.self;
    }

    public final <P> T join(Expression<P> target, Path<P> alias) {
        this.metadata.addJoin(JoinType.JOIN, this.createAlias(target, alias));
        return this.getSelf();
    }

    public final <P> T join(CollectionExpression<?, P> target, Path<P> alias) {
        this.metadata.addJoin(JoinType.JOIN, this.createAlias(target, alias));
        return this.getSelf();
    }

    public final <P> T join(MapExpression<?, P> target, Path<P> alias) {
        this.metadata.addJoin(JoinType.JOIN, this.createAlias(target, alias));
        return this.getSelf();
    }

    public final <P> T join(SubQueryExpression<P> target, Path<?> alias) {
        this.metadata.addJoin(JoinType.JOIN, this.createAlias(target, alias));
        return this.self;
    }

    public final <P> T leftJoin(Expression<P> target) {
        this.metadata.addJoin(JoinType.LEFTJOIN, target);
        return this.self;
    }

    public final <P> T leftJoin(Expression<P> target, Path<P> alias) {
        this.metadata.addJoin(JoinType.LEFTJOIN, this.createAlias(target, alias));
        return this.getSelf();
    }

    public final <P> T leftJoin(CollectionExpression<?, P> target, Path<P> alias) {
        this.metadata.addJoin(JoinType.LEFTJOIN, this.createAlias(target, alias));
        return this.getSelf();
    }

    public final <P> T leftJoin(MapExpression<?, P> target, Path<P> alias) {
        this.metadata.addJoin(JoinType.LEFTJOIN, this.createAlias(target, alias));
        return this.getSelf();
    }

    public final <P> T leftJoin(SubQueryExpression<P> target, Path<?> alias) {
        this.metadata.addJoin(JoinType.LEFTJOIN, this.createAlias(target, alias));
        return this.self;
    }

    public final T limit(long limit) {
        this.metadata.setLimit(limit);
        return this.self;
    }

    public final T offset(long offset) {
        this.metadata.setOffset(offset);
        return this.self;
    }

    public final T on(Predicate condition) {
        this.metadata.addJoinCondition(this.convert(condition, Role.FROM));
        return this.self;
    }

    public final T on(Predicate ... conditions) {
        for (Predicate condition : conditions) {
            this.metadata.addJoinCondition(this.convert(condition, Role.FROM));
        }
        return this.self;
    }

    public final T orderBy(OrderSpecifier<?> spec) {
        Expression<?> e = this.convert(spec.getTarget(), Role.ORDER_BY);
        if (!spec.getTarget().equals(e)) {
            this.metadata.addOrderBy(new OrderSpecifier(spec.getOrder(), e, spec.getNullHandling()));
        } else {
            this.metadata.addOrderBy(spec);
        }
        return this.self;
    }

    public final T orderBy(OrderSpecifier<?> ... o) {
        for (OrderSpecifier<?> spec : o) {
            this.orderBy(spec);
        }
        return this.self;
    }

    public final T restrict(QueryModifiers modifiers) {
        this.metadata.setModifiers(modifiers);
        return this.self;
    }

    public final <P> T rightJoin(Expression<P> target) {
        this.metadata.addJoin(JoinType.RIGHTJOIN, target);
        return this.self;
    }

    public final <P> T rightJoin(Expression<P> target, Path<P> alias) {
        this.metadata.addJoin(JoinType.RIGHTJOIN, this.createAlias(target, alias));
        return this.getSelf();
    }

    public final <P> T rightJoin(CollectionExpression<?, P> target, Path<P> alias) {
        this.metadata.addJoin(JoinType.RIGHTJOIN, this.createAlias(target, alias));
        return this.getSelf();
    }

    public final <P> T rightJoin(MapExpression<?, P> target, Path<P> alias) {
        this.metadata.addJoin(JoinType.RIGHTJOIN, this.createAlias(target, alias));
        return this.getSelf();
    }

    public final <P> T rightJoin(SubQueryExpression<P> target, Path<?> alias) {
        this.metadata.addJoin(JoinType.RIGHTJOIN, this.createAlias(target, alias));
        return this.self;
    }

    public final <P> T set(ParamExpression<P> param, P value) {
        this.metadata.setParam(param, value);
        return this.self;
    }

    public final void setDistinct(boolean distinct) {
        this.metadata.setDistinct(distinct);
    }

    public final void setSelf(T self) {
        this.self = self;
    }

    public final void setUnique(boolean unique) {
        this.metadata.setUnique(unique);
    }

    public final T where(Predicate e) {
        this.metadata.addWhere(this.convert(e, Role.WHERE));
        return this.self;
    }

    public final T where(Predicate ... o) {
        for (Predicate e : o) {
            this.metadata.addWhere(this.convert(e, Role.WHERE));
        }
        return this.self;
    }

    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof QueryMixin) {
            QueryMixin q = (QueryMixin)o;
            return q.metadata.equals(this.metadata);
        }
        return false;
    }

    public int hashCode() {
        return this.metadata.hashCode();
    }

    public String toString() {
        return this.metadata.toString();
    }

    public static enum Role {
        SELECT,
        FROM,
        WHERE,
        GROUP_BY,
        HAVING,
        ORDER_BY;

    }
}

