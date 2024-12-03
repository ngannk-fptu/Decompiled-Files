/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.core.util;

public class Holder<T> {
    private T value;

    public Holder() {
        this.value = null;
    }

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return this.value;
    }

    public Holder(T value) {
        this.value = value;
    }
}

