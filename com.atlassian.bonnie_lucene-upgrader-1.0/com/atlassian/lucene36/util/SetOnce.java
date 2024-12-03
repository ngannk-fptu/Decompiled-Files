/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import java.util.concurrent.atomic.AtomicBoolean;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
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
    extends RuntimeException {
        public AlreadySetException() {
            super("The object cannot be set twice!");
        }
    }
}

