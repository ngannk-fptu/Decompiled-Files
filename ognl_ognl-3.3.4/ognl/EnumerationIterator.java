/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.util.Enumeration;
import java.util.Iterator;

public class EnumerationIterator
implements Iterator {
    private Enumeration e;

    public EnumerationIterator(Enumeration e) {
        this.e = e;
    }

    @Override
    public boolean hasNext() {
        return this.e.hasMoreElements();
    }

    public Object next() {
        return this.e.nextElement();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() not supported by Enumeration");
    }
}

