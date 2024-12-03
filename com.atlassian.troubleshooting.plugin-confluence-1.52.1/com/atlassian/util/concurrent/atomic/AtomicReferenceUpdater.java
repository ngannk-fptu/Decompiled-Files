/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 */
package com.atlassian.util.concurrent.atomic;

import com.atlassian.util.concurrent.Assertions;
import com.google.common.base.Function;
import java.util.concurrent.atomic.AtomicReference;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AtomicReferenceUpdater<T>
implements Function<T, T> {
    private final AtomicReference<T> reference;

    public AtomicReferenceUpdater(AtomicReference<T> reference) {
        this.reference = Assertions.notNull("reference", reference);
    }

    public final T update() {
        Object newValue;
        T oldValue;
        do {
            oldValue = this.reference.get();
            newValue = this.apply(oldValue);
        } while (this.reference.get() != oldValue || !this.reference.compareAndSet(oldValue, newValue));
        return (T)newValue;
    }
}

