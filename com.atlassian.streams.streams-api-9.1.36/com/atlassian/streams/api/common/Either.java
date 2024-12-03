/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Functions
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Iterables
 */
package com.atlassian.streams.api.common;

import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.common.Options;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import java.util.Iterator;

public abstract class Either<L, R> {
    public static <L, R> Either<L, R> left(L left) {
        return new Left(left);
    }

    public static <L, R> Either<L, R> right(R right) {
        return new Right(right);
    }

    private Either() {
    }

    @Deprecated
    public abstract <Z> Z fold(Function<L, Z> var1, Function<R, Z> var2);

    public abstract <Z> Z fold(java.util.function.Function<L, Z> var1, java.util.function.Function<R, Z> var2);

    public final LeftProjection<L, R> left() {
        return new LeftProjection(this);
    }

    public final RightProjection<L, R> right() {
        return new RightProjection(this);
    }

    private static <A, B> Function<A, B> throwNoSuchElementException(final String message) {
        return new Function<A, B>(){

            public B apply(A a) {
                throw new UnsupportedOperationException(message);
            }
        };
    }

    public final boolean isLeft() {
        return (Boolean)this.fold(Functions.forPredicate((Predicate)Predicates.alwaysTrue()), Functions.forPredicate((Predicate)Predicates.alwaysFalse()));
    }

    public final boolean isRight() {
        return (Boolean)this.fold(Functions.forPredicate((Predicate)Predicates.alwaysFalse()), Functions.forPredicate((Predicate)Predicates.alwaysTrue()));
    }

    public static <L, R> Iterable<L> getLefts(Iterable<Either<L, R>> all) {
        return Iterables.transform((Iterable)Iterables.filter(all, (Predicate)new Predicate<Either<L, R>>(){

            public boolean apply(Either<L, R> either) {
                return either.isLeft();
            }
        }), (Function)new Function<Either<L, R>, L>(){

            public L apply(Either<L, R> either) {
                return either.left().get();
            }
        });
    }

    public static <L, R> Iterable<R> getRights(Iterable<Either<L, R>> all) {
        return Iterables.transform((Iterable)Iterables.filter(all, (Predicate)new Predicate<Either<L, R>>(){

            public boolean apply(Either<L, R> either) {
                return either.isRight();
            }
        }), (Function)new Function<Either<L, R>, R>(){

            public R apply(Either<L, R> either) {
                return either.right().get();
            }
        });
    }

    static final class Right<L, R>
    extends Either<L, R> {
        private final R value;

        Right(R value) {
            this.value = Preconditions.checkNotNull(value);
        }

        @Override
        @Deprecated
        public <Z> Z fold(Function<L, Z> l, Function<R, Z> r) {
            return (Z)r.apply(this.value);
        }

        @Override
        public <Z> Z fold(java.util.function.Function<L, Z> l, java.util.function.Function<R, Z> r) {
            return r.apply(this.value);
        }

        public String toString() {
            return String.format("right(%s)", this.value);
        }
    }

    static final class Left<L, R>
    extends Either<L, R> {
        private final L value;

        Left(L value) {
            this.value = Preconditions.checkNotNull(value);
        }

        @Override
        @Deprecated
        public <Z> Z fold(Function<L, Z> l, Function<R, Z> r) {
            return (Z)l.apply(this.value);
        }

        @Override
        public <Z> Z fold(java.util.function.Function<L, Z> l, java.util.function.Function<R, Z> r) {
            return l.apply(this.value);
        }

        public String toString() {
            return String.format("left(%s)", this.value);
        }
    }

    public static final class RightProjection<L, R>
    implements Iterable<R> {
        private final Either<L, R> e;

        public RightProjection(Either<L, R> e) {
            this.e = e;
        }

        public R get() {
            return (R)this.e.fold(Either.throwNoSuchElementException("Either.right().get() on Left"), Functions.identity());
        }

        public Option<R> toOption() {
            return this.e.fold(Options.asNone(), Options.asSome());
        }

        @Override
        public Iterator<R> iterator() {
            return this.toOption().iterator();
        }
    }

    public static final class LeftProjection<L, R>
    implements Iterable<L> {
        private final Either<L, R> e;

        LeftProjection(Either<L, R> e) {
            this.e = e;
        }

        public L get() {
            return (L)this.e.fold(Functions.identity(), Either.throwNoSuchElementException("Either.left().get() on Right"));
        }

        public Option<L> toOption() {
            return this.e.fold(Options.asSome(), Options.asNone());
        }

        @Override
        public Iterator<L> iterator() {
            return this.toOption().iterator();
        }
    }
}

