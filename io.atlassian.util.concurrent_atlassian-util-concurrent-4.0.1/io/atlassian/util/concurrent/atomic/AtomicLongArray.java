/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.util.concurrent.atomic;

import java.util.function.Function;

public class AtomicLongArray
extends java.util.concurrent.atomic.AtomicLongArray {
    private static final long serialVersionUID = 4358621597645662644L;

    public AtomicLongArray(int length) {
        super(length);
    }

    public AtomicLongArray(long[] initialValue) {
        super(initialValue);
    }

    public final long getOrSetAndGetIf(int index, long oldValue, long newValue) {
        long result = this.get(index);
        while (result == oldValue) {
            if (result == newValue) {
                return result;
            }
            this.compareAndSet(index, oldValue, newValue);
            result = this.get(index);
        }
        return result;
    }

    public final long update(int index, Function<Long, Long> newValueFactory) {
        long newValue;
        long oldValue;
        do {
            oldValue = this.get(index);
            newValue = newValueFactory.apply(oldValue);
        } while (this.get(index) != oldValue || !this.compareAndSet(index, oldValue, newValue));
        return newValue;
    }
}

