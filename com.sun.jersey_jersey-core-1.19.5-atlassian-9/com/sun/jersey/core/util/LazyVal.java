/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.util;

public abstract class LazyVal<T> {
    private volatile T val;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T get() {
        T result = this.val;
        if (result == null) {
            LazyVal lazyVal = this;
            synchronized (lazyVal) {
                result = this.val;
                if (result == null) {
                    this.val = result = this.instance();
                }
            }
        }
        return result;
    }

    public void set(T t) {
        this.val = t;
    }

    protected abstract T instance();
}

