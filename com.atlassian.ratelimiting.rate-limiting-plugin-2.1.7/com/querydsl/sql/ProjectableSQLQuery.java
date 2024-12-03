/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package com.querydsl.sql;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.FetchableQuery;
import com.querydsl.core.JoinFlag;
import com.querydsl.core.QueryFlag;
import com.querydsl.core.support.FetchableSubQueryBase;
import com.querydsl.core.support.QueryMixin;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.ParamNotSetException;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathExtractor;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimpleOperation;
import com.querydsl.core.types.dsl.SimpleTemplate;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.ForeignKey;
import com.querydsl.sql.RelationalFunctionCall;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLBindings;
import com.querydsl.sql.SQLCommonQuery;
import com.querydsl.sql.SQLOps;
import com.querydsl.sql.SQLSerializer;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.Union;
import com.querydsl.sql.UnionImpl;
import com.querydsl.sql.UnionUtils;
import com.querydsl.sql.WithBuilder;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public abstract class ProjectableSQLQuery<T, Q extends ProjectableSQLQuery<T, Q>>
extends FetchableSubQueryBase<T, Q>
implements SQLCommonQuery<Q>,
FetchableQuery<T, Q> {
    private static final Path<?> defaultQueryAlias = ExpressionUtils.path(Object.class, "query");
    protected final Configuration configuration;
    @Nullable
    protected Expression<?> union;
    protected SubQueryExpression<?> firstUnionSubQuery;
    protected boolean unionAll;

    public ProjectableSQLQuery(QueryMixin<Q> queryMixin, Configuration configuration) {
        super(queryMixin);
        this.queryMixin.setSelf(this);
        this.configuration = configuration;
    }

    @Override
    public <R, C> R accept(Visitor<R, C> v, @Nullable C context) {
        if (this.union != null) {
            return this.union.accept(v, context);
        }
        return super.accept(v, context);
    }

    @Override
    public Q addJoinFlag(String flag) {
        return (Q)this.addJoinFlag(flag, JoinFlag.Position.BEFORE_TARGET);
    }

    @Override
    public Q addJoinFlag(String flag, JoinFlag.Position position) {
        this.queryMixin.addJoinFlag(new JoinFlag(flag, position));
        return (Q)this;
    }

    @Override
    public Q addFlag(QueryFlag.Position position, String prefix, Expression<?> expr) {
        SimpleTemplate<?> flag = Expressions.template(expr.getType(), prefix + "{0}", expr);
        return (Q)((ProjectableSQLQuery)this.queryMixin.addFlag(new QueryFlag(position, flag)));
    }

    public Q addFlag(QueryFlag flag) {
        return (Q)((ProjectableSQLQuery)this.queryMixin.addFlag(flag));
    }

    @Override
    public Q addFlag(QueryFlag.Position position, String flag) {
        return (Q)((ProjectableSQLQuery)this.queryMixin.addFlag(new QueryFlag(position, flag)));
    }

    @Override
    public Q addFlag(QueryFlag.Position position, Expression<?> flag) {
        return (Q)((ProjectableSQLQuery)this.queryMixin.addFlag(new QueryFlag(position, flag)));
    }

    @Override
    public long fetchCount() {
        this.queryMixin.setProjection(Wildcard.countAsInt);
        return ((Number)this.fetchOne()).longValue();
    }

    public Q from(Expression<?> arg) {
        return (Q)((ProjectableSQLQuery)this.queryMixin.from(arg));
    }

    @Override
    public Q from(Expression<?> ... args) {
        return (Q)((ProjectableSQLQuery)this.queryMixin.from(args));
    }

    @Override
    public Q from(SubQueryExpression<?> subQuery, Path<?> alias) {
        return (Q)((ProjectableSQLQuery)this.queryMixin.from(ExpressionUtils.as(subQuery, alias)));
    }

    @Override
    public Q fullJoin(EntityPath<?> target) {
        return (Q)((ProjectableSQLQuery)this.queryMixin.fullJoin(target));
    }

    @Override
    public <E> Q fullJoin(EntityPath<E> target, Path<E> alias) {
        return (Q)((ProjectableSQLQuery)this.queryMixin.fullJoin(target, alias));
    }

    @Override
    public <E> Q fullJoin(RelationalFunctionCall<E> target, Path<E> alias) {
        return (Q)((ProjectableSQLQuery)this.queryMixin.fullJoin(target, alias));
    }

    @Override
    public Q fullJoin(SubQueryExpression<?> target, Path<?> alias) {
        return (Q)((ProjectableSQLQuery)this.queryMixin.fullJoin(target, alias));
    }

    @Override
    public <E> Q fullJoin(ForeignKey<E> key, RelationalPath<E> entity) {
        return ((ProjectableSQLQuery)this.queryMixin.fullJoin(entity)).on(key.on(entity));
    }

    @Override
    public Q innerJoin(EntityPath<?> target) {
        return (Q)((ProjectableSQLQuery)this.queryMixin.innerJoin(target));
    }

    @Override
    public <E> Q innerJoin(EntityPath<E> target, Path<E> alias) {
        return (Q)((ProjectableSQLQuery)this.queryMixin.innerJoin(target, alias));
    }

    @Override
    public <E> Q innerJoin(RelationalFunctionCall<E> target, Path<E> alias) {
        return (Q)((ProjectableSQLQuery)this.queryMixin.innerJoin(target, alias));
    }

    @Override
    public Q innerJoin(SubQueryExpression<?> target, Path<?> alias) {
        return (Q)((ProjectableSQLQuery)this.queryMixin.innerJoin(target, alias));
    }

    @Override
    public <E> Q innerJoin(ForeignKey<E> key, RelationalPath<E> entity) {
        return ((ProjectableSQLQuery)this.queryMixin.innerJoin(entity)).on(key.on(entity));
    }

    @Override
    public Q join(EntityPath<?> target) {
        return (Q)((ProjectableSQLQuery)this.queryMixin.join(target));
    }

    @Override
    public <E> Q join(EntityPath<E> target, Path<E> alias) {
        return (Q)((ProjectableSQLQuery)this.queryMixin.join(target, alias));
    }

    @Override
    public <E> Q join(RelationalFunctionCall<E> target, Path<E> alias) {
        return (Q)((ProjectableSQLQuery)this.queryMixin.join(target, alias));
    }

    @Override
    public Q join(SubQueryExpression<?> target, Path<?> alias) {
        return (Q)((ProjectableSQLQuery)this.queryMixin.join(target, alias));
    }

    @Override
    public <E> Q join(ForeignKey<E> key, RelationalPath<E> entity) {
        return ((ProjectableSQLQuery)this.queryMixin.join(entity)).on(key.on(entity));
    }

    @Override
    public Q leftJoin(EntityPath<?> target) {
        return (Q)((ProjectableSQLQuery)this.queryMixin.leftJoin(target));
    }

    @Override
    public <E> Q leftJoin(EntityPath<E> target, Path<E> alias) {
        return (Q)((ProjectableSQLQuery)this.queryMixin.leftJoin(target, alias));
    }

    @Override
    public <E> Q leftJoin(RelationalFunctionCall<E> target, Path<E> alias) {
        return (Q)((ProjectableSQLQuery)this.queryMixin.leftJoin(target, alias));
    }

    @Override
    public Q leftJoin(SubQueryExpression<?> target, Path<?> alias) {
        return (Q)((ProjectableSQLQuery)this.queryMixin.leftJoin(target, alias));
    }

    @Override
    public <E> Q leftJoin(ForeignKey<E> key, RelationalPath<E> entity) {
        return ((ProjectableSQLQuery)this.queryMixin.leftJoin(entity)).on(key.on(entity));
    }

    @Override
    public Q rightJoin(EntityPath<?> target) {
        return (Q)((ProjectableSQLQuery)this.queryMixin.rightJoin(target));
    }

    @Override
    public <E> Q rightJoin(EntityPath<E> target, Path<E> alias) {
        return (Q)((ProjectableSQLQuery)this.queryMixin.rightJoin(target, alias));
    }

    @Override
    public <E> Q rightJoin(RelationalFunctionCall<E> target, Path<E> alias) {
        return (Q)((ProjectableSQLQuery)this.queryMixin.rightJoin(target, alias));
    }

    @Override
    public Q rightJoin(SubQueryExpression<?> target, Path<?> alias) {
        return (Q)((ProjectableSQLQuery)this.queryMixin.rightJoin(target, alias));
    }

    @Override
    public <E> Q rightJoin(ForeignKey<E> key, RelationalPath<E> entity) {
        return ((ProjectableSQLQuery)this.queryMixin.rightJoin(entity)).on(key.on(entity));
    }

    private <RT> Union<RT> innerUnion(SubQueryExpression<?> ... sq) {
        return this.innerUnion((List<SubQueryExpression<RT>>)ImmutableList.copyOf((Object[])sq));
    }

    private <RT> Union<RT> innerUnion(List<SubQueryExpression<RT>> sq) {
        this.queryMixin.setProjection(sq.get(0).getMetadata().getProjection());
        if (!this.queryMixin.getMetadata().getJoins().isEmpty()) {
            throw new IllegalArgumentException("Don't mix union and from");
        }
        this.union = UnionUtils.union(sq, this.unionAll);
        this.firstUnionSubQuery = sq.get(0);
        return new UnionImpl(this);
    }

    public Q on(Predicate condition) {
        return (Q)((ProjectableSQLQuery)this.queryMixin.on(condition));
    }

    @Override
    public Q on(Predicate ... conditions) {
        return (Q)((ProjectableSQLQuery)this.queryMixin.on(conditions));
    }

    public <RT> Union<RT> union(SubQueryExpression<RT> ... sq) {
        return this.innerUnion(sq);
    }

    public <RT> Union<RT> union(List<SubQueryExpression<RT>> sq) {
        return this.innerUnion(sq);
    }

    public <RT> Q union(Path<?> alias, SubQueryExpression<RT> ... sq) {
        return this.from(UnionUtils.union(ImmutableList.copyOf((Object[])sq), alias, false));
    }

    public <RT> Union<RT> unionAll(SubQueryExpression<RT> ... sq) {
        this.unionAll = true;
        return this.innerUnion(sq);
    }

    public <RT> Union<RT> unionAll(List<SubQueryExpression<RT>> sq) {
        this.unionAll = true;
        return this.innerUnion(sq);
    }

    public <RT> Q unionAll(Path<?> alias, SubQueryExpression<RT> ... sq) {
        return this.from(UnionUtils.union(ImmutableList.copyOf((Object[])sq), alias, true));
    }

    @Override
    public T fetchOne() {
        if (this.getMetadata().getModifiers().getLimit() == null && !this.queryMixin.getMetadata().getProjection().toString().contains("count(")) {
            this.limit(2L);
        }
        CloseableIterator iterator = this.iterate();
        return this.uniqueResult(iterator);
    }

    @Override
    public Q withRecursive(Path<?> alias, SubQueryExpression<?> query) {
        this.queryMixin.addFlag(new QueryFlag(QueryFlag.Position.WITH, SQLTemplates.RECURSIVE));
        return (Q)this.with((Path)alias, (SubQueryExpression)query);
    }

    @Override
    public Q withRecursive(Path<?> alias, Expression<?> query) {
        this.queryMixin.addFlag(new QueryFlag(QueryFlag.Position.WITH, SQLTemplates.RECURSIVE));
        return (Q)this.with((Path)alias, (Expression)query);
    }

    @Override
    public WithBuilder<Q> withRecursive(Path<?> alias, Path<?> ... columns) {
        this.queryMixin.addFlag(new QueryFlag(QueryFlag.Position.WITH, SQLTemplates.RECURSIVE));
        return this.with(alias, columns);
    }

    @Override
    public Q with(Path<?> alias, SubQueryExpression<?> query) {
        Operation expr = ExpressionUtils.operation(alias.getType(), (Operator)SQLOps.WITH_ALIAS, alias, query);
        return (Q)((ProjectableSQLQuery)this.queryMixin.addFlag(new QueryFlag(QueryFlag.Position.WITH, expr)));
    }

    @Override
    public Q with(Path<?> alias, Expression<?> query) {
        Operation expr = ExpressionUtils.operation(alias.getType(), (Operator)SQLOps.WITH_ALIAS, alias, query);
        return (Q)((ProjectableSQLQuery)this.queryMixin.addFlag(new QueryFlag(QueryFlag.Position.WITH, expr)));
    }

    @Override
    public WithBuilder<Q> with(Path<?> alias, Path<?> ... columns) {
        Expression<Object> columnsCombined = ExpressionUtils.list(Object.class, columns);
        SimpleOperation aliasCombined = Expressions.operation(alias.getType(), SQLOps.WITH_COLUMNS, alias, columnsCombined);
        return new WithBuilder(this.queryMixin, aliasCombined);
    }

    protected void clone(Q query) {
        this.union = ((ProjectableSQLQuery)query).union;
        this.unionAll = ((ProjectableSQLQuery)query).unionAll;
        this.firstUnionSubQuery = ((ProjectableSQLQuery)query).firstUnionSubQuery;
    }

    public abstract Q clone();

    protected abstract SQLSerializer createSerializer();

    private Set<Path<?>> getRootPaths(Collection<? extends Expression<?>> exprs) {
        HashSet paths = Sets.newHashSet();
        for (Expression<?> e : exprs) {
            Path path = (Path)e.accept(PathExtractor.DEFAULT, null);
            if (path == null || path.getMetadata().isRoot()) continue;
            paths.add(path.getMetadata().getRootPath());
        }
        return paths;
    }

    private Collection<? extends Expression<?>> expandProjection(Expression<?> expr) {
        if (expr instanceof FactoryExpression) {
            return ((FactoryExpression)expr).getArgs();
        }
        return ImmutableList.of(expr);
    }

    protected SQLSerializer serialize(boolean forCountRow) {
        SQLSerializer serializer = this.createSerializer();
        if (this.union != null) {
            if (this.queryMixin.getMetadata().getProjection() == null || this.expandProjection(this.queryMixin.getMetadata().getProjection()).equals(this.expandProjection(this.firstUnionSubQuery.getMetadata().getProjection()))) {
                serializer.serializeUnion(this.union, this.queryMixin.getMetadata(), this.unionAll);
            } else {
                QueryMixin mixin2 = new QueryMixin(this.queryMixin.getMetadata().clone());
                Set<Path<?>> paths = this.getRootPaths(this.expandProjection(mixin2.getMetadata().getProjection()));
                if (paths.isEmpty()) {
                    mixin2.from(ExpressionUtils.as(this.union, defaultQueryAlias));
                } else if (paths.size() == 1) {
                    mixin2.from(ExpressionUtils.as(this.union, paths.iterator().next()));
                } else {
                    throw new IllegalStateException("Unable to create serialize union");
                }
                serializer.serialize(mixin2.getMetadata(), forCountRow);
            }
        } else {
            serializer.serialize(this.queryMixin.getMetadata(), forCountRow);
        }
        return serializer;
    }

    public SQLBindings getSQL() {
        SQLSerializer serializer = this.serialize(false);
        ImmutableList.Builder args = ImmutableList.builder();
        Map<ParamExpression<?>, Object> params = this.getMetadata().getParams();
        for (Object o : serializer.getConstants()) {
            if (o instanceof ParamExpression) {
                if (!params.containsKey(o)) {
                    throw new ParamNotSetException((ParamExpression)o);
                }
                o = this.queryMixin.getMetadata().getParams().get(o);
            }
            args.add(o);
        }
        return new SQLBindings(serializer.toString(), (ImmutableList<Object>)args.build());
    }

    @Override
    public String toString() {
        SQLSerializer serializer = this.serialize(false);
        return serializer.toString().trim();
    }
}

