/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.lang.holders;

import com.mchange.v1.lang.holders.ThreadSafeByteHolder;

public class VolatileByteHolder
implements ThreadSafeByteHolder {
    volatile byte value;

    @Override
    public byte getValue() {
        return this.value;
    }

    @Override
    public void setValue(byte by) {
        this.value = by;
    }
}

