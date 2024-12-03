/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.lang.holders;

import com.mchange.v1.lang.holders.ThreadSafeBooleanHolder;

public class SynchronizedBooleanHolder
implements ThreadSafeBooleanHolder {
    boolean value;

    @Override
    public synchronized boolean getValue() {
        return this.value;
    }

    @Override
    public synchronized void setValue(boolean bl) {
        this.value = bl;
    }
}

