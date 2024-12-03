/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.fugue;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Semigroup;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.function.Function;

public final class Semigroups {
    public static final Semigroup<Integer> intMaximum = Math::max;
    public static final Semigroup<Integer> intMinimum = Math::min;
    public static final Semigroup<BigInteger> bigintMaximum = BigInteger::max;
    public static final Semigroup<BigInteger> bigintMinimum = BigInteger::min;
    public static final Semigroup<BigDecimal> bigDecimalMaximum = BigDecimal::max;
    public static final Semigroup<BigDecimal> bigDecimalMinimum = BigDecimal::min;
    public static final Semigroup<Long> longMaximum = Math::max;
    public static final Semigroup<Long> longMinimum = Math::min;

    private Semigroups() {
    }

    public static <A> Semigroup<A> first() {
        return (x, y) -> x;
    }

    public static <A> Semigroup<A> last() {
        return (x, y) -> y;
    }

    public static <A, B> Semigroup<Function<A, B>> function(Semigroup<B> sb) {
        return (a1, a2) -> a -> sb.append(a1.apply(a), a2.apply(a));
    }

    public static <A> Semigroup<A> max(Comparator<A> comparator) {
        return (a1, a2) -> comparator.compare(a1, a2) < 0 ? a2 : a1;
    }

    public static <A> Semigroup<A> min(Comparator<A> comparator) {
        return (a1, a2) -> comparator.compare(a1, a2) > 0 ? a2 : a1;
    }

    public static <A extends Comparable<A>> Semigroup<A> max() {
        return (a1, a2) -> a1.compareTo(a2) < 0 ? a2 : a1;
    }

    public static <A extends Comparable<A>> Semigroup<A> min() {
        return (a1, a2) -> a1.compareTo(a2) > 0 ? a2 : a1;
    }

    public static <L, R> Semigroup<Either<L, R>> either(Semigroup<L> lS, Semigroup<R> rS) {
        return (e1, e2) -> e1.fold(l1 -> e2.fold(l2 -> Either.left(lS.append(l1, l2)), r2 -> e1), r1 -> e2.fold(l2 -> e2, r2 -> Either.right(rS.append(r1, r2))));
    }
}

