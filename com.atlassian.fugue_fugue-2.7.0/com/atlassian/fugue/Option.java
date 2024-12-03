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
import com.atlassian.fugue.Either;
import com.atlassian.fugue.Functions;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Options;
import com.atlassian.fugue.Suppliers;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class Option<A>
implements Iterable<A>,
Maybe<A>,
Serializable {
    private static final long serialVersionUID = 7849097310208471377L;
    private static final Option<Object> NONE = new Option<Object>(){
        private static final long serialVersionUID = -1978333494161467110L;

        @Override
        public <B> B fold(Supplier<? extends B> none, Function<? super Object, ? extends B> some) {
            return (B)none.get();
        }

        @Override
        public Object get() {
            throw new NoSuchElementException();
        }

        @Override
        public boolean isDefined() {
            return false;
        }

        @Override
        public Object getOrError(Supplier<String> err) {
            throw new AssertionError(err.get());
        }

        @Override
        public <X extends Throwable> Object getOrThrow(Supplier<X> ifUndefined) throws X {
            throw (Throwable)ifUndefined.get();
        }

        @Override
        public void foreach(Effect<Object> effect) {
        }
    };
    private static final Supplier<String> NONE_STRING = Suppliers.ofInstance("none()");
    private static final Supplier<Integer> NONE_HASH = Suppliers.ofInstance(31);

    public static <A> Option<A> option(A a) {
        return a == null ? Option.none() : Option.some(a);
    }

    public static <A> Option<A> some(A value) {
        Preconditions.checkNotNull(value);
        return new Some(value);
    }

    public static <A> Option<A> none() {
        Option<Object> result = NONE;
        return result;
    }

    public static <A> Option<A> none(Class<A> type) {
        return Option.none();
    }

    static <A> Function<A, Option<A>> toOption() {
        return new ToOption();
    }

    public static <A> Predicate<Option<A>> defined() {
        return new Defined();
    }

    public static <A> Supplier<Option<A>> noneSupplier() {
        return Suppliers.ofInstance(Option.none());
    }

    @Deprecated
    public static <A> Option<A> find(Iterable<Option<A>> options) {
        return Options.find(options);
    }

    @Deprecated
    public static <A> Iterable<Option<A>> filterNone(Iterable<Option<A>> options) {
        return Options.filterNone(options);
    }

    Option() {
    }

    public abstract <B> B fold(Supplier<? extends B> var1, Function<? super A, ? extends B> var2);

    @Override
    public final <B extends A> A getOrElse(B other) {
        return this.getOrElse(Suppliers.ofInstance(other));
    }

    @Override
    public final A getOrElse(Supplier<? extends A> supplier) {
        return this.fold(supplier, Functions.identity());
    }

    @Override
    public final A getOrNull() {
        return this.fold(Suppliers.alwaysNull(), Functions.identity());
    }

    public final Option<A> orElse(Option<? extends A> orElse) {
        return this.orElse(Suppliers.ofInstance(orElse));
    }

    public final Option<A> orElse(Supplier<? extends Option<? extends A>> orElse) {
        Option<A> result = this.fold(orElse, Option.toOption());
        return result;
    }

    @Override
    public final boolean exists(Predicate<? super A> p) {
        Preconditions.checkNotNull(p);
        return this.isDefined() && p.apply(this.get());
    }

    @Override
    public boolean forall(Predicate<? super A> p) {
        return this.isEmpty() || p.apply(this.get());
    }

    @Override
    public final boolean isEmpty() {
        return !this.isDefined();
    }

    @Override
    public final Iterator<A> iterator() {
        return this.fold(Suppliers.ofInstance(Collections.emptyIterator()), Functions.singletonIterator());
    }

    public final <B> Option<B> map(Function<? super A, ? extends B> f) {
        Preconditions.checkNotNull(f);
        return this.isEmpty() ? Option.none() : new Some(f.apply(this.get()));
    }

    public final <B> Option<B> flatMap(Function<? super A, ? extends Option<? extends B>> f) {
        Preconditions.checkNotNull(f);
        Option<? extends B> result = this.fold(Option.noneSupplier(), f);
        return result;
    }

    public final Option<A> filter(Predicate<? super A> p) {
        Preconditions.checkNotNull(p);
        return this.isEmpty() || p.apply(this.get()) ? this : Option.none();
    }

    public final <X> Either<X, A> toRight(Supplier<X> left) {
        return this.isEmpty() ? Either.left(left.get()) : Either.right(this.get());
    }

    public final <X> Either<A, X> toLeft(Supplier<X> right) {
        return this.isEmpty() ? Either.right(right.get()) : Either.left(this.get());
    }

    public final int hashCode() {
        return this.fold(NONE_HASH, SomeHashCode.instance());
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof Option)) {
            return false;
        }
        Option other = (Option)obj;
        return other.fold(this.isDefined() ? Suppliers.alwaysFalse() : Suppliers.alwaysTrue(), this.valuesEqual());
    }

    public final String toString() {
        return this.fold(NONE_STRING, SomeString.instance());
    }

    private Function<Object, Boolean> valuesEqual() {
        return new Function<Object, Boolean>(){

            public Boolean apply(Object obj) {
                return Option.this.isDefined() && Option.this.get().equals(obj);
            }
        };
    }

    static class ToOption<A>
    implements Function<A, Option<A>> {
        ToOption() {
        }

        public Option<A> apply(A a) {
            return Option.option(a);
        }
    }

    static enum SomeHashCode implements Function<Object, Integer>
    {
        INSTANCE;


        public Integer apply(Object a) {
            return a.hashCode();
        }

        static <A> Function<A, Integer> instance() {
            return INSTANCE;
        }
    }

    static enum SomeString implements Function<Object, String>
    {
        INSTANCE;


        public String apply(Object obj) {
            return String.format("some(%s)", obj);
        }

        static <A> Function<A, String> instance() {
            return INSTANCE;
        }
    }

    static final class Some<A>
    extends Option<A> {
        private static final long serialVersionUID = 5542513144209030852L;
        private final A value;

        private Some(A value) {
            this.value = value;
        }

        @Override
        public <B> B fold(Supplier<? extends B> none, Function<? super A, ? extends B> f) {
            return (B)f.apply(this.value);
        }

        @Override
        public A get() {
            return this.value;
        }

        @Override
        public boolean isDefined() {
            return true;
        }

        @Override
        public A getOrError(Supplier<String> err) {
            return this.get();
        }

        @Override
        public <X extends Throwable> A getOrThrow(Supplier<X> ifUndefined) throws X {
            return this.get();
        }

        @Override
        public void foreach(Effect<? super A> effect) {
            effect.apply(this.value);
        }
    }

    static final class Defined<A>
    implements Predicate<Option<A>> {
        Defined() {
        }

        public boolean apply(Option<A> option) {
            return option.isDefined();
        }
    }
}

