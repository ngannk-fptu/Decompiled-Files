/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Functions
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.Iterators
 *  javax.annotation.Nonnull
 */
package com.atlassian.streams.api.common;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Iterators;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.annotation.Nonnull;

@Deprecated
public abstract class Option<A>
implements Iterable<A> {
    private static final Option<Object> NONE = new Option<Object>(){

        @Override
        @Deprecated
        public <B> B fold(Supplier<B> none, Function<? super Object, B> some) {
            return (B)none.get();
        }

        @Override
        public <B> B fold(java.util.function.Supplier<B> none, java.util.function.Function<? super Object, B> some) {
            return none.get();
        }
    };

    private Option() {
    }

    @Nonnull
    public static <X> Optional<X> toOptional(@Nonnull Option<X> o) {
        if (o.isDefined()) {
            return Optional.of(o.get());
        }
        return Optional.empty();
    }

    @Deprecated
    public abstract <B> B fold(Supplier<B> var1, Function<? super A, B> var2);

    public abstract <B> B fold(java.util.function.Supplier<B> var1, java.util.function.Function<? super A, B> var2);

    public final A get() {
        return this.fold(Option.throwNoSuchElementException(), Functions.identity());
    }

    private static <A> Supplier<A> throwNoSuchElementException() {
        return ThrowNoSuchElementException.INSTANCE;
    }

    @Deprecated
    public final A getOrElse(Supplier<A> supplier) {
        return this.fold(supplier, Functions.identity());
    }

    public final A getOrElse(java.util.function.Supplier<A> supplier) {
        return this.fold(supplier, java.util.function.Function.identity());
    }

    public final <B extends A> A getOrElse(B other) {
        return (A)this.fold(Suppliers.ofInstance(other), Functions.identity());
    }

    public final Option<A> orElse(Option<A> orElse) {
        return this.fold(Suppliers.ofInstance(orElse), Option.option());
    }

    @Deprecated
    public final <B> Option<B> map(Function<? super A, B> f) {
        return this.flatMap(Functions.compose(Option.option(), f));
    }

    public final <B> Option<B> map(java.util.function.Function<? super A, B> f) {
        return this.flatMap(f.andThen(Option::option));
    }

    @Deprecated
    public final <B> Option<B> flatMap(Function<? super A, Option<B>> f) {
        return this.fold(Option::none, f);
    }

    public final <B> Option<B> flatMap(java.util.function.Function<? super A, Option<B>> f) {
        return this.fold(Option::none, f);
    }

    public final boolean isDefined() {
        return (Boolean)this.fold(Suppliers.ofInstance((Object)false), Functions.forPredicate((Predicate)Predicates.alwaysTrue()));
    }

    @Override
    public final Iterator<A> iterator() {
        return this.fold(Suppliers.ofInstance(Collections.emptyIterator()), Option.singletonIterator());
    }

    private static <A> Function<A, Iterator<A>> singletonIterator() {
        return a -> Iterators.singletonIterator((Object)a);
    }

    public final int hashCode() {
        return this.fold(this.noneHashCode(), this.someHashCode());
    }

    private Function<A, Integer> someHashCode() {
        return SomeHashCode.INSTANCE;
    }

    private Supplier<Integer> noneHashCode() {
        return NoneHashCode.INSTANCE;
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
        return other.fold((Supplier)(this.isDefined() ? SupplyFalse.INSTANCE : SupplyTrue.INSTANCE), (Function)this.valuesEqual());
    }

    private Function<Object, Boolean> valuesEqual() {
        return obj -> this.get().equals(obj);
    }

    public final String toString() {
        return this.fold(this.noneString(), this.someString());
    }

    private Function<A, String> someString() {
        return SomeString.INSTANCE;
    }

    private Supplier<String> noneString() {
        return NoneString.INSTANCE;
    }

    public static <A> Option<A> some(A value) {
        return new Some(value);
    }

    public static <A> Option<A> none() {
        return NONE;
    }

    public static <A> Option<A> none(Class<A> type) {
        return NONE;
    }

    @Deprecated
    public static <A> Supplier<Option<A>> noneSupplier() {
        return new NoneSupplier();
    }

    public static <A> Option<A> option(A a) {
        if (a == null) {
            return Option.none();
        }
        return Option.some(a);
    }

    @Deprecated
    public static <A> Function<A, Option<A>> option() {
        return new ToOption();
    }

    @Deprecated
    private static class ToOption<A>
    implements Function<A, Option<A>> {
        private ToOption() {
        }

        public Option<A> apply(A from) {
            return Option.option(from);
        }
    }

    private static final class NoneSupplier<A>
    implements Supplier<Option<A>> {
        private NoneSupplier() {
        }

        public Option<A> get() {
            return Option.none();
        }
    }

    private static final class Some<A>
    extends Option<A> {
        private final A value;

        private Some(A value) {
            this.value = Preconditions.checkNotNull(value, (Object)"value");
        }

        @Override
        @Deprecated
        public <B> B fold(Supplier<B> none, Function<? super A, B> f) {
            return (B)f.apply(this.value);
        }

        @Override
        public <B> B fold(java.util.function.Supplier<B> none, java.util.function.Function<? super A, B> some) {
            return some.apply(this.value);
        }
    }

    private static enum NoneString implements Supplier<String>
    {
        INSTANCE;


        public String get() {
            return "none()";
        }
    }

    private static enum SomeString implements Function
    {
        INSTANCE;


        public String apply(Object obj) {
            return String.format("some(%s)", obj);
        }
    }

    private static enum SupplyFalse implements Supplier<Boolean>
    {
        INSTANCE;


        public Boolean get() {
            return false;
        }
    }

    private static enum SupplyTrue implements Supplier<Boolean>
    {
        INSTANCE;


        public Boolean get() {
            return true;
        }
    }

    private static enum NoneHashCode implements Supplier<Integer>
    {
        INSTANCE;


        public Integer get() {
            return 31;
        }
    }

    private static enum SomeHashCode implements Function
    {
        INSTANCE;


        public Integer apply(Object a) {
            return a.hashCode();
        }
    }

    private static enum ThrowNoSuchElementException implements Supplier
    {
        INSTANCE;


        public Object get() {
            throw new NoSuchElementException();
        }
    }
}

