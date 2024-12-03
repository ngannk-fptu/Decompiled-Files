/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.util.concurrent.atomic;

import java.util.function.Function;
import java.util.function.Supplier;

public class AtomicReferenceArray<E>
extends java.util.concurrent.atomic.AtomicReferenceArray<E> {
    private static final long serialVersionUID = 6669693075971189L;

    public AtomicReferenceArray(int length) {
        super(length);
    }

    public AtomicReferenceArray(E[] initialValue) {
        super(initialValue);
    }

    public final E getOrSetAndGetIf(int index, E oldValue, Supplier<E> newValue) {
        Object result = this.get(index);
        while (result == oldValue) {
            E update = newValue.get();
            if (update == oldValue) {
                return oldValue;
            }
            this.compareAndSet(index, oldValue, update);
            result = this.get(index);
        }
        return result;
    }

    public final E getOrSetAndGetIf(int index, E oldValue, E newValue) {
        Object result = this.get(index);
        while (result == oldValue) {
            if (result == newValue) {
                return result;
            }
            this.compareAndSet(index, oldValue, newValue);
            result = this.get(index);
        }
        return result;
    }

    public final E update(int index, Function<E, E> newValueFactory) {
        E newValue;
        Object oldValue;
        do {
            oldValue = this.get(index);
            newValue = newValueFactory.apply(oldValue);
        } while (this.get(index) != oldValue || !this.compareAndSet(index, oldValue, newValue));
        return newValue;
    }
}

