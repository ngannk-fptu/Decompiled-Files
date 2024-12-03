/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.util;

import com.google.inject.internal.util.$Preconditions;
import java.util.Iterator;
import java.util.NoSuchElementException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class $AbstractIterator<T>
implements Iterator<T> {
    private State state = State.NOT_READY;
    private T next;

    protected abstract T computeNext();

    protected final T endOfData() {
        this.state = State.DONE;
        return null;
    }

    @Override
    public boolean hasNext() {
        $Preconditions.checkState(this.state != State.FAILED);
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
    public T next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        this.state = State.NOT_READY;
        return this.next;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum State {
        READY,
        NOT_READY,
        DONE,
        FAILED;

    }
}

