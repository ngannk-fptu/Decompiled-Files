/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.lang.holders;

import com.mchange.v1.lang.holders.ThreadSafeCharHolder;

public class VolatileCharHolder
implements ThreadSafeCharHolder {
    volatile char value;

    @Override
    public char getValue() {
        return this.value;
    }

    @Override
    public void setValue(char c) {
        this.value = c;
    }
}

