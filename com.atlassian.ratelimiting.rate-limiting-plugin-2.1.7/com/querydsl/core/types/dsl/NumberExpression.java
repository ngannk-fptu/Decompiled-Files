/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
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
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.util.MathUtils;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public abstract class NumberExpression<T extends Number>
extends ComparableExpressionBase<T> {
    private static final long serialVersionUID = -5485902768703364888L;
    @Nullable
    private volatile transient NumberExpression<T> abs;
    @Nullable
    private volatile transient NumberExpression<T> sum;
    @Nullable
    private volatile transient NumberExpression<T> min;
    @Nullable
    private volatile transient NumberExpression<T> max;
    @Nullable
    private volatile transient NumberExpression<T> floor;
    @Nullable
    private volatile transient NumberExpression<T> ceil;
    @Nullable
    private volatile transient NumberExpression<T> round;
    @Nullable
    private volatile transient NumberExpression<Double> avg;
    @Nullable
    private volatile transient NumberExpression<Double> sqrt;
    @Nullable
    private volatile transient NumberExpression<T> negation;
    @Nullable
    private volatile transient StringExpression stringCast;

    public static <A extends Number> NumberExpression<A> max(Expression<A> left, Expression<A> right) {
        return Expressions.numberOperation(left.getType(), Ops.MathOps.MAX, left, right);
    }

    public static <A extends Number> NumberExpression<A> min(Expression<A> left, Expression<A> right) {
        return Expressions.numberOperation(left.getType(), Ops.MathOps.MIN, left, right);
    }

    public static NumberExpression<Double> random() {
        return Constants.RANDOM;
    }

    public NumberExpression(Expression<T> mixin) {
        super(mixin);
    }

    @Override
    public NumberExpression<T> as(Path<T> alias) {
        return Expressions.numberOperation(this.getType(), Ops.ALIAS, this.mixin, alias);
    }

    @Override
    public NumberExpression<T> as(String alias) {
        return Expressions.numberOperation(this.getType(), Ops.ALIAS, this.mixin, ExpressionUtils.path(this.getType(), alias));
    }

    public StringExpression stringValue() {
        if (this.stringCast == null) {
            this.stringCast = Expressions.stringOperation(Ops.STRING_CAST, this.mixin);
        }
        return this.stringCast;
    }

    public NumberExpression<T> abs() {
        if (this.abs == null) {
            this.abs = Expressions.numberOperation(this.getType(), Ops.MathOps.ABS, this.mixin);
        }
        return this.abs;
    }

    public <N extends Number> NumberExpression<T> add(Expression<N> right) {
        return Expressions.numberOperation(this.getType(), Ops.ADD, this.mixin, right);
    }

    public <N extends Number> NumberExpression<T> add(N right) {
        return Expressions.numberOperation(this.getType(), Ops.ADD, this.mixin, ConstantImpl.create(right));
    }

    public NumberExpression<Double> avg() {
        if (this.avg == null) {
            this.avg = Expressions.numberOperation(Double.class, Ops.AggOps.AVG_AGG, this.mixin);
        }
        return this.avg;
    }

    public NumberExpression<Byte> byteValue() {
        return this.castToNum(Byte.class);
    }

    private T cast(Number number) {
        return MathUtils.cast(number, this.getType());
    }

    public <A extends Number> NumberExpression<A> castToNum(Class<A> type) {
        if (type.equals(this.getType())) {
            return this;
        }
        return Expressions.numberOperation(type, Ops.NUMCAST, this.mixin, ConstantImpl.create(type));
    }

    public NumberExpression<T> ceil() {
        if (this.ceil == null) {
            this.ceil = Expressions.numberOperation(this.getType(), Ops.MathOps.CEIL, this.mixin);
        }
        return this.ceil;
    }

    private Class<?> getDivisionType(Class<?> left, Class<?> right) {
        if (!left.equals(right)) {
            return Double.class;
        }
        return left;
    }

    public <N extends Number> NumberExpression<T> divide(Expression<N> right) {
        Class type = this.getDivisionType(this.getType(), right.getType());
        return Expressions.numberOperation(type, Ops.DIV, this.mixin, right);
    }

    public <N extends Number> NumberExpression<T> divide(N right) {
        Class type = this.getDivisionType(this.getType(), right.getClass());
        return Expressions.numberOperation(type, Ops.DIV, this.mixin, ConstantImpl.create(right));
    }

    public NumberExpression<Double> doubleValue() {
        return this.castToNum(Double.class);
    }

    public NumberExpression<Float> floatValue() {
        return this.castToNum(Float.class);
    }

    public NumberExpression<T> floor() {
        if (this.floor == null) {
            this.floor = Expressions.numberOperation(this.getType(), Ops.MathOps.FLOOR, this.mixin);
        }
        return this.floor;
    }

    public final <A extends Number> BooleanExpression goe(A right) {
        return this.goe(ConstantImpl.create(this.cast(right)));
    }

    public final <A extends Number> BooleanExpression goe(Expression<A> right) {
        return Expressions.booleanOperation(Ops.GOE, this.mixin, right);
    }

    public BooleanExpression goeAll(CollectionExpression<?, ? super T> right) {
        return this.goe(ExpressionUtils.all(right));
    }

    public BooleanExpression goeAny(CollectionExpression<?, ? super T> right) {
        return this.goe(ExpressionUtils.any(right));
    }

    public final <A extends Number> BooleanExpression gt(A right) {
        return this.gt(ConstantImpl.create(this.cast(right)));
    }

    public final <A extends Number> BooleanExpression gt(Expression<A> right) {
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

    public final <A extends Number> BooleanExpression between(@Nullable A from, @Nullable A to) {
        if (from == null) {
            if (to != null) {
                return this.loe(to);
            }
            throw new IllegalArgumentException("Either from or to needs to be non-null");
        }
        if (to == null) {
            return this.goe(from);
        }
        return this.between(ConstantImpl.create(this.cast(from)), ConstantImpl.create(this.cast(to)));
    }

    public final <A extends Number> BooleanExpression between(@Nullable Expression<A> from, @Nullable Expression<A> to) {
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

    public final <A extends Number> BooleanExpression notBetween(A from, A to) {
        return this.between(from, to).not();
    }

    public final <A extends Number> BooleanExpression notBetween(Expression<A> from, Expression<A> to) {
        return this.between(from, to).not();
    }

    public NumberExpression<Integer> intValue() {
        return this.castToNum(Integer.class);
    }

    public BooleanExpression like(String str) {
        return Expressions.booleanOperation(Ops.LIKE, this.stringValue(), ConstantImpl.create(str));
    }

    public BooleanExpression like(Expression<String> str) {
        return Expressions.booleanOperation(Ops.LIKE, this.stringValue(), str);
    }

    public final <A extends Number> BooleanExpression loe(A right) {
        return this.loe(ConstantImpl.create(this.cast(right)));
    }

    public final <A extends Number> BooleanExpression loe(Expression<A> right) {
        return Expressions.booleanOperation(Ops.LOE, this.mixin, right);
    }

    public BooleanExpression loeAll(CollectionExpression<?, ? super T> right) {
        return this.loe(ExpressionUtils.all(right));
    }

    public BooleanExpression loeAny(CollectionExpression<?, ? super T> right) {
        return this.loe(ExpressionUtils.any(right));
    }

    public NumberExpression<Long> longValue() {
        return this.castToNum(Long.class);
    }

    public final <A extends Number> BooleanExpression lt(A right) {
        return this.lt(ConstantImpl.create(this.cast(right)));
    }

    public final <A extends Number> BooleanExpression lt(Expression<A> right) {
        return Expressions.booleanOperation(Ops.LT, this, right);
    }

    public BooleanExpression ltAll(CollectionExpression<?, ? super T> right) {
        return this.lt(ExpressionUtils.all(right));
    }

    public BooleanExpression ltAny(CollectionExpression<?, ? super T> right) {
        return this.lt(ExpressionUtils.any(right));
    }

    public NumberExpression<T> max() {
        if (this.max == null) {
            this.max = Expressions.numberOperation(this.getType(), Ops.AggOps.MAX_AGG, this.mixin);
        }
        return this.max;
    }

    public NumberExpression<T> min() {
        if (this.min == null) {
            this.min = Expressions.numberOperation(this.getType(), Ops.AggOps.MIN_AGG, this.mixin);
        }
        return this.min;
    }

    public NumberExpression<T> mod(Expression<T> num) {
        return Expressions.numberOperation(this.getType(), Ops.MOD, this.mixin, num);
    }

    public NumberExpression<T> mod(T num) {
        return Expressions.numberOperation(this.getType(), Ops.MOD, this.mixin, ConstantImpl.create(num));
    }

    public <N extends Number> NumberExpression<T> multiply(Expression<N> right) {
        return Expressions.numberOperation(this.getType(), Ops.MULT, this.mixin, right);
    }

    public <N extends Number> NumberExpression<T> multiply(N right) {
        return Expressions.numberOperation(this.getType(), Ops.MULT, this.mixin, ConstantImpl.create(right));
    }

    public NumberExpression<T> negate() {
        if (this.negation == null) {
            this.negation = Expressions.numberOperation(this.getType(), Ops.NEGATE, this.mixin);
        }
        return this.negation;
    }

    public NumberExpression<T> round() {
        if (this.round == null) {
            this.round = Expressions.numberOperation(this.getType(), Ops.MathOps.ROUND, this.mixin);
        }
        return this.round;
    }

    public NumberExpression<Short> shortValue() {
        return this.castToNum(Short.class);
    }

    public NumberExpression<Double> sqrt() {
        if (this.sqrt == null) {
            this.sqrt = Expressions.numberOperation(Double.class, Ops.MathOps.SQRT, this.mixin);
        }
        return this.sqrt;
    }

    public <N extends Number> NumberExpression<T> subtract(Expression<N> right) {
        return Expressions.numberOperation(this.getType(), Ops.SUB, this.mixin, right);
    }

    public <N extends Number> NumberExpression<T> subtract(N right) {
        return Expressions.numberOperation(this.getType(), Ops.SUB, this.mixin, ConstantImpl.create(right));
    }

    public NumberExpression<T> sum() {
        if (this.sum == null) {
            this.sum = Expressions.numberOperation(this.getType(), Ops.AggOps.SUM_AGG, this.mixin);
        }
        return this.sum;
    }

    public BooleanExpression in(Number ... numbers) {
        return super.in(this.convert(numbers));
    }

    public BooleanExpression notIn(Number ... numbers) {
        return super.notIn(this.convert(numbers));
    }

    private List<T> convert(Number ... numbers) {
        ArrayList list = new ArrayList(numbers.length);
        for (int i = 0; i < numbers.length; ++i) {
            list.add(MathUtils.cast(numbers[i], this.getType()));
        }
        return list;
    }

    private static class Constants {
        private static final NumberExpression<Double> RANDOM = Expressions.numberOperation(Double.class, Ops.MathOps.RANDOM, new Expression[0]);

        private Constants() {
        }
    }
}

