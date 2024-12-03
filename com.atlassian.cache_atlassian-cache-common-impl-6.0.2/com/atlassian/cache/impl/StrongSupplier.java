/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.cache.impl;

import java.util.function.Supplier;

public class StrongSupplier<V>
implements Supplier<V> {
    private final V referent;

    public StrongSupplier(V referent) {
        this.referent = referent;
    }

    @Override
    public V get() {
        return this.referent;
    }
}

