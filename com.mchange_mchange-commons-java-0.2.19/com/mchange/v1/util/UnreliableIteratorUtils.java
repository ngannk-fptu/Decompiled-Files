/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.util;

import com.mchange.v1.util.UnreliableIterator;
import com.mchange.v1.util.UnreliableIteratorException;
import java.util.Collection;
import java.util.Iterator;

public class UnreliableIteratorUtils {
    public static void addToCollection(Collection collection, UnreliableIterator unreliableIterator) throws UnreliableIteratorException {
        while (unreliableIterator.hasNext()) {
            collection.add(unreliableIterator.next());
        }
    }

    public static UnreliableIterator unreliableIteratorFromIterator(final Iterator iterator) {
        return new UnreliableIterator(){

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

