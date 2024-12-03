/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Iterables
 */
package com.atlassian.fugue;

import com.atlassian.fugue.Either;
import com.atlassian.fugue.Functions;
import com.atlassian.fugue.Iterables;
import com.atlassian.fugue.Option;
import com.atlassian.fugue.Options;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;

public class Eithers {
    private Eithers() {
    }

    public static <T> T merge(Either<T, T> either) {
        if (either.isLeft()) {
            return (T)either.left().get();
        }
        return (T)either.right().get();
    }

    public static <L, R> Either<L, R> cond(boolean predicate, L left, R right) {
        return predicate ? Either.right(right) : Either.left(left);
    }

    public static <X extends Exception, A> A getOrThrow(Either<X, A> either) throws X {
        if (either.isLeft()) {
            throw (Exception)either.left().get();
        }
        return (A)either.right().get();
    }

    public static <L, R> Either<L, Iterable<R>> sequenceRight(Iterable<Either<L, R>> eithers) {
        ImmutableList.Builder it = ImmutableList.builder();
        for (Either<L, R> e : eithers) {
            if (e.isLeft()) {
                return e.left().as();
            }
            it.add(e.right().get());
        }
        return Either.right(it.build());
    }

    public static <L, R> Either<Iterable<L>, R> sequenceLeft(Iterable<Either<L, R>> eithers) {
        Object it = ImmutableList.of();
        for (Either<L, R> e : eithers) {
            if (e.isRight()) {
                return e.right().as();
            }
            it = com.google.common.collect.Iterables.concat((Iterable)it, (Iterable)e.left());
        }
        return Either.left(it);
    }

    public static <L, R> Predicate<Either<L, R>> isLeft() {
        return new Predicate<Either<L, R>>(){

            public boolean apply(Either<L, R> e) {
                return e.isLeft();
            }
        };
    }

    public static <L, R> Predicate<Either<L, R>> isRight() {
        return new Predicate<Either<L, R>>(){

            public boolean apply(Either<L, R> e) {
                return e.isRight();
            }
        };
    }

    public static <L, R> Function<Either<L, R>, Option<L>> leftMapper() {
        return new Function<Either<L, R>, Option<L>>(){

            public Option<L> apply(Either<L, R> either) {
                return either.left().toOption();
            }
        };
    }

    public static <L, R> Function<Either<L, R>, Option<R>> rightMapper() {
        return new Function<Either<L, R>, Option<R>>(){

            public Option<R> apply(Either<L, R> either) {
                return either.right().toOption();
            }
        };
    }

    public static <L, R> Iterable<L> filterLeft(Iterable<Either<L, R>> it) {
        return Iterables.collect(it, Eithers.leftMapper());
    }

    public static <L, R> Iterable<R> filterRight(Iterable<Either<L, R>> it) {
        return Options.flatten(com.google.common.collect.Iterables.transform(it, Eithers.rightMapper()));
    }

    public static <L, R> Function<L, Either<L, R>> toLeft() {
        return new Function<L, Either<L, R>>(){

            public Either<L, R> apply(L from) {
                return Either.left(from);
            }
        };
    }

    public static <L, R> Function<L, Either<L, R>> toLeft(Class<L> leftType, Class<R> rightType) {
        return Eithers.toLeft();
    }

    public static <L, R> Supplier<Either<L, R>> toLeft(L l) {
        return Suppliers.compose(Eithers.toLeft(), (Supplier)Suppliers.ofInstance(l));
    }

    public static <L, R> Supplier<Either<L, R>> toLeft(L l, Class<R> rightType) {
        return Eithers.toLeft(l);
    }

    public static <L, R> Function<R, Either<L, R>> toRight() {
        return new Function<R, Either<L, R>>(){

            public Either<L, R> apply(R from) {
                return Either.right(from);
            }
        };
    }

    public static <L, R> Function<R, Either<L, R>> toRight(Class<L> leftType, Class<R> rightType) {
        return Eithers.toRight();
    }

    public static <L, R> Supplier<Either<L, R>> toRight(R r) {
        return Suppliers.compose(Eithers.toRight(), (Supplier)Suppliers.ofInstance(r));
    }

    public static <L, R> Supplier<Either<L, R>> toRight(Class<L> leftType, R r) {
        return Eithers.toRight(r);
    }

    public static <LL, L extends LL, R> Either<LL, R> upcastLeft(Either<L, R> e) {
        return e.left().map(Functions.identity());
    }

    public static <L, RR, R extends RR> Either<L, RR> upcastRight(Either<L, R> e) {
        return e.right().map(Functions.identity());
    }
}

