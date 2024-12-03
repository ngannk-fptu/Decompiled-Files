/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections.ArrayStack;
import org.apache.commons.collections.Transformer;

public class ObjectGraphIterator
implements Iterator {
    protected final ArrayStack stack = new ArrayStack(8);
    protected Object root;
    protected Transformer transformer;
    protected boolean hasNext = false;
    protected Iterator currentIterator;
    protected Object currentValue;
    protected Iterator lastUsedIterator;

    public ObjectGraphIterator(Object root, Transformer transformer) {
        if (root instanceof Iterator) {
            this.currentIterator = (Iterator)root;
        } else {
            this.root = root;
        }
        this.transformer = transformer;
    }

    public ObjectGraphIterator(Iterator rootIterator) {
        this.currentIterator = rootIterator;
        this.transformer = null;
    }

    protected void updateCurrentIterator() {
        if (this.hasNext) {
            return;
        }
        if (this.currentIterator == null) {
            if (this.root != null) {
                if (this.transformer == null) {
                    this.findNext(this.root);
                } else {
                    this.findNext(this.transformer.transform(this.root));
                }
                this.root = null;
            }
        } else {
            this.findNextByIterator(this.currentIterator);
        }
    }

    protected void findNext(Object value) {
        if (value instanceof Iterator) {
            this.findNextByIterator((Iterator)value);
        } else {
            this.currentValue = value;
            this.hasNext = true;
        }
    }

    protected void findNextByIterator(Iterator iterator) {
        if (iterator != this.currentIterator) {
            if (this.currentIterator != null) {
                this.stack.push(this.currentIterator);
            }
            this.currentIterator = iterator;
        }
        while (this.currentIterator.hasNext() && !this.hasNext) {
            Object next = this.currentIterator.next();
            if (this.transformer != null) {
                next = this.transformer.transform(next);
            }
            this.findNext(next);
        }
        if (!this.hasNext && !this.stack.isEmpty()) {
            this.currentIterator = (Iterator)this.stack.pop();
            this.findNextByIterator(this.currentIterator);
        }
    }

    public boolean hasNext() {
        this.updateCurrentIterator();
        return this.hasNext;
    }

    public Object next() {
        this.updateCurrentIterator();
        if (!this.hasNext) {
            throw new NoSuchElementException("No more elements in the iteration");
        }
        this.lastUsedIterator = this.currentIterator;
        Object result = this.currentValue;
        this.currentValue = null;
        this.hasNext = false;
        return result;
    }

    public void remove() {
        if (this.lastUsedIterator == null) {
            throw new IllegalStateException("Iterator remove() cannot be called at this time");
        }
        this.lastUsedIterator.remove();
        this.lastUsedIterator = null;
    }
}

