/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.iterators;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

public class EnumerationIterator
implements Iterator {
    private Collection collection;
    private Enumeration enumeration;
    private Object last;

    public EnumerationIterator() {
        this(null, null);
    }

    public EnumerationIterator(Enumeration enumeration) {
        this(enumeration, null);
    }

    public EnumerationIterator(Enumeration enumeration, Collection collection) {
        this.enumeration = enumeration;
        this.collection = collection;
        this.last = null;
    }

    public boolean hasNext() {
        return this.enumeration.hasMoreElements();
    }

    public Object next() {
        this.last = this.enumeration.nextElement();
        return this.last;
    }

    public void remove() {
        if (this.collection != null) {
            if (this.last == null) {
                throw new IllegalStateException("next() must have been called for remove() to function");
            }
        } else {
            throw new UnsupportedOperationException("No Collection associated with this Iterator");
        }
        this.collection.remove(this.last);
    }

    public Enumeration getEnumeration() {
        return this.enumeration;
    }

    public void setEnumeration(Enumeration enumeration) {
        this.enumeration = enumeration;
    }
}

