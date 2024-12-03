/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ParametricNullness;
import com.google.common.collect.UnmodifiableListIterator;
import java.util.NoSuchElementException;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class AbstractIndexedListIterator<E>
extends UnmodifiableListIterator<E> {
    private final int size;
    private int position;

    @ParametricNullness
    protected abstract E get(int var1);

    protected AbstractIndexedListIterator(int size) {
        this(size, 0);
    }

    protected AbstractIndexedListIterator(int size, int position) {
        Preconditions.checkPositionIndex(position, size);
        this.size = size;
        this.position = position;
    }

    @Override
    public final boolean hasNext() {
        return this.position < this.size;
    }

    @Override
    @ParametricNullness
    public final E next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        return this.get(this.position++);
    }

    @Override
    public final int nextIndex() {
        return this.position;
    }

    @Override
    public final boolean hasPrevious() {
        return this.position > 0;
    }

    @Override
    @ParametricNullness
    public final E previous() {
        if (!this.hasPrevious()) {
            throw new NoSuchElementException();
        }
        return this.get(--this.position);
    }

    @Override
    public final int previousIndex() {
        return this.position - 1;
    }
}

