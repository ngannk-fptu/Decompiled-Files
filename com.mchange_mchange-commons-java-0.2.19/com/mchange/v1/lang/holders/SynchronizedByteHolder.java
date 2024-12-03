/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.lang.holders;

import com.mchange.v1.lang.holders.ThreadSafeByteHolder;

public class SynchronizedByteHolder
implements ThreadSafeByteHolder {
    byte value;

    @Override
    public synchronized byte getValue() {
        return this.value;
    }

    @Override
    public synchronized void setValue(byte by) {
        this.value = by;
    }
}

