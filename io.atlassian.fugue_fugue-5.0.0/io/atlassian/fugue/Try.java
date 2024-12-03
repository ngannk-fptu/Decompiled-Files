/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.fugue;

import io.atlassian.fugue.Checked;
import io.atlassian.fugue.Either;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.Suppliers;
import io.atlassian.fugue.Unit;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Try<A>
implements Serializable,
Iterable<A> {
    private static final long serialVersionUID = -999421999482330308L;

    public static <A> Try<A> failure(Exception e) {
        return new Failure(e);
    }

    public static <A> Try<A> successful(A value) {
        return new Success<A>(value);
    }

    public static <A> Try<A> delayed(Supplier<Try<A>> supplier) {
        return Delayed.fromSupplier(supplier);
    }

    public static <A> Try<Iterable<A>> sequence(Iterable<Try<A>> trys) {
        return Try.sequence(trys, Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    public static <T, A, R> Try<R> sequence(Iterable<Try<T>> trys, Collector<T, A, R> collector) {
        A accumulator = collector.supplier().get();
        for (Try<T> t : trys) {
            if (t.isFailure()) {
                return Try.failure(t.fold(Function.identity(), x -> {
                    throw new NoSuchElementException();
                }));
            }
            collector.accumulator().accept(accumulator, t.fold(f -> {
                throw new NoSuchElementException();
            }, Function.identity()));
        }
        return Try.successful(collector.finisher().apply(accumulator));
    }

    public static <A> Try<A> flatten(Try<Try<A>> t) {
        return t.flatMap(Function.identity());
    }

    public abstract boolean isFailure();

    public abstract boolean isSuccess();

    public abstract <B> Try<B> flatMap(Function<? super A, Try<B>> var1);

    public abstract <B> Try<B> map(Function<? super A, ? extends B> var1);

    public abstract Try<A> recover(Function<? super Exception, A> var1);

    public abstract <X extends Exception> Try<A> recover(Class<X> var1, Function<? super X, A> var2);

    public abstract Try<A> recoverWith(Function<? super Exception, Try<A>> var1);

    public abstract <X extends Exception> Try<A> recoverWith(Class<X> var1, Function<? super X, Try<A>> var2);

    public abstract A getOrElse(Supplier<A> var1);

    public final Try<A> orElse(Try<? extends A> orElse) {
        return this.orElse(Suppliers.ofInstance(orElse));
    }

    public abstract Try<A> orElse(Supplier<? extends Try<? extends A>> var1);

    public abstract Try<A> filterOrElse(Predicate<? super A> var1, Supplier<Exception> var2);

    public abstract <B> B fold(Function<? super Exception, B> var1, Function<A, B> var2);

    public abstract Either<Exception, A> toEither();

    public abstract Option<A> toOption();

    public abstract Optional<A> toOptional();

    public abstract Stream<A> toStream();

    @Override
    public abstract void forEach(Consumer<? super A> var1);

    @Override
    public final Iterator<A> iterator() {
        return this.toOption().iterator();
    }

    private static final class Delayed<A>
    extends Try<A>
    implements Externalizable {
        private static final long serialVersionUID = 2439842151512848666L;
        private final AtomicReference<Function<Unit, Try<A>>> runReference;

        static <A> Delayed<A> fromSupplier(Supplier<Try<A>> delayed) {
            Supplier memorized = Suppliers.memoize(delayed);
            return new Delayed<A>(unit -> (Try)memorized.get());
        }

        public Delayed() {
            this(unit -> {
                throw new IllegalStateException("Try.Delayed() default constructor only required for Serialization. Do not invoke directly.");
            });
        }

        private Delayed(Function<Unit, Try<A>> run) {
            this.runReference = new AtomicReference<Function<Unit, Try<A>>>(run);
        }

        private Function<Unit, Try<A>> getRunner() {
            return this.runReference.get();
        }

        private Try<A> eval() {
            return this.getRunner().apply(Unit.Unit());
        }

        @Override
        public boolean isFailure() {
            return this.eval().isFailure();
        }

        @Override
        public boolean isSuccess() {
            return this.eval().isSuccess();
        }

        private <B> Try<B> composeDelayed(Function<Try<A>, Try<B>> f) {
            return new Delayed<A>(f.compose(this.getRunner()));
        }

        @Override
        public <B> Try<B> flatMap(Function<? super A, Try<B>> f) {
            return this.composeDelayed(t -> t.flatMap(f));
        }

        @Override
        public <B> Try<B> map(Function<? super A, ? extends B> f) {
            return this.composeDelayed(t -> t.map(f));
        }

        @Override
        public Try<A> recover(Function<? super Exception, A> f) {
            return this.composeDelayed(t -> t.recover(f));
        }

        @Override
        public <X extends Exception> Try<A> recover(Class<X> exceptionType, Function<? super X, A> f) {
            return this.composeDelayed(t -> t.recover(exceptionType, f));
        }

        @Override
        public Try<A> recoverWith(Function<? super Exception, Try<A>> f) {
            return this.composeDelayed(t -> t.recoverWith(f));
        }

        @Override
        public <X extends Exception> Try<A> recoverWith(Class<X> exceptionType, Function<? super X, Try<A>> f) {
            return this.composeDelayed(t -> t.recoverWith(exceptionType, f));
        }

        @Override
        public A getOrElse(Supplier<A> s) {
            return this.eval().getOrElse(s);
        }

        @Override
        public Try<A> orElse(Supplier<? extends Try<? extends A>> orElse) {
            return this.composeDelayed(t -> t.orElse(orElse));
        }

        @Override
        public Try<A> filterOrElse(Predicate<? super A> p, Supplier<Exception> orElseSupplier) {
            return this.composeDelayed(t -> t.filterOrElse(p, orElseSupplier));
        }

        @Override
        public <B> B fold(Function<? super Exception, B> failureF, Function<A, B> successF) {
            return this.eval().fold(failureF, successF);
        }

        @Override
        public Either<Exception, A> toEither() {
            return this.eval().toEither();
        }

        @Override
        public Option<A> toOption() {
            return this.eval().toOption();
        }

        @Override
        public Optional<A> toOptional() {
            return this.eval().toOptional();
        }

        @Override
        public Stream<A> toStream() {
            return this.eval().toStream();
        }

        @Override
        public void forEach(Consumer<? super A> action) {
            this.eval().forEach(action);
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(this.eval());
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            Try result = (Try)in.readObject();
            this.runReference.set(unit -> result);
        }
    }

    private static final class Success<A>
    extends Try<A> {
        private static final long serialVersionUID = -8360076933771852847L;
        private final A value;

        Success(A value) {
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public <B> Try<B> map(Function<? super A, ? extends B> f) {
            return Checked.now(() -> f.apply((A)this.value));
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public <B> Try<B> flatMap(Function<? super A, Try<B>> f) {
            return f.apply(this.value);
        }

        @Override
        public Try<A> recover(Function<? super Exception, A> f) {
            return this;
        }

        @Override
        public <X extends Exception> Try<A> recover(Class<X> exceptionType, Function<? super X, A> f) {
            return this;
        }

        @Override
        public Try<A> recoverWith(Function<? super Exception, Try<A>> f) {
            return this;
        }

        @Override
        public <X extends Exception> Try<A> recoverWith(Class<X> exceptionType, Function<? super X, Try<A>> f) {
            return this;
        }

        @Override
        public A getOrElse(Supplier<A> s) {
            return this.value;
        }

        @Override
        public Try<A> orElse(Supplier<? extends Try<? extends A>> orElse) {
            return this;
        }

        @Override
        public Try<A> filterOrElse(Predicate<? super A> p, Supplier<Exception> orElseSupplier) {
            return Checked.now(() -> {
                if (p.test((A)this.value)) {
                    return this.value;
                }
                throw (Exception)orElseSupplier.get();
            });
        }

        @Override
        public <B> B fold(Function<? super Exception, B> failureF, Function<A, B> successF) {
            return successF.apply(this.value);
        }

        @Override
        public Either<Exception, A> toEither() {
            return Either.right(this.value);
        }

        @Override
        public Option<A> toOption() {
            return Option.some(this.value);
        }

        @Override
        public Optional<A> toOptional() {
            return Optional.of(this.value);
        }

        @Override
        public Stream<A> toStream() {
            return Stream.of(this.value);
        }

        @Override
        public void forEach(Consumer<? super A> action) {
            action.accept(this.value);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Success success = (Success)o;
            return this.value != null ? this.value.equals(success.value) : success.value == null;
        }

        public int hashCode() {
            return this.value.hashCode();
        }

        public String toString() {
            return "Try.Success(" + this.value.toString() + ")";
        }
    }

    private static final class Failure<A>
    extends Try<A> {
        private static final long serialVersionUID = 735762069058538901L;
        private final Exception e;

        Failure(Exception e) {
            this.e = Objects.requireNonNull(e);
        }

        @Override
        public <B> Try<B> map(Function<? super A, ? extends B> f) {
            return Try.failure(this.e);
        }

        @Override
        public boolean isFailure() {
            return true;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public <B> Try<B> flatMap(Function<? super A, Try<B>> f) {
            return Try.failure(this.e);
        }

        @Override
        public Try<A> recover(Function<? super Exception, A> f) {
            return Checked.now(() -> f.apply(this.e));
        }

        @Override
        public <X extends Exception> Try<A> recover(Class<X> exceptionType, Function<? super X, A> f) {
            return exceptionType.isAssignableFrom(this.e.getClass()) ? Checked.now(() -> f.apply((Object)this.e)) : this;
        }

        @Override
        public Try<A> recoverWith(Function<? super Exception, Try<A>> f) {
            return f.apply(this.e);
        }

        @Override
        public <X extends Exception> Try<A> recoverWith(Class<X> exceptionType, Function<? super X, Try<A>> f) {
            return exceptionType.isAssignableFrom(this.e.getClass()) ? f.apply(this.e) : this;
        }

        @Override
        public A getOrElse(Supplier<A> s) {
            return s.get();
        }

        @Override
        public Try<A> orElse(Supplier<? extends Try<? extends A>> orElse) {
            Try<? extends A> result = orElse.get();
            return result;
        }

        @Override
        public Try<A> filterOrElse(Predicate<? super A> p, Supplier<Exception> orElseSupplier) {
            return Try.failure(this.e);
        }

        @Override
        public <B> B fold(Function<? super Exception, B> failureF, Function<A, B> successF) {
            return failureF.apply(this.e);
        }

        @Override
        public Either<Exception, A> toEither() {
            return Either.left(this.e);
        }

        @Override
        public Option<A> toOption() {
            return Option.none();
        }

        @Override
        public Optional<A> toOptional() {
            return Optional.empty();
        }

        @Override
        public Stream<A> toStream() {
            return Stream.empty();
        }

        @Override
        public void forEach(Consumer<? super A> action) {
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Failure failure = (Failure)o;
            return this.e != null ? this.e.equals(failure.e) : failure.e == null;
        }

        public int hashCode() {
            return ~this.e.hashCode();
        }

        public String toString() {
            return "Try.Failure(" + this.e.toString() + ")";
        }
    }
}

