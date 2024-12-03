/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 */
package com.atlassian.upm.api.util;

import com.atlassian.upm.api.util.Option;
import com.google.common.base.Function;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
    public <Z> Z fold(Function<L, Z> l, Function<R, Z> r) {
        return this.fold((java.util.function.Function<L, Z>)l, (java.util.function.Function<R, Z>)r);
    }

    public abstract <Z> Z fold(java.util.function.Function<L, Z> var1, java.util.function.Function<R, Z> var2);

    public final LeftProjection<L, R> left() {
        return new LeftProjection(this);
    }

    public final RightProjection<L, R> right() {
        return new RightProjection(this);
    }

    private static <A, B> java.util.function.Function<A, B> throwNoSuchElementException(String message) {
        return a -> {
            throw new UnsupportedOperationException(message);
        };
    }

    public final boolean isLeft() {
        return (Boolean)this.fold(ignored -> true, ignored -> false);
    }

    public final boolean isRight() {
        return (Boolean)this.fold(ignored -> false, ignored -> true);
    }

    public static <L, R> Iterable<L> getLefts(Iterable<Either<L, R>> all) {
        return StreamSupport.stream(all.spliterator(), false).filter(Either::isLeft).map(either -> either.left().get()).collect(Collectors.toList());
    }

    public static <L, R> Iterable<R> getRights(Iterable<Either<L, R>> all) {
        return StreamSupport.stream(all.spliterator(), false).filter(Either::isRight).map(either -> either.right().get()).collect(Collectors.toList());
    }

    static final class Right<L, R>
    extends Either<L, R> {
        private final R value;

        Right(R value) {
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public <Z> Z fold(java.util.function.Function<L, Z> l, java.util.function.Function<R, Z> r) {
            return r.apply(this.value);
        }

        public boolean equals(Object other) {
            return other instanceof Right && this.value.equals(((Right)other).value);
        }

        public int hashCode() {
            return this.value.hashCode();
        }

        public String toString() {
            return String.format("right(%s)", this.value);
        }
    }

    static final class Left<L, R>
    extends Either<L, R> {
        private final L value;

        Left(L value) {
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public <Z> Z fold(java.util.function.Function<L, Z> l, java.util.function.Function<R, Z> r) {
            return l.apply(this.value);
        }

        public boolean equals(Object other) {
            return other instanceof Left && this.value.equals(((Left)other).value);
        }

        public int hashCode() {
            return this.value.hashCode();
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
            return (R)this.e.fold(Either.throwNoSuchElementException("Either.right().get() on Left"), java.util.function.Function.identity());
        }

        public Option<R> toOption() {
            return (Option)this.e.fold(ignored -> Option.none(), Option::some);
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
            return (L)this.e.fold(java.util.function.Function.identity(), Either.throwNoSuchElementException("Either.left().get() on Right"));
        }

        public Option<L> toOption() {
            return (Option)this.e.fold(Option::some, ignored -> Option.none());
        }

        @Override
        public Iterator<L> iterator() {
            return this.toOption().iterator();
        }
    }
}

