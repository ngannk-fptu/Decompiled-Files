/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.util.concurrent.atomic;

import java.util.function.Function;
import java.util.function.Supplier;

public class AtomicReference<V>
extends java.util.concurrent.atomic.AtomicReference<V> {
    private static final long serialVersionUID = -6792744642556679378L;

    public AtomicReference() {
    }

    public AtomicReference(V initialValue) {
        super(initialValue);
    }

    public final V getOrSetAndGetIf(V oldValue, Supplier<V> newValue) {
        Object result = this.get();
        while (result == oldValue) {
            V update = newValue.get();
            if (update == oldValue) {
                return oldValue;
            }
            this.compareAndSet(oldValue, update);
            result = this.get();
        }
        return result;
    }

    public final V getOrSetAndGetIf(V oldValue, V newValue) {
        Object result = this.get();
        while (result == oldValue) {
            if (result == newValue) {
                return result;
            }
            this.compareAndSet(oldValue, newValue);
            result = this.get();
        }
        return result;
    }

    public final V update(Function<V, V> newValueFactory) {
        V newValue;
        Object oldValue;
        do {
            oldValue = this.get();
            newValue = newValueFactory.apply(oldValue);
        } while (this.get() != oldValue || !this.compareAndSet(oldValue, newValue));
        return newValue;
    }
}

