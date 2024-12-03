/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.fugue;

import io.atlassian.fugue.Functions;
import io.atlassian.fugue.Iterables;
import io.atlassian.fugue.Maybe;
import io.atlassian.fugue.Option;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class Options {
    private Options() {
        throw new UnsupportedOperationException("This class is not instantiable.");
    }

    public static <A> Option<A> find(Iterable<Option<A>> options) {
        for (Option<A> option : options) {
            if (!option.isDefined()) continue;
            return option;
        }
        return Option.none();
    }

    public static <AA, A extends AA> Option<AA> upcast(Option<A> o) {
        return o.map(Functions.identity());
    }

    public static <A, B> Function<Option<A>, Option<B>> lift(Function<A, B> f) {
        Objects.requireNonNull(f);
        return oa -> oa.map(f);
    }

    public static <A, B> Function<Function<A, B>, Function<Option<A>, Option<B>>> lift() {
        return Options::lift;
    }

    public static <A> Predicate<Option<A>> lift(Predicate<? super A> pred) {
        Objects.requireNonNull(pred);
        return oa -> oa.exists(pred);
    }

    public static <A, B> Option<B> ap(Option<A> oa, Option<Function<A, B>> of) {
        return of.fold(Option.noneSupplier(), Functions.compose(Functions.apply(oa), Options.lift()));
    }

    public static <A, B, C> BiFunction<Option<A>, Option<B>, Option<C>> lift2(BiFunction<A, B, C> f2) {
        Function<A, Function<B, C>> curried = Functions.curried(f2);
        Function lifted = Options.lift(curried);
        return (oa, ob) -> {
            Option ofbc = (Option)lifted.apply((Option)oa);
            return Options.ap(ob, ofbc);
        };
    }

    public static <A, B, C> Function<BiFunction<A, B, C>, BiFunction<Option<A>, Option<B>, Option<C>>> lift2() {
        return Options::lift2;
    }

    public static <A> Iterable<Option<A>> filterNone(Iterable<Option<A>> options) {
        return Iterables.filter(options, Maybe::isDefined);
    }

    public static <A> Iterable<A> flatten(Iterable<Option<A>> options) {
        return Iterables.map(Options.filterNone(options), Maybe::get);
    }

    public static <A> Function<A, Option<A>> toOption() {
        return Option::option;
    }

    public static <A, B> Function<A, Option<B>> nullSafe(Function<A, B> nullProducing) {
        return nullProducing.andThen(Options.toOption());
    }
}

