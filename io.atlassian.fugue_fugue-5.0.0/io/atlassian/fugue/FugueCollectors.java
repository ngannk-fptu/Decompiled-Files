/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.fugue;

import io.atlassian.fugue.Checked;
import io.atlassian.fugue.Either;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.Try;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class FugueCollectors {
    public static <L, R> Collector<Either<L, R>, ?, Either<List<L>, R>> toEitherLeft() {
        return FugueCollectors.toEitherLeft(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    public static <L, R, A, B> Collector<Either<L, R>, ?, Either<B, R>> toEitherLeft(Collector<L, A, B> lCollector) {
        Objects.requireNonNull(lCollector);
        return Collector.of(() -> new Ref(Either.left(lCollector.supplier().get())), (ref, either) -> ((Ref)ref).update(acc -> acc.left().flatMap(a -> either.leftMap(l -> {
            lCollector.accumulator().accept(a, l);
            return a;
        }))), (refL, refR) -> new Ref(((Either)((Ref)refL).get()).left().flatMap(lv -> ((Either)((Ref)refR).get()).leftMap(rv -> lCollector.combiner().apply(lv, rv)))), ref -> ((Either)((Ref)ref).get()).leftMap(a -> lCollector.finisher().apply(a)), FugueCollectors.maybeUnorderedCharacteristics(lCollector));
    }

    public static <L, R> Collector<Either<L, R>, ?, Either<L, List<R>>> toEitherRight() {
        return FugueCollectors.toEitherRight(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    public static <L, R, A, B> Collector<Either<L, R>, ?, Either<L, B>> toEitherRight(Collector<R, A, B> rCollector) {
        Objects.requireNonNull(rCollector);
        return Collector.of(() -> new Ref(Either.right(rCollector.supplier().get())), (ref, either) -> ((Ref)ref).update(acc -> acc.flatMap(a -> either.map(r -> {
            rCollector.accumulator().accept(a, r);
            return a;
        }))), (refL, refR) -> new Ref(((Either)((Ref)refL).get()).flatMap(lv -> ((Either)((Ref)refR).get()).map(rv -> rCollector.combiner().apply(lv, rv)))), ref -> ((Either)((Ref)ref).get()).map(a -> rCollector.finisher().apply(a)), FugueCollectors.maybeUnorderedCharacteristics(rCollector));
    }

    public static <A> Collector<Option<A>, ?, List<A>> flatten() {
        return FugueCollectors.flatten(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    public static <A, B, C> Collector<Option<A>, ?, C> flatten(Collector<A, B, C> aCollector) {
        Objects.requireNonNull(aCollector);
        return Collector.of(aCollector.supplier(), (acc, option) -> option.forEach(a -> aCollector.accumulator().accept(acc, a)), aCollector.combiner(), aCollector.finisher(), aCollector.characteristics().toArray(new Collector.Characteristics[0]));
    }

    public static <A> Collector<Try<A>, ?, Try<List<A>>> toTrySuccess() {
        return FugueCollectors.toTrySuccess(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    public static <A, B, C> Collector<Try<A>, ?, Try<C>> toTrySuccess(Collector<A, B, C> aCollector) {
        Objects.requireNonNull(aCollector);
        return Collector.of(() -> new Ref(Checked.now(() -> Objects.requireNonNull(aCollector.supplier().get()))), (ref, aTry) -> ((Ref)ref).update(acc -> acc.flatMap(b -> aTry.map(a -> {
            aCollector.accumulator().accept(b, a);
            return b;
        }))), (refL, refR) -> new Ref(((Try)((Ref)refL).get()).flatMap(lv -> ((Try)((Ref)refR).get()).map(rv -> aCollector.combiner().apply(lv, rv)))), ref -> ((Try)((Ref)ref).get()).map(b -> aCollector.finisher().apply(b)), FugueCollectors.maybeUnorderedCharacteristics(aCollector));
    }

    private static Collector.Characteristics[] maybeUnorderedCharacteristics(Collector<?, ?, ?> delegate) {
        Collector.Characteristics[] characteristicsArray;
        if (delegate.characteristics().contains((Object)Collector.Characteristics.UNORDERED)) {
            Collector.Characteristics[] characteristicsArray2 = new Collector.Characteristics[1];
            characteristicsArray = characteristicsArray2;
            characteristicsArray2[0] = Collector.Characteristics.UNORDERED;
        } else {
            characteristicsArray = new Collector.Characteristics[]{};
        }
        return characteristicsArray;
    }

    private static final class Ref<A> {
        private A value;

        private Ref(A value) {
            this.value = value;
        }

        private A get() {
            return this.value;
        }

        private void update(UnaryOperator<A> update) {
            this.value = update.apply(this.value);
        }
    }
}

