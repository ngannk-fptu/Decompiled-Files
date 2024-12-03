/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.util;

import com.mchange.v1.util.UIterator;
import java.util.Collection;
import java.util.Iterator;

public class UIteratorUtils {
    public static void addToCollection(Collection collection, UIterator uIterator) throws Exception {
        while (uIterator.hasNext()) {
            collection.add(uIterator.next());
        }
    }

    public static UIterator uiteratorFromIterator(final Iterator iterator) {
        return new UIterator(){

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Object next() {
                return iterator.next();
            }

            @Override
            public void remove() {
                iterator.remove();
            }

            @Override
            public void close() {
            }
        };
    }
}

