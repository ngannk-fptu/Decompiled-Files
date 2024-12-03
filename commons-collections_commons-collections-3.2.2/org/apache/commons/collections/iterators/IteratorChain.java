/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.iterators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.collections.iterators.EmptyIterator;
import org.apache.commons.collections.list.UnmodifiableList;

public class IteratorChain
implements Iterator {
    protected final List iteratorChain = new ArrayList();
    protected int currentIteratorIndex = 0;
    protected Iterator currentIterator = null;
    protected Iterator lastUsedIterator = null;
    protected boolean isLocked = false;

    public IteratorChain() {
    }

    public IteratorChain(Iterator iterator) {
        this.addIterator(iterator);
    }

    public IteratorChain(Iterator a, Iterator b) {
        this.addIterator(a);
        this.addIterator(b);
    }

    public IteratorChain(Iterator[] iterators) {
        for (int i = 0; i < iterators.length; ++i) {
            this.addIterator(iterators[i]);
        }
    }

    public IteratorChain(Collection iterators) {
        Iterator it = iterators.iterator();
        while (it.hasNext()) {
            Iterator item = (Iterator)it.next();
            this.addIterator(item);
        }
    }

    public void addIterator(Iterator iterator) {
        this.checkLocked();
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        this.iteratorChain.add(iterator);
    }

    public void setIterator(int index, Iterator iterator) throws IndexOutOfBoundsException {
        this.checkLocked();
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        this.iteratorChain.set(index, iterator);
    }

    public List getIterators() {
        return UnmodifiableList.decorate(this.iteratorChain);
    }

    public int size() {
        return this.iteratorChain.size();
    }

    public boolean isLocked() {
        return this.isLocked;
    }

    private void checkLocked() {
        if (this.isLocked) {
            throw new UnsupportedOperationException("IteratorChain cannot be changed after the first use of a method from the Iterator interface");
        }
    }

    private void lockChain() {
        if (!this.isLocked) {
            this.isLocked = true;
        }
    }

    protected void updateCurrentIterator() {
        if (this.currentIterator == null) {
            this.currentIterator = this.iteratorChain.isEmpty() ? EmptyIterator.INSTANCE : (Iterator)this.iteratorChain.get(0);
            this.lastUsedIterator = this.currentIterator;
        }
        while (!this.currentIterator.hasNext() && this.currentIteratorIndex < this.iteratorChain.size() - 1) {
            ++this.currentIteratorIndex;
            this.currentIterator = (Iterator)this.iteratorChain.get(this.currentIteratorIndex);
        }
    }

    public boolean hasNext() {
        this.lockChain();
        this.updateCurrentIterator();
        this.lastUsedIterator = this.currentIterator;
        return this.currentIterator.hasNext();
    }

    public Object next() {
        this.lockChain();
        this.updateCurrentIterator();
        this.lastUsedIterator = this.currentIterator;
        return this.currentIterator.next();
    }

    public void remove() {
        this.lockChain();
        if (this.currentIterator == null) {
            this.updateCurrentIterator();
        }
        this.lastUsedIterator.remove();
    }
}

