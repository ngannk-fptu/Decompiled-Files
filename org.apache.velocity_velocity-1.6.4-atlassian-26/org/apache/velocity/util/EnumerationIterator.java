/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.util;

import java.util.Enumeration;
import java.util.Iterator;

public class EnumerationIterator
implements Iterator {
    private Enumeration enumeration = null;

    public EnumerationIterator(Enumeration enumeration) {
        this.enumeration = enumeration;
    }

    public Object next() {
        return this.enumeration.nextElement();
    }

    @Override
    public boolean hasNext() {
        return this.enumeration.hasMoreElements();
    }

    @Override
    public void remove() {
    }
}

