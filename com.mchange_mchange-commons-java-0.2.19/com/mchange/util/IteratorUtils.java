/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util;

import java.util.Iterator;

public class IteratorUtils {
    public static Iterator unmodifiableIterator(final Iterator iterator) {
        return new Iterator(){

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            public Object next() {
                return iterator.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("This Iterator does not support the remove operation.");
            }
        };
    }
}

