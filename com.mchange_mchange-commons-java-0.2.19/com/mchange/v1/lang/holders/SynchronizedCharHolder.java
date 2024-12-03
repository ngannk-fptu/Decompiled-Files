/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.lang.holders;

import com.mchange.v1.lang.holders.ThreadSafeCharHolder;

public class SynchronizedCharHolder
implements ThreadSafeCharHolder {
    char value;

    @Override
    public synchronized char getValue() {
        return this.value;
    }

    @Override
    public synchronized void setValue(char c) {
        this.value = c;
    }
}

