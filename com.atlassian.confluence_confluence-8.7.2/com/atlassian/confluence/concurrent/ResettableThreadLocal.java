/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.concurrent;

public abstract class ResettableThreadLocal<T>
extends ThreadLocal<T> {
    public void reset() {
        this.set(this.initialValue());
    }
}

