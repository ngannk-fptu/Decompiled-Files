/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.lang.holders;

import com.mchange.v1.lang.holders.ThreadSafeFloatHolder;

public class VolatileFloatHolder
implements ThreadSafeFloatHolder {
    volatile float value;

    @Override
    public float getValue() {
        return this.value;
    }

    @Override
    public void setValue(float f) {
        this.value = f;
    }
}

