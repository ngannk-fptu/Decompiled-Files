/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.util;

import java.util.Enumeration;
import java.util.Iterator;
import org.apache.struts2.util.MakeIterator;

public abstract class IteratorFilterSupport {
    protected Object getIterator(Object source) {
        return MakeIterator.convert(source);
    }

    public static class EnumerationIterator
    implements Iterator {
        Enumeration enumeration;

        public EnumerationIterator(Enumeration aEnum) {
            this.enumeration = aEnum;
        }

        @Override
        public boolean hasNext() {
            return this.enumeration.hasMoreElements();
        }

        public Object next() {
            return this.enumeration.nextElement();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove is not supported in IteratorFilterSupport.");
        }
    }
}

