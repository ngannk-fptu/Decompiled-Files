/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.iterators;

import java.util.Iterator;
import org.apache.commons.collections.Transformer;

public class TransformIterator
implements Iterator {
    private Iterator iterator;
    private Transformer transformer;

    public TransformIterator() {
    }

    public TransformIterator(Iterator iterator) {
        this.iterator = iterator;
    }

    public TransformIterator(Iterator iterator, Transformer transformer) {
        this.iterator = iterator;
        this.transformer = transformer;
    }

    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    public Object next() {
        return this.transform(this.iterator.next());
    }

    public void remove() {
        this.iterator.remove();
    }

    public Iterator getIterator() {
        return this.iterator;
    }

    public void setIterator(Iterator iterator) {
        this.iterator = iterator;
    }

    public Transformer getTransformer() {
        return this.transformer;
    }

    public void setTransformer(Transformer transformer) {
        this.transformer = transformer;
    }

    protected Object transform(Object source) {
        if (this.transformer != null) {
            return this.transformer.transform(source);
        }
        return source;
    }
}

