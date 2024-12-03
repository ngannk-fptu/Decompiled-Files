/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.iterators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.commons.collections.ResettableListIterator;

public class ListIteratorWrapper
implements ResettableListIterator {
    private static final String UNSUPPORTED_OPERATION_MESSAGE = "ListIteratorWrapper does not support optional operations of ListIterator.";
    private final Iterator iterator;
    private final List list = new ArrayList();
    private int currentIndex = 0;
    private int wrappedIteratorIndex = 0;

    public ListIteratorWrapper(Iterator iterator) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        this.iterator = iterator;
    }

    public void add(Object obj) throws UnsupportedOperationException {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public boolean hasNext() {
        if (this.currentIndex == this.wrappedIteratorIndex) {
            return this.iterator.hasNext();
        }
        return true;
    }

    public boolean hasPrevious() {
        return this.currentIndex != 0;
    }

    public Object next() throws NoSuchElementException {
        if (this.currentIndex < this.wrappedIteratorIndex) {
            ++this.currentIndex;
            return this.list.get(this.currentIndex - 1);
        }
        Object retval = this.iterator.next();
        this.list.add(retval);
        ++this.currentIndex;
        ++this.wrappedIteratorIndex;
        return retval;
    }

    public int nextIndex() {
        return this.currentIndex;
    }

    public Object previous() throws NoSuchElementException {
        if (this.currentIndex == 0) {
            throw new NoSuchElementException();
        }
        --this.currentIndex;
        return this.list.get(this.currentIndex);
    }

    public int previousIndex() {
        return this.currentIndex - 1;
    }

    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public void set(Object obj) throws UnsupportedOperationException {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    public void reset() {
        this.currentIndex = 0;
    }
}

