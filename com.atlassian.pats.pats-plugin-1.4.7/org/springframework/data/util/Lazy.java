/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.data.util;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class Lazy<T>
implements Supplier<T> {
    private static final Lazy<?> EMPTY = new Lazy<Object>(() -> null, null, true);
    private final Supplier<? extends T> supplier;
    @Nullable
    private T value;
    private volatile boolean resolved;

    @Deprecated
    public Lazy(Supplier<? extends T> supplier) {
        this(supplier, null, false);
    }

    private Lazy(Supplier<? extends T> supplier, @Nullable T value, boolean resolved) {
        this.supplier = supplier;
        this.value = value;
        this.resolved = resolved;
    }

    public static <T> Lazy<T> of(Supplier<? extends T> supplier) {
        return new Lazy<T>(supplier);
    }

    public static <T> Lazy<T> of(T value) {
        Assert.notNull(value, (String)"Value must not be null!");
        return new Lazy<Object>(() -> value);
    }

    public static <T> Lazy<T> empty() {
        return EMPTY;
    }

    @Override
    public T get() {
        T value = this.getNullable();
        if (value == null) {
            throw new IllegalStateException("Expected lazy evaluation to yield a non-null value but got null!");
        }
        return value;
    }

    public Optional<T> getOptional() {
        return Optional.ofNullable(this.getNullable());
    }

    public Lazy<T> or(Supplier<? extends T> supplier) {
        Assert.notNull(supplier, (String)"Supplier must not be null!");
        return Lazy.of(() -> this.orElseGet(supplier));
    }

    public Lazy<T> or(T value) {
        Assert.notNull(value, (String)"Value must not be null!");
        return Lazy.of(() -> this.orElse(value));
    }

    @Nullable
    public T orElse(@Nullable T value) {
        T nullable = this.getNullable();
        return nullable == null ? value : nullable;
    }

    @Nullable
    private T orElseGet(Supplier<? extends T> supplier) {
        Assert.notNull(supplier, (String)"Default value supplier must not be null!");
        T value = this.getNullable();
        return value == null ? supplier.get() : value;
    }

    public <S> Lazy<S> map(Function<? super T, ? extends S> function) {
        Assert.notNull(function, (String)"Function must not be null!");
        return Lazy.of(() -> function.apply((T)this.get()));
    }

    public <S> Lazy<S> flatMap(Function<? super T, Lazy<? extends S>> function) {
        Assert.notNull(function, (String)"Function must not be null!");
        return Lazy.of(() -> ((Lazy)function.apply((T)this.get())).get());
    }

    @Nullable
    public T getNullable() {
        if (this.resolved) {
            return this.value;
        }
        this.value = this.supplier.get();
        this.resolved = true;
        return this.value;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Lazy)) {
            return false;
        }
        Lazy lazy = (Lazy)o;
        if (this.resolved != lazy.resolved) {
            return false;
        }
        if (!ObjectUtils.nullSafeEquals(this.supplier, lazy.supplier)) {
            return false;
        }
        return ObjectUtils.nullSafeEquals(this.value, lazy.value);
    }

    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode(this.supplier);
        result = 31 * result + ObjectUtils.nullSafeHashCode(this.value);
        result = 31 * result + (this.resolved ? 1 : 0);
        return result;
    }
}

