/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.lang.holders;

import com.mchange.v1.lang.holders.ThreadSafeShortHolder;

public class SynchronizedShortHolder
implements ThreadSafeShortHolder {
    short value;

    @Override
    public synchronized short getValue() {
        return this.value;
    }

    @Override
    public synchronized void setValue(short s) {
        this.value = s;
    }
}

