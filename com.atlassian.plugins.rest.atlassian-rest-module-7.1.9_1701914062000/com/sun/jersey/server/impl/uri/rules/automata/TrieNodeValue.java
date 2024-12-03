/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.uri.rules.automata;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class TrieNodeValue<T> {
    private Object value = null;

    public void set(T value) {
        if (this.value == null) {
            this.value = value;
        } else if (this.value.getClass().isArray()) {
            Object[] old = (Object[])this.value;
            Object[] copy = new Object[old.length + 1];
            System.arraycopy(old, 0, copy, 0, old.length + 1);
            copy[copy.length - 1] = value;
            this.value = copy;
        } else {
            this.value = new Object[]{this.value, value};
        }
    }

    public Iterator<T> getIterator() {
        if (this.value == null) {
            return new EmptyIterator();
        }
        if (this.value.getClass().isArray()) {
            return new ArrayIterator((Object[])this.value);
        }
        return new SingleEntryIterator<Object>(this.value);
    }

    public boolean isEmpty() {
        return this.value == null;
    }

    static final class EmptyIterator<T>
    implements Iterator<T> {
        EmptyIterator() {
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public T next() {
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static final class SingleEntryIterator<T>
    implements Iterator<T> {
        private T t;

        SingleEntryIterator(T t) {
            this.t = t;
        }

        @Override
        public boolean hasNext() {
            return this.t != null;
        }

        @Override
        public T next() {
            if (this.hasNext()) {
                T _t = this.t;
                this.t = null;
                return _t;
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static final class ArrayIterator<T>
    implements Iterator<T> {
        private Object[] data;
        private int cursor = 0;

        public ArrayIterator(Object[] data) {
            this.data = data;
        }

        @Override
        public boolean hasNext() {
            return this.cursor < this.data.length;
        }

        @Override
        public T next() {
            if (this.hasNext()) {
                return (T)this.data[this.cursor++];
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

