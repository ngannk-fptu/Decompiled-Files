/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.lang.holders;

import com.mchange.v1.lang.holders.ThreadSafeIntHolder;

public class VolatileIntHolder
implements ThreadSafeIntHolder {
    volatile int value;

    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public void setValue(int n) {
        this.value = n;
    }
}

