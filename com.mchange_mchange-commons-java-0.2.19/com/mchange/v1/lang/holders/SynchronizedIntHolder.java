/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.lang.holders;

import com.mchange.v1.lang.holders.ThreadSafeIntHolder;

public class SynchronizedIntHolder
implements ThreadSafeIntHolder {
    int value;

    @Override
    public synchronized int getValue() {
        return this.value;
    }

    @Override
    public synchronized void setValue(int n) {
        this.value = n;
    }
}

