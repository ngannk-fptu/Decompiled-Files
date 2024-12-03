/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.querydsl.sql;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.ProjectionRole;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.RelationalPath;
import java.io.Serializable;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class PrimaryKey<E>
implements Serializable,
ProjectionRole<Tuple> {
    private static final long serialVersionUID = -6913344535043394649L;
    private final RelationalPath<?> entity;
    private final ImmutableList<? extends Path<?>> localColumns;
    @Nullable
    private volatile transient Expression<Tuple> mixin;

    public PrimaryKey(RelationalPath<?> entity, Path<?> ... localColumns) {
        this(entity, ImmutableList.copyOf((Object[])localColumns));
    }

    public PrimaryKey(RelationalPath<?> entity, ImmutableList<? extends Path<?>> localColumns) {
        this.entity = entity;
        this.localColumns = localColumns;
        this.mixin = ExpressionUtils.list(Tuple.class, localColumns);
    }

    public RelationalPath<?> getEntity() {
        return this.entity;
    }

    public List<? extends Path<?>> getLocalColumns() {
        return this.localColumns;
    }

    public BooleanExpression in(CollectionExpression<?, Tuple> coll) {
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

