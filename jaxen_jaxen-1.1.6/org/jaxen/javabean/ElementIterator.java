/*
 * Decompiled with CFR 0.152.
 */
package org.jaxen.javabean;

import java.util.Iterator;
import org.jaxen.javabean.Element;

public class ElementIterator
implements Iterator {
    private Element parent;
    private String name;
    private Iterator iterator;

    public ElementIterator(Element parent, String name, Iterator iterator) {
        this.parent = parent;
        this.name = name;
        this.iterator = iterator;
    }

    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    public Object next() {
        return new Element(this.parent, this.name, this.iterator.next());
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}

