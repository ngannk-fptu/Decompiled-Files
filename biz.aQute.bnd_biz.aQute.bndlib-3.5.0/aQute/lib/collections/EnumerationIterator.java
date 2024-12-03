/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.collections;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

public class EnumerationIterator<T>
implements Iterable<T>,
Iterator<T> {
    private final Enumeration<? extends T> enumerator;
    private final AtomicBoolean done = new AtomicBoolean();

    public static <T> EnumerationIterator<T> iterator(Enumeration<? extends T> e) {
        return new EnumerationIterator<T>(e);
    }

    public EnumerationIterator(Enumeration<? extends T> e) {
        this.enumerator = e;
    }

    @Override
    public Iterator<T> iterator() {
        if (!this.done.compareAndSet(false, true)) {
            throw new IllegalStateException("Can only be used once");
        }
        return this;
    }

    @Override
    public boolean hasNext() {
        return this.enumerator.hasMoreElements();
    }

    @Override
    public T next() {
        return this.enumerator.nextElement();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Does not support removes");
    }
}

