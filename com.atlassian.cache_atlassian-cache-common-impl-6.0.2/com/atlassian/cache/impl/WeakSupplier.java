/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.cache.impl;

import java.lang.ref.WeakReference;
import java.util.function.Supplier;

public class WeakSupplier<V>
extends WeakReference<V>
implements Supplier<V> {
    public WeakSupplier(V referent) {
        super(referent);
    }
}

