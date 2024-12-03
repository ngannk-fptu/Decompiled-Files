/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.fugue;

import io.atlassian.fugue.Effect;
import io.atlassian.fugue.Either;
import io.atlassian.fugue.Functions;
import io.atlassian.fugue.Iterators;
import io.atlassian.fugue.Maybe;
import io.atlassian.fugue.Options;
import io.atlassian.fugue.Suppliers;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class Option<A>
implements Iterable<A>,
Maybe<A>,
Serializable {
    private static final long serialVersionUID = 7849097310208471377L;
    private static final Supplier<String> NONE_STRING = Suppliers.ofInstance("none()");
    private static final Supplier<Integer> NONE_HASH = Suppliers.ofInstance(31);
    private static final Function<Object, String> SOME_STRING = obj -> String.format("some(%s)", obj);
    private static final Function<Object, Integer> SOME_HASH = Object::hashCode;
    @Deprecated
    private static final Serializable NONE = new Serializable(){
        private static final long serialVersionUID = -1978333494161467110L;

        private Object readResolve() {
            return None.NONE;
        }
    };

    public static <A> Option<A> option(A a) {
        return a == null ? Option.none() : new Some(a);
    }

    public static <A> Option<A> some(A value) {
        Objects.requireNonNull(value);
        return new Some(value);
    }

    public static <A> Option<A> none() {
        Option result = None.NONE;
        return result;
    }

    public static <A> Option<A> none(Class<A> type) {
        return Option.none();
    }

    public static <A> Predicate<Option<A>> defined() {
        return Maybe::isDefined;
    }

    public static <A> Supplier<Option<A>> noneSupplier() {
        return Suppliers.ofInstance(Option.none());
    }

    public static <A> Option<A> fromOptional(Optional<A> optional) {
        return Option.option(optional.orElse(null));
    }

    Option() {
    }

    public abstract <B> B fold(Supplier<? extends B> var1, Function<? super A, ? extends B> var2);

    @Override
    public final <B extends A> A getOrElse(B other) {
        return this.getOr(Suppliers.ofInstance(other));
    }

    @Override
    public final A getOr(Supplier<? extends A> supplier) {
        return this.fold(supplier, Functions.identity());
    }

    @Override
    @Deprecated
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
        Option result = this.fold(orElse, Options.toOption());
        return result;
    }

    @Override
    public final boolean exists(Predicate<? super A> p) {
        Objects.requireNonNull(p);
        return this.isDefined() && p.test(this.get());
    }

    @Override
    public final boolean forall(Predicate<? super A> p) {
        Objects.requireNonNull(p);
        return this.isEmpty() || p.test(this.get());
    }

    @Override
    public final boolean isEmpty() {
        return !this.isDefined();
    }

    @Override
    public final Iterator<A> iterator() {
        return this.fold(Suppliers.ofInstance(Iterators.emptyIterator()), Iterators::singletonIterator);
    }

    public final <B> Option<B> map(Function<? super A, ? extends B> f) {
        Objects.requireNonNull(f);
        return this.isEmpty() ? Option.none() : new Some(f.apply(this.get()));
    }

    public final <B> Option<B> flatMap(Function<? super A, ? extends Option<? extends B>> f) {
        Objects.requireNonNull(f);
        Option<? extends B> result = this.fold(Option.noneSupplier(), f);
        return result;
    }

    public final Option<A> filter(Predicate<? super A> p) {
        Objects.requireNonNull(p);
        return this.isEmpty() || p.test(this.get()) ? this : Option.none();
    }

    public final <X> Either<X, A> toRight(Supplier<X> left) {
        return this.isEmpty() ? Either.left(left.get()) : Either.right(this.get());
    }

    public final <X> Either<A, X> toLeft(Supplier<X> right) {
        return this.isEmpty() ? Either.right(right.get()) : Either.left(this.get());
    }

    public abstract Optional<A> toOptional();

    public abstract Stream<A> toStream();

    public final int hashCode() {
        return this.fold(NONE_HASH, SOME_HASH);
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
        return this.fold(NONE_STRING, SOME_STRING);
    }

    private Function<Object, Boolean> valuesEqual() {
        return obj -> this.isDefined() && Objects.equals(this.get(), obj);
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
            return f.apply(this.value);
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
        @Deprecated
        public void foreach(Effect<? super A> effect) {
            this.forEach((Consumer<? super A>)effect);
        }

        @Override
        public void forEach(Consumer<? super A> effect) {
            effect.accept(this.value);
        }

        @Override
        public Optional<A> toOptional() {
            return Optional.of(this.value);
        }

        @Override
        public Stream<A> toStream() {
            return Stream.of(this.value);
        }
    }

    static final class None
    extends Option<Object> {
        private static final long serialVersionUID = -1978333494161467110L;
        private static final Option<Object> NONE = new None();

        None() {
        }

        @Override
        public <B> B fold(Supplier<? extends B> none, Function<? super Object, ? extends B> some) {
            return none.get();
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
            throw new AssertionError((Object)err.get());
        }

        @Override
        public <X extends Throwable> Object getOrThrow(Supplier<X> ifUndefined) throws X {
            throw (Throwable)ifUndefined.get();
        }

        @Override
        @Deprecated
        public void foreach(Effect<? super Object> effect) {
            this.forEach((Consumer<? super Object>)effect);
        }

        @Override
        public void forEach(Consumer<? super Object> effect) {
        }

        @Override
        public Optional<Object> toOptional() {
            return Optional.empty();
        }

        @Override
        public Stream<Object> toStream() {
            return Stream.empty();
        }

        private Object readResolve() {
            return NONE;
        }
    }
}

