/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.iterators;

import java.util.Iterator;

public class ProxyIterator
implements Iterator {
    private Iterator iterator;

    public ProxyIterator() {
    }

    public ProxyIterator(Iterator iterator) {
        this.iterator = iterator;
    }

    public boolean hasNext() {
        return this.getIterator().hasNext();
    }

    public Object next() {
        return this.getIterator().next();
    }

    public void remove() {
        this.getIterator().remove();
    }

    public Iterator getIterator() {
        return this.iterator;
    }

    public void setIterator(Iterator iterator) {
        this.iterator = iterator;
    }
}

