/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.util.concurrent.atomic;

import java.util.function.Function;

public class AtomicInteger
extends java.util.concurrent.atomic.AtomicInteger {
    private static final long serialVersionUID = 8415715351483640403L;

    public AtomicInteger() {
    }

    public AtomicInteger(int initialValue) {
        super(initialValue);
    }

    public final int getOrSetAndGetIf(int oldValue, int newValue) {
        int result = this.get();
        while (result == oldValue) {
            if (result == newValue) {
                return oldValue;
            }
            this.compareAndSet(oldValue, newValue);
            result = this.get();
        }
        return result;
    }

    public final int update(Function<Integer, Integer> newValueFactory) {
        int newValue;
        int oldValue;
        do {
            oldValue = this.get();
            newValue = newValueFactory.apply(oldValue);
        } while (this.get() != oldValue || !this.compareAndSet(oldValue, newValue));
        return newValue;
    }
}

