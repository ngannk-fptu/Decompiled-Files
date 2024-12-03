/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 */
package com.atlassian.streams.api.common;

import com.atlassian.streams.api.common.Fold;
import com.atlassian.streams.api.common.Function2;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.common.Pair;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.Iterator;
import java.util.function.Predicate;

public final class Pairs {
    public static <A> Iterable<Pair<A, A>> mkPairs(Iterable<A> xs) {
        return Fold.foldl(xs, Pair.pair(ImmutableList.of(), Option.none()), Pairs.mkPairs()).first();
    }

    private static <A> Function2<A, Pair<Iterable<Pair<A, A>>, Option<A>>, Pair<Iterable<Pair<A, A>>, Option<A>>> mkPairs() {
        return new MkPairs();
    }

    @Deprecated
    public static <A, B> Function<A, Pair<A, B>> pairWith(B b) {
        return new PairWith(b);
    }

    @Deprecated
    public static <A, B> Iterable<Pair<A, B>> pairWith(B b, Iterable<A> as) {
        return Iterables.transform(as, Pairs.pairWith(b));
    }

    public static <A, B> Iterable<A> firsts(Iterable<Pair<A, B>> pairs) {
        return Iterables.transform(pairs, Pairs.first());
    }

    @Deprecated
    public static <A, B> com.google.common.base.Predicate<Pair<A, B>> withFirst(final com.google.common.base.Predicate<A> p) {
        return new com.google.common.base.Predicate<Pair<A, B>>(){

            public boolean apply(Pair<A, B> pair) {
                return p.apply(pair.first());
            }

            public String toString() {
                return String.format("withFirst(%s)", p);
            }
        };
    }

    public static <A, B> Predicate<Pair<A, B>> withFirst(final Predicate<A> p) {
        return new com.google.common.base.Predicate<Pair<A, B>>(){

            public boolean apply(Pair<A, B> pair) {
                return p.test(pair.first());
            }

            public String toString() {
                return String.format("withFirst(%s)", p);
            }
        };
    }

    @Deprecated
    public static <A, B> com.google.common.base.Predicate<Pair<A, B>> withSecond(final com.google.common.base.Predicate<B> p) {
        return new com.google.common.base.Predicate<Pair<A, B>>(){

            public boolean apply(Pair<A, B> pair) {
                return p.apply(pair.second());
            }

            public String toString() {
                return String.format("withSecond(%s)", p);
            }
        };
    }

    public static <A, B> Predicate<Pair<A, B>> withSecond(final Predicate<B> p) {
        return new com.google.common.base.Predicate<Pair<A, B>>(){

            public boolean apply(Pair<A, B> pair) {
                return p.test(pair.second());
            }

            public String toString() {
                return String.format("withSecond(%s)", p);
            }
        };
    }

    @Deprecated
    public static <A, B> Function<Pair<A, B>, A> first() {
        return new First();
    }

    @Deprecated
    public static <A, B> Function<Pair<A, B>, B> second() {
        return new Second();
    }

    private static final class Second<A, B>
    implements Function<Pair<A, B>, B> {
        private Second() {
        }

        public B apply(Pair<A, B> p) {
            return p.second();
        }
    }

    private static final class First<A, B>
    implements Function<Pair<A, B>, A> {
        private First() {
        }

        public A apply(Pair<A, B> p) {
            return p.first();
        }
    }

    @Deprecated
    private static final class PairWith<A, B>
    implements Function<A, Pair<A, B>> {
        private final B b;

        public PairWith(B b) {
            this.b = b;
        }

        public Pair<A, B> apply(A a) {
            return Pair.pair(a, this.b);
        }

        public String toString() {
            return "pairWith(" + this.b + ")";
        }
    }

    private static final class MkPairs<A>
    implements Function2<A, Pair<Iterable<Pair<A, A>>, Option<A>>, Pair<Iterable<Pair<A, A>>, Option<A>>> {
        private MkPairs() {
        }

        @Override
        public Pair<Iterable<Pair<A, A>>, Option<A>> apply(A v, Pair<Iterable<Pair<A, A>>, Option<A>> intermediate) {
            Iterator<A> iterator = intermediate.second().iterator();
            if (iterator.hasNext()) {
                A a = iterator.next();
                return Pair.pair(Iterables.concat(intermediate.first(), (Iterable)ImmutableList.of(Pair.pair(a, v))), Option.none());
            }
            return Pair.pair(intermediate.first(), Option.some(v));
        }
    }
}

