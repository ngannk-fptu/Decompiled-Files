/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 */
package com.querydsl.sql;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.ProjectionRole;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.RelationalPath;
import java.io.Serializable;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class ForeignKey<E>
implements Serializable,
ProjectionRole<Tuple> {
    private static final long serialVersionUID = 2260578033772289023L;
    private final RelationalPath<?> entity;
    private final ImmutableList<? extends Path<?>> localColumns;
    private final ImmutableList<String> foreignColumns;
    @Nullable
    private volatile transient Expression<Tuple> mixin;

    public ForeignKey(RelationalPath<?> entity, Path<?> localColumn, String foreignColumn) {
        this(entity, ImmutableList.of(localColumn), (ImmutableList<String>)ImmutableList.of((Object)foreignColumn));
    }

    public ForeignKey(RelationalPath<?> entity, ImmutableList<? extends Path<?>> localColumns, ImmutableList<String> foreignColumns) {
        this.entity = entity;
        this.localColumns = localColumns;
        this.foreignColumns = foreignColumns;
    }

    public RelationalPath<?> getEntity() {
        return this.entity;
    }

    public List<? extends Path<?>> getLocalColumns() {
        return this.localColumns;
    }

    public List<String> getForeignColumns() {
        return this.foreignColumns;
    }

    public Predicate on(RelationalPath<E> entity) {
        BooleanBuilder builder = new BooleanBuilder();
        for (int i = 0; i < this.localColumns.size(); ++i) {
            Expression local = (Expression)this.localColumns.get(i);
            Path foreign = ExpressionUtils.path(local.getType(), entity, (String)this.foreignColumns.get(i));
            builder.and(ExpressionUtils.eq(local, foreign));
        }
        return builder.getValue();
    }

    public BooleanExpression in(SubQueryExpression<Tuple> coll) {
        return Expressions.booleanOperation(Ops.IN, this.getProjection(), coll);
    }

    @Override
    public Expression<Tuple> getProjection() {
        if (this.mixin == null) {
            this.mixin = ExpressionUtils.list(Tuple.class, this.localColumns);
        }
        return this.mixin;
    }
}

