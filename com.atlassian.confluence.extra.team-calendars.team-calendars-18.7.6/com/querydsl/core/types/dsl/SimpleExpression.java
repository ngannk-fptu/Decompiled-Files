/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.querydsl.core.types.dsl;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseForEqBuilder;
import com.querydsl.core.types.dsl.DslExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import java.util.Collection;
import javax.annotation.Nullable;

public abstract class SimpleExpression<T>
extends DslExpression<T> {
    private static final long serialVersionUID = -4405387187738167105L;
    @Nullable
    private volatile transient NumberExpression<Long> count;
    @Nullable
    private volatile transient NumberExpression<Long> countDistinct;
    @Nullable
    private volatile transient BooleanExpression isnull;
    @Nullable
    private volatile transient BooleanExpression isnotnull;

    public SimpleExpression(Expression<T> mixin) {
        super(mixin);
    }

    @Override
    public SimpleExpression<T> as(Path<T> alias) {
        return Expressions.operation(this.getType(), Ops.ALIAS, this.mixin, alias);
    }

    @Override
    public SimpleExpression<T> as(String alias) {
        return this.as(ExpressionUtils.path(this.getType(), alias));
    }

    public BooleanExpression isNotNull() {
        if (this.isnotnull == null) {
            this.isnotnull = Expressions.booleanOperation(Ops.IS_NOT_NULL, this.mixin);
        }
        return this.isnotnull;
    }

    public BooleanExpression isNull() {
        if (this.isnull == null) {
            this.isnull = Expressions.booleanOperation(Ops.IS_NULL, this.mixin);
        }
        return this.isnull;
    }

    public NumberExpression<Long> count() {
        if (this.count == null) {
            this.count = Expressions.numberOperation(Long.class, Ops.AggOps.COUNT_AGG, this.mixin);
        }
        return this.count;
    }

    public NumberExpression<Long> countDistinct() {
        if (this.countDistinct == null) {
            this.countDistinct = Expressions.numberOperation(Long.class, Ops.AggOps.COUNT_DISTINCT_AGG, this.mixin);
        }
        return this.countDistinct;
    }

    public BooleanExpression eq(T right) {
        if (right == null) {
            throw new IllegalArgumentException("eq(null) is not allowed. Use isNull() instead");
        }
        return this.eq((Expression<? super T>)ConstantImpl.create(right));
    }

    public BooleanExpression eq(Expression<? super T> right) {
        return Expressions.booleanOperation(Ops.EQ, this.mixin, right);
    }

    public BooleanExpression eqAll(CollectionExpression<?, ? super T> right) {
        return this.eq(ExpressionUtils.all(right));
    }

    public BooleanExpression eqAny(CollectionExpression<?, ? super T> right) {
        return this.eq(ExpressionUtils.any(right));
    }

    public BooleanExpression eqAll(SubQueryExpression<? extends T> right) {
        return this.eq(ExpressionUtils.all(right));
    }

    public BooleanExpression eqAny(SubQueryExpression<? extends T> right) {
        return this.eq(ExpressionUtils.any(right));
    }

    public BooleanExpression in(Collection<? extends T> right) {
        if (right.size() == 1) {
            return this.eq(right.iterator().next());
        }
        return Expressions.booleanOperation(Ops.IN, this.mixin, ConstantImpl.create(right));
    }

    public BooleanExpression in(T ... right) {
        if (right.length == 1) {
            return this.eq(right[0]);
        }
        return Expressions.booleanOperation(Ops.IN, this.mixin, ConstantImpl.create(ImmutableList.copyOf((Object[])right)));
    }

    public BooleanExpression in(CollectionExpression<?, ? extends T> right) {
        return Expressions.booleanOperation(Ops.IN, this.mixin, right);
    }

    public BooleanExpression in(SubQueryExpression<? extends T> right) {
        return Expressions.booleanOperation(Ops.IN, this.mixin, right);
    }

    public BooleanExpression in(Expression<? extends T> ... right) {
        return Expressions.booleanOperation(Ops.IN, this.mixin, Expressions.set(right));
    }

    public BooleanExpression ne(T right) {
        if (right == null) {
            throw new IllegalArgumentException("ne(null) is not allowed. Use isNotNull() instead");
        }
        return this.ne((Expression<? super T>)ConstantImpl.create(right));
    }

    public BooleanExpression ne(Expression<? super T> right) {
        return Expressions.booleanOperation(Ops.NE, this.mixin, right);
    }

    public BooleanExpression neAll(CollectionExpression<?, ? super T> right) {
        return this.ne(ExpressionUtils.all(right));
    }

    public BooleanExpression neAny(CollectionExpression<?, ? super T> right) {
        return this.ne(ExpressionUtils.any(right));
    }

    public BooleanExpression notIn(Collection<? extends T> right) {
        if (right.size() == 1) {
            return this.ne(right.iterator().next());
        }
        return Expressions.booleanOperation(Ops.NOT_IN, this.mixin, ConstantImpl.create(right));
    }

    public BooleanExpression notIn(T ... right) {
        if (right.length == 1) {
            return this.ne(right[0]);
        }
        return Expressions.booleanOperation(Ops.NOT_IN, this.mixin, ConstantImpl.create(ImmutableList.copyOf((Object[])right)));
    }

    public final BooleanExpression notIn(CollectionExpression<?, ? extends T> right) {
        return Expressions.booleanOperation(Ops.NOT_IN, this.mixin, right);
    }

    public final BooleanExpression notIn(SubQueryExpression<? extends T> right) {
        return Expressions.booleanOperation(Ops.NOT_IN, this.mixin, right);
    }

    public final BooleanExpression notIn(Expression<? extends T> ... right) {
        return Expressions.booleanOperation(Ops.NOT_IN, this.mixin, Expressions.list(right));
    }

    public SimpleExpression<T> nullif(Expression<T> other) {
        return Expressions.operation(this.getType(), Ops.NULLIF, this, other);
    }

    public SimpleExpression<T> nullif(T other) {
        return this.nullif((T)ConstantImpl.create(other));
    }

    public CaseForEqBuilder<T> when(T other) {
        return new CaseForEqBuilder<T>(this.mixin, ConstantImpl.create(other));
    }

    public CaseForEqBuilder<T> when(Expression<? extends T> other) {
        return new CaseForEqBuilder<T>(this.mixin, other);
    }
}

