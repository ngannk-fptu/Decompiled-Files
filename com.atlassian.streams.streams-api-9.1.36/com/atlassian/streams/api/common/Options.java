/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.base.Supplier
 *  com.google.common.collect.Iterables
 *  org.joda.time.DateTime
 */
package com.atlassian.streams.api.common;

import com.atlassian.streams.api.common.Option;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import java.util.Optional;
import java.util.stream.Stream;
import org.joda.time.DateTime;

@Deprecated
public final class Options {
    private Options() {
    }

    public static <A> Iterable<A> catOptions(Iterable<Option<A>> as) {
        return Iterables.concat(as);
    }

    public static <A> Option<A> find(Iterable<Option<A>> as) {
        for (Option<A> a : as) {
            if (!a.isDefined()) continue;
            return a;
        }
        return Option.none();
    }

    @Deprecated
    public static <A> Predicate<Option<A>> isDefined() {
        return new IsDefined();
    }

    public static <R> Stream<R> stream(Option<R> option) {
        return option.isDefined() ? Stream.of(option.get()) : Stream.empty();
    }

    @Deprecated
    public static Option<DateTime> toOption(Optional<DateTime> optional) {
        return optional.map(Option::some).orElseGet(Option::none);
    }

    @Deprecated
    public static <A, B> Function<A, Option<B>> asNone() {
        return new AsNone();
    }

    @Deprecated
    public static <A> Function<A, Option<A>> asSome() {
        return new AsSome();
    }

    @Deprecated
    public static <T> Supplier<Option<T>> noneSupplier() {
        return new NoneSupplier();
    }

    @Deprecated
    private static class NoneSupplier<T>
    implements Supplier<Option<T>> {
        private NoneSupplier() {
        }

        public Option<T> get() {
            return Option.none();
        }
    }

    @Deprecated
    private static final class AsSome<A>
    implements Function<A, Option<A>> {
        private AsSome() {
        }

        public Option<A> apply(A a) {
            return Option.some(a);
        }
    }

    @Deprecated
    private static final class AsNone<A, B>
    implements Function<A, Option<B>> {
        private AsNone() {
        }

        public Option<B> apply(A a) {
            return Option.none();
        }
    }

    @Deprecated
    private static final class IsDefined<A>
    implements Predicate<Option<A>> {
        private IsDefined() {
        }

        public boolean apply(Option<A> a) {
            return a.isDefined();
        }
    }
}

