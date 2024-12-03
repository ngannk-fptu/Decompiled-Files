/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.base.Supplier
 *  javax.annotation.Nonnull
 */
package com.atlassian.upm.api.util;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

public abstract class Option<A>
implements Iterable<A> {
    private static final Option<Object> NONE = new Option<Object>(){

        @Override
        public <B> B fold(java.util.function.Supplier<B> none, java.util.function.Function<Object, B> some) {
            return none.get();
        }
    };

    private Option() {
    }

    public abstract <B> B fold(java.util.function.Supplier<B> var1, java.util.function.Function<A, B> var2);

    public final A get() {
        return this.fold(Option.throwNoSuchElementException(), java.util.function.Function.identity());
    }

    public final <B extends A> A getOrElse(B other) {
        return (A)this.fold(() -> other, java.util.function.Function.identity());
    }

    public final A getOrElse(java.util.function.Supplier<A> supplier) {
        return this.fold(supplier, java.util.function.Function.identity());
    }

    public final boolean isDefined() {
        return (Boolean)this.fold(() -> false, ignored -> true);
    }

    @Override
    @Nonnull
    public final Iterator<A> iterator() {
        return (Iterator)this.fold(Collections::emptyIterator, a -> Collections.singletonList(a).iterator());
    }

    public final Option<A> orElse(Option<A> orElse) {
        return this.orElse(() -> orElse);
    }

    public final Option<A> orElse(java.util.function.Supplier<Option<A>> orElse) {
        return this.fold(orElse, Option::option);
    }

    public final Option<A> filter(Predicate<A> f) {
        return this.flatMap(input -> {
            if (f.test(input)) {
                return Option.some(input);
            }
            return Option.none();
        });
    }

    public final boolean exists(Predicate<A> f) {
        return this.filter(f).isDefined();
    }

    public final <B> Option<B> flatMap(java.util.function.Function<A, Option<B>> f) {
        return this.fold(Option::none, f);
    }

    public final <B> Option<B> map(java.util.function.Function<A, B> f) {
        return this.flatMap(f.andThen(Option::option));
    }

    public final int hashCode() {
        return (Integer)this.fold(() -> 31, Object::hashCode);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Option other = (Option)obj;
        return (Boolean)other.fold(() -> !this.isDefined(), otherObj -> this.get().equals(otherObj));
    }

    public final String toString() {
        return (String)this.fold(() -> "none()", obj -> String.format("some(%s)", obj));
    }

    public static <A> Option<A> none() {
        return NONE;
    }

    public static <A> Option<A> none(Class<A> type) {
        return NONE;
    }

    public static <A> Option<A> some(A value) {
        return new Some(value);
    }

    public static <A> Option<A> option(A a) {
        if (a == null) {
            return Option.none();
        }
        return Option.some(a);
    }

    @Deprecated
    public <B> B fold(Supplier<B> none, Function<A, B> some) {
        return this.fold((java.util.function.Supplier<B>)none, (java.util.function.Function<A, B>)some);
    }

    @Deprecated
    public final A getOrElse(Supplier<A> supplier) {
        return this.getOrElse((java.util.function.Supplier<A>)supplier);
    }

    @Deprecated
    public final Option<A> orElse(Supplier<Option<A>> orElse) {
        return this.orElse((java.util.function.Supplier<Option<A>>)orElse);
    }

    @Deprecated
    public final Option<A> filter(com.google.common.base.Predicate<A> f) {
        return this.filter((Predicate<A>)f);
    }

    @Deprecated
    public final boolean exists(com.google.common.base.Predicate<A> f) {
        return this.exists((Predicate<A>)f);
    }

    @Deprecated
    public final <B> Option<B> flatMap(Function<A, Option<B>> f) {
        return this.flatMap((java.util.function.Function<A, Option<B>>)f);
    }

    @Deprecated
    public final <B> Option<B> map(Function<A, B> f) {
        return this.map((java.util.function.Function<A, B>)f);
    }

    @Deprecated
    public static <A> Supplier<Option<A>> noneSupplier() {
        return Option::none;
    }

    @Deprecated
    public static <A> Function<A, Option<A>> option() {
        return Option::option;
    }

    private static <A> java.util.function.Supplier<A> throwNoSuchElementException() {
        return () -> {
            throw new NoSuchElementException();
        };
    }

    private static final class Some<A>
    extends Option<A> {
        private final A value;

        private Some(A value) {
            this.value = Objects.requireNonNull(value, "value");
        }

        @Override
        public <B> B fold(java.util.function.Supplier<B> none, java.util.function.Function<A, B> f) {
            return f.apply(this.value);
        }
    }
}

