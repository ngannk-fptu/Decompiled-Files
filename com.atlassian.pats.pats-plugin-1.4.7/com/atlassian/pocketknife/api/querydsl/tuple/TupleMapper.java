/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.pocketknife.api.querydsl.tuple;

import com.atlassian.annotations.Internal;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.NumberExpression;
import java.math.BigDecimal;
import java.util.function.Function;

@Internal
class TupleMapper {
    TupleMapper() {
    }

    <T> Function<Tuple, T> column(Expression<T> expr) {
        return tuple -> {
            Object t = tuple.get(expr);
            return t == null ? null : t;
        };
    }

    <T extends Number> Function<Tuple, Integer> toInt(NumberExpression<T> expr) {
        return tuple -> {
            Number t = (Number)tuple.get(expr);
            return t == null ? null : Integer.valueOf(t.intValue());
        };
    }

    <T extends Number> Function<Tuple, Long> toLong(NumberExpression<T> expr) {
        return tuple -> {
            Number t = (Number)tuple.get(expr);
            return t == null ? null : Long.valueOf(t.longValue());
        };
    }

    <T extends Number> Function<Tuple, Float> toFloat(NumberExpression<T> expr) {
        return tuple -> {
            Number t = (Number)tuple.get(expr);
            return t == null ? null : Float.valueOf(t.floatValue());
        };
    }

    <T extends Number> Function<Tuple, Double> toDouble(NumberExpression<T> expr) {
        return tuple -> {
            Number t = (Number)tuple.get(expr);
            return t == null ? null : Double.valueOf(t.doubleValue());
        };
    }

    <T extends Number> Function<Tuple, BigDecimal> toBigDecimal(NumberExpression<T> expr) {
        return tuple -> {
            Number t = (Number)tuple.get(expr);
            if (t instanceof BigDecimal) {
                return (BigDecimal)t;
            }
            return t == null ? null : new BigDecimal(t.doubleValue());
        };
    }
}

