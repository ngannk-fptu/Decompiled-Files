/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;

public final class MathExpressions {
    public static <A extends Number> NumberExpression<Double> acos(Expression<A> num) {
        return Expressions.numberOperation(Double.class, Ops.MathOps.ACOS, num);
    }

    public static <A extends Number> NumberExpression<Double> asin(Expression<A> num) {
        return Expressions.numberOperation(Double.class, Ops.MathOps.ASIN, num);
    }

    public static <A extends Number> NumberExpression<Double> atan(Expression<A> num) {
        return Expressions.numberOperation(Double.class, Ops.MathOps.ATAN, num);
    }

    public static <A extends Number> NumberExpression<Double> cos(Expression<A> num) {
        return Expressions.numberOperation(Double.class, Ops.MathOps.COS, num);
    }

    public static <A extends Number> NumberExpression<Double> cosh(Expression<A> num) {
        return Expressions.numberOperation(Double.class, Ops.MathOps.COSH, num);
    }

    public static <A extends Number> NumberExpression<Double> cot(Expression<A> num) {
        return Expressions.numberOperation(Double.class, Ops.MathOps.COT, num);
    }

    public static <A extends Number> NumberExpression<Double> coth(Expression<A> num) {
        return Expressions.numberOperation(Double.class, Ops.MathOps.COTH, num);
    }

    public static <A extends Number> NumberExpression<Double> degrees(Expression<A> num) {
        return Expressions.numberOperation(Double.class, Ops.MathOps.DEG, num);
    }

    public static <A extends Number> NumberExpression<Double> exp(Expression<A> num) {
        return Expressions.numberOperation(Double.class, Ops.MathOps.EXP, num);
    }

    public static <A extends Number> NumberExpression<Double> ln(Expression<A> num) {
        return Expressions.numberOperation(Double.class, Ops.MathOps.LN, num);
    }

    public static <A extends Number> NumberExpression<Double> log(Expression<A> num, int base) {
        return Expressions.numberOperation(Double.class, Ops.MathOps.LOG, num, ConstantImpl.create(base));
    }

    public static <A extends Number> NumberExpression<A> max(Expression<A> left, Expression<A> right) {
        return NumberExpression.max(left, right);
    }

    public static <A extends Number> NumberExpression<A> min(Expression<A> left, Expression<A> right) {
        return NumberExpression.min(left, right);
    }

    public static <A extends Number> NumberExpression<Double> power(Expression<A> num, int exponent) {
        return Expressions.numberOperation(Double.class, Ops.MathOps.POWER, num, ConstantImpl.create(exponent));
    }

    public static <A extends Number> NumberExpression<Double> radians(Expression<A> num) {
        return Expressions.numberOperation(Double.class, Ops.MathOps.RAD, num);
    }

    public static NumberExpression<Double> random() {
        return NumberExpression.random();
    }

    public static NumberExpression<Double> random(int seed) {
        return Expressions.numberOperation(Double.class, Ops.MathOps.RANDOM2, ConstantImpl.create(seed));
    }

    public static <A extends Number> NumberExpression<A> round(Expression<A> num) {
        return Expressions.numberOperation(num.getType(), Ops.MathOps.ROUND, num);
    }

    public static <A extends Number> NumberExpression<A> round(Expression<A> num, int s) {
        return Expressions.numberOperation(num.getType(), Ops.MathOps.ROUND2, num, ConstantImpl.create(s));
    }

    public static <A extends Number> NumberExpression<Integer> sign(Expression<A> num) {
        return Expressions.numberOperation(Integer.class, Ops.MathOps.SIGN, num);
    }

    public static <A extends Number> NumberExpression<Double> sin(Expression<A> num) {
        return Expressions.numberOperation(Double.class, Ops.MathOps.SIN, num);
    }

    public static <A extends Number> NumberExpression<Double> sinh(Expression<A> num) {
        return Expressions.numberOperation(Double.class, Ops.MathOps.SINH, num);
    }

    public static <A extends Number> NumberExpression<Double> tan(Expression<A> num) {
        return Expressions.numberOperation(Double.class, Ops.MathOps.TAN, num);
    }

    public static <A extends Number> NumberExpression<Double> tanh(Expression<A> num) {
        return Expressions.numberOperation(Double.class, Ops.MathOps.TANH, num);
    }

    private MathExpressions() {
    }
}

