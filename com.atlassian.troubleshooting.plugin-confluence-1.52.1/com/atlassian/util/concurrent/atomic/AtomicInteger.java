/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 */
package com.atlassian.util.concurrent.atomic;

import com.google.common.base.Function;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
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
            newValue = (Integer)newValueFactory.apply((Object)oldValue);
        } while (this.get() != oldValue || !this.compareAndSet(oldValue, newValue));
        return newValue;
    }
}

