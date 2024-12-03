/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.impl.support;

import com.atlassian.plugin.webresource.impl.support.Tuple;
import java.util.concurrent.atomic.AtomicReference;

public abstract class ResettableLazyReferenceWithVersionCheck<T> {
    AtomicReference<Tuple<Integer, T>> atomicReference = new AtomicReference<Tuple<Object, Object>>(new Tuple<Object, Object>(null, null));

    public T get() {
        Tuple<Integer, T> updated;
        Tuple<Integer, T> original;
        do {
            if ((original = this.atomicReference.get()).getLast() == null || original.getFirst().intValue() != this.getVersion()) continue;
            return original.getLast();
        } while (!this.atomicReference.compareAndSet(original, updated = new Tuple<Integer, T>(this.getVersion(), this.create())));
        return updated.getLast();
    }

    public void reset() {
        this.atomicReference.set(new Tuple<Object, Object>(null, null));
    }

    protected abstract int getVersion();

    protected abstract T create();
}

