/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.fugue;

import io.atlassian.fugue.Iterables;
import io.atlassian.fugue.Pair;
import io.atlassian.fugue.Semigroup;
import java.util.Collections;

public interface Monoid<A>
extends Semigroup<A> {
    public A zero();

    default public A sum(Iterable<A> as) {
        return Semigroup.super.sumNonEmpty(this.zero(), as);
    }

    default public A multiply(int n, A a) {
        return n <= 0 ? this.zero() : Semigroup.super.multiply1p(n - 1, a);
    }

    default public A intersperse(Iterable<? extends A> as, A a) {
        return this.sum(Iterables.intersperse(as, a));
    }

    @Override
    default public A sumNonEmpty(A head, Iterable<A> tail) {
        return this.sum(Iterables.concat(Collections.singletonList(head), tail));
    }

    @Override
    default public A multiply1p(int n, A a) {
        return n == Integer.MAX_VALUE ? this.append(a, this.multiply(n, a)) : this.multiply(n + 1, a);
    }

    public static <A, B> Monoid<Pair<A, B>> compose(final Monoid<A> ma, final Monoid<B> mb) {
        final Pair<A, B> zero = Pair.pair(ma.zero(), mb.zero());
        return new Monoid<Pair<A, B>>(){

            @Override
            public Pair<A, B> append(Pair<A, B> p1, Pair<A, B> p2) {
                return Pair.pair(ma.append(p1.left(), p2.left()), mb.append(p1.right(), p2.right()));
            }

            @Override
            public Pair<A, B> zero() {
                return zero;
            }
        };
    }

    public static <A> Monoid<A> dual(final Monoid<A> monoid) {
        return new Monoid<A>(){

            @Override
            public A append(A a1, A a2) {
                return monoid.append(a2, a1);
            }

            @Override
            public A zero() {
                return monoid.zero();
            }

            @Override
            public A multiply(int n, A a) {
                return monoid.multiply(n, a);
            }
        };
    }
}

