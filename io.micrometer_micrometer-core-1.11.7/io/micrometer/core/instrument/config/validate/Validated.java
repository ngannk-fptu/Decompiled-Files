/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNull
 *  io.micrometer.common.lang.Nullable
 *  io.micrometer.common.util.StringUtils
 */
package io.micrometer.core.instrument.config.validate;

import io.micrometer.common.lang.NonNull;
import io.micrometer.common.lang.Nullable;
import io.micrometer.common.util.StringUtils;
import io.micrometer.core.annotation.Incubating;
import io.micrometer.core.instrument.config.validate.InvalidReason;
import io.micrometer.core.instrument.config.validate.ValidationException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Incubating(since="1.5.0")
public interface Validated<T>
extends Iterable<Validated<T>> {
    public boolean isValid();

    default public boolean isInvalid() {
        return !this.isValid();
    }

    default public List<Invalid<?>> failures() {
        return StreamSupport.stream(this.spliterator(), false).filter(Validated::isInvalid).map((? super T v) -> (Invalid)v).collect(Collectors.toList());
    }

    public static Secret validSecret(String property, String value) {
        return new Secret(property, value);
    }

    public static <T> None<T> none() {
        return new None();
    }

    public static <T> Valid<T> valid(String property, @Nullable T value) {
        return new Valid<T>(property, value);
    }

    public static <T> Invalid<T> invalid(String property, @Nullable Object value, String message, InvalidReason reason) {
        return Validated.invalid(property, value, message, reason, null);
    }

    public static <T> Invalid<T> invalid(String property, @Nullable Object value, String message, InvalidReason reason, @Nullable Throwable exception) {
        return new Invalid(property, value, message, reason, exception);
    }

    default public Validated<?> and(Validated<?> validated) {
        if (this instanceof None) {
            return validated;
        }
        return new Either(this, validated);
    }

    public <U> Validated<U> map(Function<T, U> var1);

    public <U> Validated<U> flatMap(BiFunction<T, Valid<T>, Validated<U>> var1);

    default public <U> Validated<U> flatMap(Function<T, Validated<U>> mapping) {
        return this.flatMap((T value, Valid<T> original) -> (Validated)mapping.apply(value));
    }

    default public Validated<T> invalidateWhen(Predicate<T> condition, String message, InvalidReason reason) {
        return this.flatMap((T value, Valid<T> valid) -> condition.test(value) ? Validated.invalid(valid.property, value, message, reason) : valid);
    }

    default public Validated<T> required() {
        return this.invalidateWhen(Objects::isNull, "is required", InvalidReason.MISSING);
    }

    default public Validated<T> nonBlank() {
        return this.invalidateWhen(t -> StringUtils.isBlank((String)t.toString()), "cannot be blank", InvalidReason.MISSING);
    }

    public T get() throws ValidationException;

    default public T orElse(@Nullable T t) throws ValidationException {
        return (T)this.orElseGet(() -> t);
    }

    public T orElseGet(Supplier<T> var1) throws ValidationException;

    public void orThrow() throws ValidationException;

    public static class Secret
    extends Valid<String> {
        public Secret(String property, String value) {
            super(property, value);
        }

        @Override
        public String toString() {
            return "Secret{property='" + this.property + '\'' + '}';
        }
    }

    public static class None<T>
    implements Validated<T> {
        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public <U> Validated<U> map(Function<T, U> mapping) {
            return this;
        }

        @Override
        public <U> Validated<U> flatMap(BiFunction<T, Valid<T>, Validated<U>> mapping) {
            return this;
        }

        @Override
        public T get() {
            return null;
        }

        @Override
        public T orElseGet(Supplier<T> t) {
            return null;
        }

        @Override
        public void orThrow() {
        }

        @Override
        @NonNull
        public Iterator<Validated<T>> iterator() {
            return Collections.emptyIterator();
        }
    }

    public static class Valid<T>
    implements Validated<T> {
        protected final String property;
        private final T value;

        public Valid(String property, T value) {
            this.property = property;
            this.value = value;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        @NonNull
        public Iterator<Validated<T>> iterator() {
            return Stream.of(this).iterator();
        }

        @Override
        public T get() {
            return this.value;
        }

        @Override
        public void orThrow() {
        }

        @Override
        public T orElseGet(Supplier<T> t) {
            return this.value == null ? t.get() : this.value;
        }

        @Override
        public <U> Validated<U> map(Function<T, U> mapping) {
            return new Valid<U>(this.property, mapping.apply(this.value));
        }

        @Override
        public <U> Validated<U> flatMap(BiFunction<T, Valid<T>, Validated<U>> mapping) {
            return mapping.apply(this.value, this);
        }

        public String getProperty() {
            return this.property;
        }

        public String toString() {
            return "Valid{property='" + this.property + '\'' + ", value='" + this.value + '\'' + '}';
        }
    }

    public static class Invalid<T>
    implements Validated<T> {
        private final String property;
        @Nullable
        private final Object value;
        private final String message;
        private final InvalidReason reason;
        @Nullable
        private final Throwable exception;

        public Invalid(String property, @Nullable Object value, String message, InvalidReason reason, @Nullable Throwable exception) {
            this.property = property;
            this.value = value;
            this.message = message;
            this.reason = reason;
            this.exception = exception;
        }

        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        @NonNull
        public Iterator<Validated<T>> iterator() {
            return Stream.of(this).iterator();
        }

        public String getMessage() {
            return this.message;
        }

        public InvalidReason getReason() {
            return this.reason;
        }

        @Nullable
        public Throwable getException() {
            return this.exception;
        }

        @Override
        public T get() throws ValidationException {
            throw new ValidationException(this);
        }

        @Override
        public T orElseGet(Supplier<T> t) throws ValidationException {
            throw new ValidationException(this);
        }

        @Override
        public void orThrow() throws ValidationException {
            throw new ValidationException(this);
        }

        @Override
        public <U> Validated<U> map(Function<T, U> mapping) {
            return this;
        }

        @Override
        public <U> Validated<U> flatMap(BiFunction<T, Valid<T>, Validated<U>> mapping) {
            return this;
        }

        public String getProperty() {
            return this.property;
        }

        @Nullable
        public Object getValue() {
            return this.value;
        }

        public String toString() {
            return "Invalid{property='" + this.property + '\'' + ", value='" + this.value + '\'' + ", message='" + this.message + '\'' + '}';
        }
    }

    public static class Either
    implements Validated<Object> {
        private final Validated<?> left;
        private final Validated<?> right;

        public Either(Validated<?> left, Validated<?> right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean isValid() {
            return this.left.isValid() && this.right.isValid();
        }

        @Override
        public Object get() {
            throw new UnsupportedOperationException("get not supported on more than one Validated object");
        }

        @Override
        public Object orElseGet(Supplier<Object> o) throws ValidationException {
            throw new UnsupportedOperationException("orElse not supported on more than one Validated object");
        }

        @Override
        public void orThrow() throws ValidationException {
            List<Invalid<?>> failures = this.failures();
            if (!failures.isEmpty()) {
                throw new ValidationException(this);
            }
        }

        @Override
        public <U> Validated<U> map(Function<Object, U> mapping) {
            throw new UnsupportedOperationException("cannot invoke map on more than one Validated object");
        }

        @Override
        public <U> Validated<U> flatMap(BiFunction<Object, Valid<Object>, Validated<U>> mapping) {
            throw new UnsupportedOperationException("cannot invoke flatMap on more than one Validated object");
        }

        @Override
        @NonNull
        public Iterator<Validated<Object>> iterator() {
            return Stream.concat(StreamSupport.stream(this.left.spliterator(), false).map((? super T v) -> v.map((T o) -> o)), StreamSupport.stream(this.right.spliterator(), false).map((? super T v) -> v.map((T o) -> o))).iterator();
        }
    }
}

