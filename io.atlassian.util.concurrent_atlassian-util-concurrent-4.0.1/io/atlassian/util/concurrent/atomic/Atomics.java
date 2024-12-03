/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.ThreadSafe
 */
package io.atlassian.util.concurrent.atomic;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Supplier;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class Atomics {
    public static <T> T getAndSetIf(AtomicReference<T> reference, T oldValue, Supplier<T> newValue) {
        T result = Objects.requireNonNull(reference, "reference").get();
        while (result == oldValue) {
            T update = newValue.get();
            if (update == oldValue) {
                return oldValue;
            }
            reference.compareAndSet(oldValue, update);
            result = reference.get();
        }
        return result;
    }

    public static <T> T getAndSetIf(AtomicReference<T> reference, T oldValue, T newValue) {
        T result = Objects.requireNonNull(reference, "reference").get();
        while (result == oldValue) {
            if (newValue == oldValue) {
                return oldValue;
            }
            reference.compareAndSet(oldValue, newValue);
            result = reference.get();
        }
        return result;
    }

    public static <T> T getAndSetIfNull(AtomicReference<T> reference, Supplier<T> newValue) {
        return (T)Atomics.getAndSetIf(reference, null, newValue);
    }

    public static <T> T getAndSetIfNull(AtomicReference<T> reference, T newValue) {
        Supplier<Object> supplier = () -> newValue;
        return (T)Atomics.getAndSetIf(reference, null, supplier);
    }

    public static <T> T getAndSetIf(AtomicReferenceArray<T> reference, int index, T oldValue, Supplier<T> newValue) {
        T result = Objects.requireNonNull(reference, "reference").get(index);
        while (result == oldValue) {
            T update = newValue.get();
            if (update == oldValue) {
                return oldValue;
            }
            reference.compareAndSet(index, oldValue, update);
            result = reference.get(index);
        }
        return result;
    }

    public static <T> T getAndSetIf(AtomicReferenceArray<T> reference, int index, T oldValue, T newValue) {
        T result = Objects.requireNonNull(reference, "reference").get(index);
        while (result == oldValue) {
            if (newValue == oldValue) {
                return oldValue;
            }
            reference.compareAndSet(index, oldValue, newValue);
            result = reference.get(index);
        }
        return result;
    }

    public static <T> T getAndSetIfNull(AtomicReferenceArray<T> reference, int index, Supplier<T> newValue) {
        return (T)Atomics.getAndSetIf(reference, index, null, newValue);
    }

    public static <T> T getAndSetIfNull(AtomicReferenceArray<T> reference, int index, T newValue) {
        Supplier<Object> supplier = () -> newValue;
        return (T)Atomics.getAndSetIf(reference, index, null, supplier);
    }

    public static long getAndSetIf(AtomicLong reference, long oldValue, long newValue) {
        long result = Objects.requireNonNull(reference, "reference").get();
        if (newValue == oldValue) {
            return result;
        }
        while (result == oldValue) {
            reference.compareAndSet(oldValue, newValue);
            result = reference.get();
        }
        return result;
    }

    public static long getAndSetIf(AtomicInteger reference, int oldValue, int newValue) {
        int result = Objects.requireNonNull(reference, "reference").get();
        if (newValue == oldValue) {
            return result;
        }
        while (result == oldValue) {
            reference.compareAndSet(oldValue, newValue);
            result = reference.get();
        }
        return result;
    }

    public static boolean getAndSetIf(AtomicBoolean reference, boolean oldValue, boolean newValue) {
        boolean result = Objects.requireNonNull(reference, "reference").get();
        if (newValue == oldValue) {
            return result;
        }
        while (result == oldValue) {
            reference.compareAndSet(oldValue, newValue);
            result = reference.get();
        }
        return result;
    }

    private Atomics() {
        throw new AssertionError((Object)"cannot be instantiated!");
    }
}

