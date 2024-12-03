/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.NullnessCasts;
import com.google.common.collect.ParametricNullness;
import com.google.common.collect.UnmodifiableIterator;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.NoSuchElementException;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class AbstractIterator<T>
extends UnmodifiableIterator<T> {
    private State state = State.NOT_READY;
    @CheckForNull
    private T next;

    protected AbstractIterator() {
    }

    @CheckForNull
    protected abstract T computeNext();

    @CheckForNull
    @CanIgnoreReturnValue
    protected final T endOfData() {
        this.state = State.DONE;
        return null;
    }

    @Override
    public final boolean hasNext() {
        Preconditions.checkState(this.state != State.FAILED);
        switch (this.state) {
            case DONE: {
                return false;
            }
            case READY: {
                return true;
            }
        }
        return this.tryToComputeNext();
    }

    private boolean tryToComputeNext() {
        this.state = State.FAILED;
        this.next = this.computeNext();
        if (this.state != State.DONE) {
            this.state = State.READY;
            return true;
        }
        return false;
    }

    @Override
    @ParametricNullness
    @CanIgnoreReturnValue
    public final T next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        this.state = State.NOT_READY;
        T result = NullnessCasts.uncheckedCastNullableTToT(this.next);
        this.next = null;
        return result;
    }

    @ParametricNullness
    public final T peek() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        return NullnessCasts.uncheckedCastNullableTToT(this.next);
    }

    private static enum State {
        READY,
        NOT_READY,
        DONE,
        FAILED;

    }
}

