/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.codehaus.groovy.util.ManagedReference;
import org.codehaus.groovy.util.ReferenceBundle;

public class ManagedConcurrentLinkedQueue<T>
implements Iterable<T> {
    private final ReferenceBundle bundle;
    private final ConcurrentLinkedQueue<Element<T>> queue;

    public ManagedConcurrentLinkedQueue(ReferenceBundle bundle) {
        this.bundle = bundle;
        this.queue = new ConcurrentLinkedQueue();
    }

    public void add(T value) {
        Element<T> e = new Element<T>(value);
        this.queue.offer(e);
    }

    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    public T[] toArray(T[] tArray) {
        return this.values().toArray(tArray);
    }

    public List<T> values() {
        ArrayList<T> result = new ArrayList<T>();
        Iterator<T> itr = this.iterator();
        while (itr.hasNext()) {
            result.add(itr.next());
        }
        return result;
    }

    @Override
    public Iterator<T> iterator() {
        return new Itr(this.queue.iterator());
    }

    private class Itr
    implements Iterator<T> {
        final Iterator<Element<T>> wrapped;
        T value;
        Element<T> current;
        boolean exhausted;

        Itr(Iterator<Element<T>> wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public boolean hasNext() {
            if (!this.exhausted && this.value == null) {
                this.advance();
            }
            return this.value != null;
        }

        @Override
        public T next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            Object next = this.value;
            this.value = null;
            return next;
        }

        @Override
        public void remove() {
            if (this.current == null || this.value != null) {
                throw new IllegalStateException("Next method has not been called");
            }
            this.wrapped.remove();
            this.current = null;
        }

        private void advance() {
            while (this.wrapped.hasNext()) {
                Element e = this.wrapped.next();
                Object v = e.get();
                if (v != null) {
                    this.current = e;
                    this.value = v;
                    return;
                }
                this.wrapped.remove();
            }
            this.value = null;
            this.current = null;
            this.exhausted = true;
        }
    }

    private class Element<V>
    extends ManagedReference<V> {
        Element(V value) {
            super(ManagedConcurrentLinkedQueue.this.bundle, value);
        }

        @Override
        public void finalizeReference() {
            ManagedConcurrentLinkedQueue.this.queue.remove(this);
            super.finalizeReference();
        }
    }
}

