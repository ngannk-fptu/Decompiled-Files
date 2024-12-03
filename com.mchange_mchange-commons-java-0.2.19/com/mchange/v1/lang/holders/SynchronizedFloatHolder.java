/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.lang.holders;

import com.mchange.v1.lang.holders.ThreadSafeFloatHolder;

public class SynchronizedFloatHolder
implements ThreadSafeFloatHolder {
    float value;

    @Override
    public synchronized float getValue() {
        return this.value;
    }

    @Override
    public synchronized void setValue(float f) {
        this.value = f;
    }
}

