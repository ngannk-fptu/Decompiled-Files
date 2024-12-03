/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.util;

import java.util.ArrayList;
import java.util.Iterator;
import org.codehaus.groovy.util.ManagedReference;
import org.codehaus.groovy.util.ReferenceBundle;

@Deprecated
public class ManagedLinkedList<T> {
    private Element<T> tail;
    private Element<T> head;
    private ReferenceBundle bundle;

    public ManagedLinkedList(ReferenceBundle bundle) {
        this.bundle = bundle;
    }

    public void add(T value) {
        Element<T> element = new Element<T>(this.bundle, value);
        element.previous = this.tail;
        if (this.tail != null) {
            this.tail.next = element;
        }
        this.tail = element;
        if (this.head == null) {
            this.head = element;
        }
    }

    public Iterator<T> iterator() {
        return new Iter();
    }

    public T[] toArray(T[] tArray) {
        ArrayList<T> array = new ArrayList<T>(100);
        Iterator<T> it = this.iterator();
        while (it.hasNext()) {
            T val = it.next();
            if (val == null) continue;
            array.add(val);
        }
        return array.toArray(tArray);
    }

    public boolean isEmpty() {
        return this.head == null;
    }

    private final class Iter
    implements Iterator<T> {
        private Element<T> current;
        private boolean currentHandled = false;

        Iter() {
            this.current = ManagedLinkedList.this.head;
        }

        @Override
        public boolean hasNext() {
            if (this.current == null) {
                return false;
            }
            if (this.currentHandled) {
                return this.current.next != null;
            }
            return this.current != null;
        }

        @Override
        public T next() {
            if (this.currentHandled) {
                this.current = this.current.next;
            }
            this.currentHandled = true;
            if (this.current == null) {
                return null;
            }
            return this.current.get();
        }

        @Override
        public void remove() {
            if (this.current != null) {
                this.current.finalizeReference();
            }
        }
    }

    private final class Element<V>
    extends ManagedReference<V> {
        Element next;
        Element previous;

        public Element(ReferenceBundle bundle, V value) {
            super(bundle, value);
        }

        @Override
        public void finalizeReference() {
            if (this.previous != null && this.previous.next != null) {
                this.previous.next = this.next;
            }
            if (this.next != null && this.next.previous != null) {
                this.next.previous = this.previous;
            }
            if (this == ManagedLinkedList.this.head) {
                ManagedLinkedList.this.head = this.next;
            }
            this.next = null;
            if (this == ManagedLinkedList.this.tail) {
                ManagedLinkedList.this.tail = this.previous;
            }
            this.previous = null;
            super.finalizeReference();
        }
    }
}

