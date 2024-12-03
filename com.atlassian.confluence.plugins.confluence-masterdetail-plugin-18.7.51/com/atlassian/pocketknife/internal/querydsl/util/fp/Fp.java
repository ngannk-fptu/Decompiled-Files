/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  io.atlassian.fugue.Option
 *  javax.annotation.Nullable
 */
package com.atlassian.pocketknife.internal.querydsl.util.fp;

import com.google.common.base.Preconditions;
import io.atlassian.fugue.Option;
import java.io.Serializable;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class Fp {
    public static <S> Predicate<S> alwaysTrue() {
        return s -> true;
    }

    public static <T> Function<T, T> identity() {
        return t -> t;
    }

    public static <A, B, C> Function<A, C> compose(Function<B, C> g, Function<A, ? extends B> f) {
        return new FunctionComposition<A, B, C>(g, f);
    }

    public static <A, B> Predicate<A> compose(Predicate<B> predicate, Function<A, ? extends B> function) {
        return new CompositionPredicate(predicate, function);
    }

    public static <T, R> Supplier<R> asSupplier(T value, Function<T, R> function) {
        Object apply = function.apply(value);
        return () -> apply;
    }

    public static <T> Option<T> toOption(Optional<T> schema) {
        return Option.option(schema.orElse(null));
    }

    public static <T> Optional<T> toOptional(Option<T> schema) {
        return Optional.ofNullable(schema.getOrNull());
    }

    private static class CompositionPredicate<A, B>
    implements Predicate<A>,
    Serializable {
        final Predicate<B> p;
        final Function<A, ? extends B> f;
        private static final long serialVersionUID = 0L;

        private CompositionPredicate(Predicate<B> p, Function<A, ? extends B> f) {
            this.p = (Predicate)Preconditions.checkNotNull(p);
            this.f = (Function)Preconditions.checkNotNull(f);
        }

        @Override
        public boolean test(A a) {
            return this.p.test(this.f.apply(a));
        }

        public boolean equals(@Nullable Object obj) {
            if (obj instanceof CompositionPredicate) {
                CompositionPredicate that = (CompositionPredicate)obj;
                return this.f.equals(that.f) && this.p.equals(that.p);
            }
            return false;
        }

        public int hashCode() {
            return this.f.hashCode() ^ this.p.hashCode();
        }

        public String toString() {
            return this.p.toString() + "(" + this.f.toString() + ")";
        }
    }

    private static class FunctionComposition<A, B, C>
    implements Function<A, C>,
    Serializable {
        private final Function<B, C> g;
        private final Function<A, ? extends B> f;
        private static final long serialVersionUID = 0L;

        public FunctionComposition(Function<B, C> g, Function<A, ? extends B> f) {
            this.g = (Function)Preconditions.checkNotNull(g);
            this.f = (Function)Preconditions.checkNotNull(f);
        }

        @Override
        public C apply(A a) {
            return this.g.apply(this.f.apply(a));
        }

        public boolean equals(@Nullable Object obj) {
            if (obj instanceof FunctionComposition) {
                FunctionComposition that = (FunctionComposition)obj;
                return this.f.equals(that.f) && this.g.equals(that.g);
            }
            return false;
        }

        public int hashCode() {
            return this.f.hashCode() ^ this.g.hashCode();
        }

        public String toString() {
            return this.g.toString() + "(" + this.f.toString() + ")";
        }
    }
}

