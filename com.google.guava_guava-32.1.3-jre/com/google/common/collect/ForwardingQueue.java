/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.ParametricNullness;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.NoSuchElementException;
import java.util.Queue;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingQueue<E>
extends ForwardingCollection<E>
implements Queue<E> {
    protected ForwardingQueue() {
    }

    @Override
    protected abstract Queue<E> delegate();

    @Override
    @CanIgnoreReturnValue
    public boolean offer(@ParametricNullness E o) {
        return this.delegate().offer(o);
    }

    @Override
    @CheckForNull
    @CanIgnoreReturnValue
    public E poll() {
        return this.delegate().poll();
    }

    @Override
    @ParametricNullness
    @CanIgnoreReturnValue
    public E remove() {
        return this.delegate().remove();
    }

    @Override
    @CheckForNull
    public E peek() {
        return this.delegate().peek();
    }

    @Override
    @ParametricNullness
    public E element() {
        return this.delegate().element();
    }

    protected boolean standardOffer(@ParametricNullness E e) {
        try {
            return this.add(e);
        }
        catch (IllegalStateException caught) {
            return false;
        }
    }

    @CheckForNull
    protected E standardPeek() {
        try {
            return this.element();
        }
        catch (NoSuchElementException caught) {
            return null;
        }
    }

    @CheckForNull
    protected E standardPoll() {
        try {
            return this.remove();
        }
        catch (NoSuchElementException caught) {
            return null;
        }
    }
}

