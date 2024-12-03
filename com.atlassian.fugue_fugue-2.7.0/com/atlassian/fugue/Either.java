/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Supplier
 */
package com.atlassian.fugue;

import com.atlassian.fugue.Effect;
import com.atlassian.fugue.Eithers;
import com.atlassian.fugue.Functions;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.fugue.Suppliers;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class Either<L, R>
implements Serializable {
    private static final long serialVersionUID = -1L;

    public static <L, R> Either<L, R> left(L left) {
        Preconditions.checkNotNull(left);
        return new Left(left);
    }

    public static <L, R> Either<L, R> right(R right) {
        Preconditions.checkNotNull(right);
        return new Right(right);
    }

    @Deprecated
    public static <T> T merge(Either<T, T> either) {
        return Eithers.merge(either);
    }

    @Deprecated
    public static <L, R> Either<L, R> cond(boolean predicate, R right, L left) {
        return Eithers.cond(predicate, left, right);
    }

    @Deprecated
    public static <X extends Exception, A> A getOrThrow(Either<X, A> either) throws X {
        return Eithers.getOrThrow(either);
    }

    @Deprecated
    public static <L, R> Either<L, Iterable<R>> sequenceRight(Iterable<Either<L, R>> eithers) {
        return Eithers.sequenceRight(eithers);
    }

    @Deprecated
    public static <L, R> Either<Iterable<L>, R> sequenceLeft(Iterable<Either<L, R>> eithers) {
        return Eithers.sequenceLeft(eithers);
    }

    Either() {
    }

    public final LeftProjection left() {
        return new LeftProjection();
    }

    public final RightProjection right() {
        return new RightProjection();
    }

    public final R getOrElse(Supplier<? extends R> supplier) {
        return (R)this.right().getOrElse(supplier);
    }

    public final <X extends R> R getOrElse(X other) {
        return (R)this.right().getOrElse(other);
    }

    public final R getOrNull() {
        return (R)this.right().getOrNull();
    }

    public final R getOrError(Supplier<String> msg) {
        return (R)this.right().getOrError(msg);
    }

    public final <X extends Throwable> R getOrThrow(Supplier<X> ifUndefined) throws X {
        return (R)this.right().getOrThrow((Supplier)ifUndefined);
    }

    public final <X> Either<L, X> map(Function<? super R, X> f) {
        return this.right().map(f);
    }

    public final <X, LL extends L> Either<L, X> flatMap(Function<? super R, Either<LL, X>> f) {
        return this.right().flatMap(f);
    }

    public final boolean exists(Predicate<? super R> p) {
        return this.right().exists(p);
    }

    public final boolean forall(Predicate<? super R> p) {
        return this.right().forall(p);
    }

    public final void foreach(Effect<? super R> effect) {
        this.right().foreach(effect);
    }

    public final Either<L, R> orElse(Either<? extends L, ? extends R> orElse) {
        return this.orElse(Suppliers.ofInstance(orElse));
    }

    public final Either<L, R> orElse(Supplier<? extends Either<? extends L, ? extends R>> orElse) {
        if (this.right().isDefined()) {
            return new Right(this.right().get());
        }
        Either result = (Either)orElse.get();
        return result;
    }

    public final R valueOr(Function<L, ? extends R> or) {
        if (this.right().isDefined()) {
            return this.right().get();
        }
        return (R)or.apply(this.left().get());
    }

    public final Option<Either<L, R>> filter(Predicate<? super R> p) {
        return this.right().filter(p);
    }

    public final Option<R> toOption() {
        return this.right().toOption();
    }

    public <X> Either<L, X> sequence(Either<L, X> e) {
        return this.right().sequence(e);
    }

    public <X> Either<L, X> apply(Either<L, Function<R, X>> either) {
        return this.right().apply(either);
    }

    public final <X> Either<X, R> leftMap(Function<? super L, X> f) {
        return this.left().map(f);
    }

    public abstract boolean isLeft();

    public abstract boolean isRight();

    public abstract Either<R, L> swap();

    public abstract <V> V fold(Function<? super L, V> var1, Function<? super R, V> var2);

    public abstract <LL, RR> Either<LL, RR> bimap(Function<? super L, ? extends LL> var1, Function<? super R, ? extends RR> var2);

    L getLeft() {
        throw new NoSuchElementException();
    }

    R getRight() {
        throw new NoSuchElementException();
    }

    public static interface Projection<A, B, L, R>
    extends Maybe<A> {
        public Either<L, R> either();

        public Option<? super A> toOption();

        public A on(Function<? super B, ? extends A> var1);
    }

    public final class RightProjection
    extends AbstractProjection<R, L>
    implements Projection<R, L, L, R> {
        private RightProjection() {
        }

        @Override
        public R get() {
            return Either.this.getRight();
        }

        @Override
        public boolean isDefined() {
            return Either.this.isRight();
        }

        @Override
        public R on(Function<? super L, ? extends R> f) {
            return Either.this.isRight() ? this.get() : f.apply(Either.this.left().get());
        }

        public <X> Either<L, X> map(Function<? super R, X> f) {
            return Either.this.isRight() ? new Right(f.apply(this.get())) : this.toLeft();
        }

        public <X, LL extends L> Either<L, X> flatMap(Function<? super R, Either<LL, X>> f) {
            if (Either.this.isRight()) {
                Either result = (Either)f.apply(this.get());
                return result;
            }
            return this.toLeft();
        }

        <X> Left<L, X> toLeft() {
            return new Left(Either.this.left().get());
        }

        public <X> Either<L, X> sequence(Either<L, X> e) {
            return this.flatMap(Functions.constant(e));
        }

        public <X> Option<Either<X, R>> filter(Predicate<? super R> f) {
            if (Either.this.isRight() && f.apply(this.get())) {
                Right result = new Right(this.get());
                return Option.some(result);
            }
            return Option.none();
        }

        public <X> Either<L, X> apply(Either<L, Function<R, X>> either) {
            return either.right().flatMap(new Function<Function<R, X>, Either<L, X>>(){

                public Either<L, X> apply(Function<R, X> f) {
                    return RightProjection.this.map(f);
                }
            });
        }

        <X> Either<X, R> as() {
            return Either.right(this.get());
        }
    }

    public final class LeftProjection
    extends AbstractProjection<L, R>
    implements Projection<L, R, L, R> {
        private LeftProjection() {
        }

        @Override
        public L get() {
            return Either.this.getLeft();
        }

        @Override
        public boolean isDefined() {
            return Either.this.isLeft();
        }

        @Override
        public L on(Function<? super R, ? extends L> f) {
            return Either.this.isLeft() ? this.get() : f.apply(Either.this.right().get());
        }

        public <X> Either<X, R> map(Function<? super L, X> f) {
            return Either.this.isLeft() ? new Left(f.apply(this.get())) : this.toRight();
        }

        public <X, RR extends R> Either<X, R> flatMap(Function<? super L, Either<X, RR>> f) {
            if (Either.this.isLeft()) {
                Either result = (Either)f.apply(this.get());
                return result;
            }
            return this.toRight();
        }

        <X> Right<X, R> toRight() {
            return new Right(Either.this.getRight());
        }

        public <X> Either<X, R> sequence(Either<X, R> e) {
            return this.flatMap(Functions.constant(e));
        }

        public <X> Option<Either<L, X>> filter(Predicate<? super L> f) {
            if (Either.this.isLeft() && f.apply(this.get())) {
                Left result = new Left(this.get());
                return Option.some(result);
            }
            return Option.none();
        }

        public <X> Either<X, R> apply(Either<Function<L, X>, R> either) {
            return either.left().flatMap(new Function<Function<L, X>, Either<X, R>>(){

                public Either<X, R> apply(Function<L, X> f) {
                    return LeftProjection.this.map(f);
                }
            });
        }

        <X> Either<L, X> as() {
            return Either.left(this.get());
        }
    }

    abstract class AbstractProjection<A, B>
    implements Projection<A, B, L, R> {
        AbstractProjection() {
        }

        @Override
        public final Iterator<A> iterator() {
            return this.toOption().iterator();
        }

        @Override
        public final Either<L, R> either() {
            return Either.this;
        }

        @Override
        public final boolean isEmpty() {
            return !this.isDefined();
        }

        @Override
        public final Option<A> toOption() {
            return this.isDefined() ? Option.some(this.get()) : Option.none();
        }

        @Override
        public final boolean exists(Predicate<? super A> f) {
            return this.isDefined() && f.apply(this.get());
        }

        @Override
        public final A getOrNull() {
            return this.isDefined() ? (A)this.get() : null;
        }

        @Override
        public final boolean forall(Predicate<? super A> f) {
            return this.isEmpty() || f.apply(this.get());
        }

        @Override
        public final A getOrError(Supplier<String> err) {
            return this.toOption().getOrError(err);
        }

        @Override
        public <X extends Throwable> A getOrThrow(Supplier<X> ifUndefined) throws X {
            return this.toOption().getOrThrow(ifUndefined);
        }

        @Override
        public final A getOrElse(Supplier<? extends A> a) {
            return (A)(this.isDefined() ? this.get() : a.get());
        }

        @Override
        public final <X extends A> A getOrElse(X x) {
            return (A)(this.isDefined() ? this.get() : x);
        }

        @Override
        public final void foreach(Effect<? super A> f) {
            if (this.isDefined()) {
                f.apply(this.get());
            }
        }
    }

    static final class Right<L, R>
    extends Either<L, R> {
        private static final long serialVersionUID = 5025077305715784930L;
        private final R value;

        public Right(R value) {
            Preconditions.checkNotNull(value);
            this.value = value;
        }

        @Override
        final R getRight() {
            return this.value;
        }

        @Override
        public boolean isRight() {
            return true;
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public Either<R, L> swap() {
            return Right.left(this.value);
        }

        @Override
        public <V> V fold(Function<? super L, V> ifLeft, Function<? super R, V> ifRight) {
            return (V)ifRight.apply(this.value);
        }

        @Override
        public <LL, RR> Either<LL, RR> bimap(Function<? super L, ? extends LL> ifLeft, Function<? super R, ? extends RR> ifRight) {
            Either map = this.right().map(ifRight);
            return map;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || !(o instanceof Right)) {
                return false;
            }
            return this.value.equals(((Right)o).value);
        }

        public int hashCode() {
            return this.value.hashCode();
        }

        public String toString() {
            return "Either.Right(" + this.value.toString() + ")";
        }
    }

    static final class Left<L, R>
    extends Either<L, R> {
        private static final long serialVersionUID = -6846704510630179771L;
        private final L value;

        public Left(L value) {
            Preconditions.checkNotNull(value);
            this.value = value;
        }

        @Override
        final L getLeft() {
            return this.value;
        }

        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        public boolean isRight() {
            return false;
        }

        @Override
        public Either<R, L> swap() {
            return Left.right(this.value);
        }

        @Override
        public <V> V fold(Function<? super L, V> ifLeft, Function<? super R, V> ifRight) {
            return (V)ifLeft.apply(this.value);
        }

        @Override
        public <LL, RR> Either<LL, RR> bimap(Function<? super L, ? extends LL> ifLeft, Function<? super R, ? extends RR> ifRight) {
            Either map = this.left().map(ifLeft);
            return map;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || !(o instanceof Left)) {
                return false;
            }
            return this.value.equals(((Left)o).value);
        }

        public int hashCode() {
            return ~this.value.hashCode();
        }

        public String toString() {
            return "Either.Left(" + this.value.toString() + ")";
        }
    }
}

