/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.lang.holders;

import com.mchange.v1.lang.holders.ThreadSafeLongHolder;

public class SynchronizedLongHolder
implements ThreadSafeLongHolder {
    long value;

    @Override
    public synchronized long getValue() {
        return this.value;
    }

    @Override
    public synchronized void setValue(long l) {
        this.value = l;
    }

    public SynchronizedLongHolder(long l) {
        this.value = l;
    }

    public SynchronizedLongHolder() {
        this(0L);
    }
}

