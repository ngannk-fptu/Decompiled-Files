/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.pocketknife.api.querydsl.tuple;

import com.atlassian.pocketknife.api.querydsl.stream.ClosePromise;
import com.atlassian.pocketknife.api.querydsl.stream.CloseableIterable;
import com.atlassian.pocketknife.api.querydsl.tuple.TupleMapper;
import com.atlassian.pocketknife.internal.querydsl.stream.CloseableIterableImpl;
import com.atlassian.pocketknife.internal.querydsl.util.fp.Fp;
import com.google.common.base.Preconditions;
import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.NumberExpression;
import java.math.BigDecimal;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Tuples {
    private static final TupleMapper tupleMapple = new TupleMapper();

    public static <T> Expression<T>[] tupleOf(Expression<T> expression) {
        return new Expression[]{expression};
    }

    public static <T> CloseableIterable<T> map(CloseableIterator<Tuple> closeableIterator, Function<Tuple, T> f) {
        return new CloseableIterableImpl<Tuple, T>(closeableIterator, f, ClosePromise.NOOP());
    }

    public static <T> CloseableIterable<T> map(CloseableIterator<Tuple> closeableIterator, Function<Tuple, T> f, ClosePromise closePromise) {
        return new CloseableIterableImpl<Tuple, T>(closeableIterator, f, closePromise);
    }

    public static <T> CloseableIterable<T> take(CloseableIterator<Tuple> closeableIterator, Function<Tuple, T> f, ClosePromise closePromise, int n) {
        Preconditions.checkArgument((n >= 0 ? 1 : 0) != 0, (Object)"take(n) must be >= 0");
        Predicate<Tuple> nTaken = CloseableIterableImpl.nTakenPredicate(n);
        return Tuples.takeWhile(closeableIterator, f, closePromise, nTaken);
    }

    public static <T> CloseableIterable<T> takeWhile(CloseableIterator<Tuple> closeableIterator, Function<Tuple, T> f, ClosePromise closePromise, Predicate<Tuple> takeWhilePredicate) {
        return new CloseableIterableImpl<Tuple, T>(closeableIterator, f, closePromise, Fp.alwaysTrue(), takeWhilePredicate);
    }

    public static <T> CloseableIterable<T> filter(CloseableIterator<Tuple> closeableIterator, Function<Tuple, T> f, ClosePromise closePromise, Predicate<Tuple> filterPredicate) {
        return new CloseableIterableImpl<Tuple, T>(closeableIterator, f, closePromise, filterPredicate, Fp.alwaysTrue());
    }

    public static void foreach(CloseableIterator<Tuple> closeableIterator, Consumer<Tuple> effect) {
        Tuples.foreach(closeableIterator, effect, ClosePromise.NOOP());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void foreach(CloseableIterator<Tuple> closeableIterator, Consumer<Tuple> effect, ClosePromise closePromise) {
        try {
            while (closeableIterator.hasNext()) {
                Tuple t = (Tuple)closeableIterator.next();
                effect.accept(t);
            }
        }
        finally {
            Tuples.closeQuietly(closeableIterator);
            closePromise.close();
        }
    }

    public static <T> Function<Tuple, T> column(Expression<T> expr) {
        return tupleMapple.column(expr);
    }

    public static <T extends Number> Function<Tuple, BigDecimal> toBigDecimal(NumberExpression<T> expr) {
        return tupleMapple.toBigDecimal(expr);
    }

    public static <T extends Number> Function<Tuple, Long> toLong(NumberExpression<T> expr) {
        return tupleMapple.toLong(expr);
    }

    public static <T extends Number> Function<Tuple, Float> toFloat(NumberExpression<T> expr) {
        return tupleMapple.toFloat(expr);
    }

    public static <T extends Number> Function<Tuple, Integer> toInt(NumberExpression<T> expr) {
        return tupleMapple.toInt(expr);
    }

    public static <T extends Number> Function<Tuple, Double> toDouble(NumberExpression<T> expr) {
        return tupleMapple.toDouble(expr);
    }

    private static void closeQuietly(CloseableIterator<Tuple> closeableIterator) {
        try {
            closeableIterator.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

