/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.fugue;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Functions;
import io.atlassian.fugue.Iterables;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.Options;
import io.atlassian.fugue.Suppliers;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

    public static <L, R> Predicate<Either<L, R>> isLeft() {
        return Either::isLeft;
    }

    public static <L, R> Predicate<Either<L, R>> isRight() {
        return Either::isRight;
    }

    public static <L, R> Function<Either<L, R>, Option<L>> leftMapper() {
        return either -> either.left().toOption();
    }

    public static <L, R> Function<Either<L, R>, Option<R>> rightMapper() {
        return either -> either.right().toOption();
    }

    public static <L, R> Function<L, Either<L, R>> toLeft() {
        return Either::left;
    }

    public static <L, R> Function<L, Either<L, R>> toLeft(Class<L> leftType, Class<R> rightType) {
        return Eithers.toLeft();
    }

    public static <L, R> Supplier<Either<L, R>> toLeft(L l) {
        return Suppliers.compose(Eithers.toLeft(), Suppliers.ofInstance(l));
    }

    public static <L, R> Supplier<Either<L, R>> toLeft(L l, Class<R> rightType) {
        return Eithers.toLeft(l);
    }

    public static <L, R> Function<R, Either<L, R>> toRight() {
        return Either::right;
    }

    public static <L, R> Function<R, Either<L, R>> toRight(Class<L> leftType, Class<R> rightType) {
        return Eithers.toRight();
    }

    public static <L, R> Supplier<Either<L, R>> toRight(R r) {
        return Suppliers.compose(Eithers.toRight(), Suppliers.ofInstance(r));
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

    public static <L, R> Iterable<L> filterLeft(Iterable<Either<L, R>> it) {
        return Iterables.collect(it, Eithers.leftMapper());
    }

    public static <L, R> Iterable<R> filterRight(Iterable<Either<L, R>> it) {
        return Options.flatten(Iterables.map(it, Eithers.rightMapper()));
    }

    public static <L, R> Either<L, Iterable<R>> sequenceRight(Iterable<Either<L, R>> eithers) {
        return Eithers.sequenceRight(eithers, Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    public static <L, R, A, C> Either<L, C> sequenceRight(Iterable<Either<L, R>> eithers, Collector<R, A, C> collector) {
        A accumulator = collector.supplier().get();
        for (Either<L, R> e : eithers) {
            if (e.isLeft()) {
                return e.left().as();
            }
            collector.accumulator().accept(accumulator, e.right().get());
        }
        return Either.right(collector.finisher().apply(accumulator));
    }

    public static <L, R> Either<Iterable<L>, R> sequenceLeft(Iterable<Either<L, R>> eithers) {
        return Eithers.sequenceLeft(eithers, Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    public static <L, R, A, C> Either<C, R> sequenceLeft(Iterable<Either<L, R>> eithers, Collector<L, A, C> collector) {
        A accumulator = collector.supplier().get();
        for (Either<L, R> e : eithers) {
            if (e.isRight()) {
                return e.right().as();
            }
            collector.accumulator().accept(accumulator, e.left().get());
        }
        return Either.left(collector.finisher().apply(accumulator));
    }
}

