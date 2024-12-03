/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.Expressions;
import javax.annotation.Nullable;

public abstract class ComparableExpression<T extends Comparable>
extends ComparableExpressionBase<T> {
    private static final long serialVersionUID = 5761359576767404270L;

    public ComparableExpression(Expression<T> mixin) {
        super(mixin);
    }

    @Override
    public ComparableExpression<T> as(Path<T> alias) {
        return Expressions.comparableOperation(this.getType(), Ops.ALIAS, this.mixin, alias);
    }

    @Override
    public ComparableExpression<T> as(String alias) {
        return this.as(ExpressionUtils.path(this.getType(), alias));
    }

    public final BooleanExpression between(@Nullable T from, @Nullable T to) {
        if (from == null) {
            if (to != null) {
                return Expressions.booleanOperation(Ops.LOE, this.mixin, ConstantImpl.create(to));
            }
            throw new IllegalArgumentException("Either from or to needs to be non-null");
        }
        if (to == null) {
            return Expressions.booleanOperation(Ops.GOE, this.mixin, ConstantImpl.create(from));
        }
        return Expressions.booleanOperation(Ops.BETWEEN, this.mixin, ConstantImpl.create(from), ConstantImpl.create(to));
    }

    public final BooleanExpression between(@Nullable Expression<T> from, @Nullable Expression<T> to) {
        if (from == null) {
            if (to != null) {
                return Expressions.booleanOperation(Ops.LOE, this.mixin, to);
            }
            throw new IllegalArgumentException("Either from or to needs to be non-null");
        }
        if (to == null) {
            return Expressions.booleanOperation(Ops.GOE, this.mixin, from);
        }
        return Expressions.booleanOperation(Ops.BETWEEN, this.mixin, from, to);
    }

    public final BooleanExpression notBetween(T from, T to) {
        return this.between(from, to).not();
    }

    public final BooleanExpression notBetween(Expression<T> from, Expression<T> to) {
        return this.between(from, to).not();
    }

    public BooleanExpression gt(T right) {
        return this.gt(ConstantImpl.create(right));
    }

    public BooleanExpression gt(Expression<T> right) {
        return Expressions.booleanOperation(Ops.GT, this.mixin, right);
    }

    public BooleanExpression gtAll(CollectionExpression<?, ? super T> right) {
        return this.gt(ExpressionUtils.all(right));
    }

    public BooleanExpression gtAny(CollectionExpression<?, ? super T> right) {
        return this.gt(ExpressionUtils.any(right));
    }

    public BooleanExpression gtAll(SubQueryExpression<? extends T> right) {
        return this.gt(ExpressionUtils.all(right));
    }

    public BooleanExpression gtAny(SubQueryExpression<? extends T> right) {
        return this.gt(ExpressionUtils.any(right));
    }

    public BooleanExpression goe(T right) {
        return this.goe(ConstantImpl.create(right));
    }

    public BooleanExpression goe(Expression<T> right) {
        return Expressions.booleanOperation(Ops.GOE, this.mixin, right);
    }

    public BooleanExpression goeAll(CollectionExpression<?, ? super T> right) {
        return this.goe(ExpressionUtils.all(right));
    }

    public BooleanExpression goeAny(CollectionExpression<?, ? super T> right) {
        return this.goe(ExpressionUtils.any(right));
    }

    public BooleanExpression goeAll(SubQueryExpression<? extends T> right) {
        return this.goe(ExpressionUtils.all(right));
    }

    public BooleanExpression goeAny(SubQueryExpression<? extends T> right) {
        return this.goe(ExpressionUtils.any(right));
    }

    public final BooleanExpression lt(T right) {
        return this.lt(ConstantImpl.create(right));
    }

    public final BooleanExpression lt(Expression<T> right) {
        return Expressions.booleanOperation(Ops.LT, this.mixin, right);
    }

    public BooleanExpression ltAll(CollectionExpression<?, ? super T> right) {
        return this.lt(ExpressionUtils.all(right));
    }

    public BooleanExpression ltAny(CollectionExpression<?, ? super T> right) {
        return this.lt(ExpressionUtils.any(right));
    }

    public BooleanExpression ltAll(SubQueryExpression<? extends T> right) {
        return this.lt(ExpressionUtils.all(right));
    }

    public BooleanExpression ltAny(SubQueryExpression<? extends T> right) {
        return this.lt(ExpressionUtils.any(right));
    }

    public final BooleanExpression loe(T right) {
        return Expressions.booleanOperation(Ops.LOE, this.mixin, ConstantImpl.create(right));
    }

    public final BooleanExpression loe(Expression<T> right) {
        return Expressions.booleanOperation(Ops.LOE, this.mixin, right);
    }

    public BooleanExpression loeAll(CollectionExpression<?, ? super T> right) {
        return this.loe(ExpressionUtils.all(right));
    }

    public BooleanExpression loeAny(CollectionExpression<?, ? super T> right) {
        return this.loe(ExpressionUtils.any(right));
    }

    public BooleanExpression loeAll(SubQueryExpression<? extends T> right) {
        return this.loe(ExpressionUtils.all(right));
    }

    public BooleanExpression loeAny(SubQueryExpression<? extends T> right) {
        return this.loe(ExpressionUtils.any(right));
    }
}

