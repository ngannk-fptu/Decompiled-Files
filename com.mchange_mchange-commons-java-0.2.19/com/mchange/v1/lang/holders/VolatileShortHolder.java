/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.lang.holders;

import com.mchange.v1.lang.holders.ThreadSafeShortHolder;

public class VolatileShortHolder
implements ThreadSafeShortHolder {
    volatile short value;

    @Override
    public short getValue() {
        return this.value;
    }

    @Override
    public void setValue(short s) {
        this.value = s;
    }
}

