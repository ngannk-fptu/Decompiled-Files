/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.threadlocal;

import com.atlassian.threadlocal.RegisteredThreadLocals;

public class RegisteredThreadLocal<T>
extends ThreadLocal<T> {
    @Override
    protected final T initialValue() {
        RegisteredThreadLocals.register(this);
        return this.supplyInitialValue();
    }

    protected T supplyInitialValue() {
        return null;
    }

    @Override
    public void set(T value) {
        RegisteredThreadLocals.register(this);
        super.set(value);
    }
}

