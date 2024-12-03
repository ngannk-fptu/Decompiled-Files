/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.UnmodifiableIterator;
import java.util.NoSuchElementException;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class AbstractSequentialIterator<T>
extends UnmodifiableIterator<T> {
    @CheckForNull
    private T nextOrNull;

    protected AbstractSequentialIterator(@CheckForNull T firstOrNull) {
        this.nextOrNull = firstOrNull;
    }

    @CheckForNull
    protected abstract T computeNext(T var1);

    @Override
    public final boolean hasNext() {
        return this.nextOrNull != null;
    }

    @Override
    public final T next() {
        if (this.nextOrNull == null) {
            throw new NoSuchElementException();
        }
        T oldNext = this.nextOrNull;
        this.nextOrNull = this.computeNext(oldNext);
        return oldNext;
    }
}

