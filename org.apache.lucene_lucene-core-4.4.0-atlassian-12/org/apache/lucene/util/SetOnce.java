/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import java.util.concurrent.atomic.AtomicBoolean;

public final class SetOnce<T> {
    private volatile T obj = null;
    private final AtomicBoolean set;

    public SetOnce() {
        this.set = new AtomicBoolean(false);
    }

    public SetOnce(T obj) {
        this.obj = obj;
        this.set = new AtomicBoolean(true);
    }

    public final void set(T obj) {
        if (!this.set.compareAndSet(false, true)) {
            throw new AlreadySetException();
        }
        this.obj = obj;
    }

    public final T get() {
        return this.obj;
    }

    public static final class AlreadySetException
    extends IllegalStateException {
        public AlreadySetException() {
            super("The object cannot be set twice!");
        }
    }
}

