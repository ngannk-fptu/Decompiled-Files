/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.lang.holders;

import com.mchange.v1.lang.holders.ThreadSafeDoubleHolder;

public class SynchronizedDoubleHolder
implements ThreadSafeDoubleHolder {
    double value;

    @Override
    public synchronized double getValue() {
        return this.value;
    }

    @Override
    public synchronized void setValue(double d) {
        this.value = d;
    }
}

