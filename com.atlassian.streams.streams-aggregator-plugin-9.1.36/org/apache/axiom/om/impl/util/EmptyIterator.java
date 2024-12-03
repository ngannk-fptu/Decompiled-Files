/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class EmptyIterator
implements Iterator {
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public boolean hasNext() {
        return false;
    }

    public Object next() {
        throw new NoSuchElementException();
    }
}

