/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.util.concurrent.atomic;

import java.util.function.Function;

public class AtomicLong
extends java.util.concurrent.atomic.AtomicLong {
    private static final long serialVersionUID = -3751676505640700325L;

    public AtomicLong() {
    }

    public AtomicLong(long initialValue) {
        super(initialValue);
    }

    public final long getOrSetAndGetIf(long oldValue, long newValue) {
        long result = this.get();
        while (result == oldValue) {
            if (result == newValue) {
                return result;
            }
            this.compareAndSet(oldValue, newValue);
            result = this.get();
        }
        return result;
    }

    public final long update(Function<Long, Long> newValueFactory) {
        long newValue;
        long oldValue;
        do {
            oldValue = this.get();
            newValue = newValueFactory.apply(oldValue);
        } while (this.get() != oldValue || !this.compareAndSet(oldValue, newValue));
        return newValue;
    }
}

