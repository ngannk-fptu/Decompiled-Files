/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.lang.holders;

import com.mchange.v1.lang.holders.ThreadSafeBooleanHolder;

public class VolatileBooleanHolder
implements ThreadSafeBooleanHolder {
    volatile boolean value;

    @Override
    public boolean getValue() {
        return this.value;
    }

    @Override
    public void setValue(boolean bl) {
        this.value = bl;
    }
}

