/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.store;

import java.io.IOException;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.terracotta.modules.ehcache.store.ValueModeHandler;

class RealObjectKeySet
extends AbstractSet {
    private final ValueModeHandler mode;
    private final Collection keys;

    public RealObjectKeySet(ValueModeHandler mode, Collection keys) {
        this.mode = mode;
        this.keys = keys;
    }

    @Override
    public int size() {
        return this.keys.size();
    }

    @Override
    public boolean contains(Object o) {
        try {
            return this.keys.contains(this.mode.createPortableKey(o));
        }
        catch (IOException e) {
            return false;
        }
    }

    @Override
    public Iterator iterator() {
        return new KeyIterator(this.mode, this.keys.iterator());
    }

    static class KeyIterator
    implements Iterator {
        private static final Object NO_OBJECT = new Object();
        private final Iterator keysIterator;
        private final ValueModeHandler mode;
        private Object next;

        private KeyIterator(ValueModeHandler mode, Iterator iterator) {
            this.mode = mode;
            this.keysIterator = iterator;
            this.advance();
        }

        private void advance() {
            Object real;
            this.next = this.keysIterator.hasNext() ? (real = this.mode.getRealKeyObject((String)this.keysIterator.next())) : NO_OBJECT;
        }

        @Override
        public boolean hasNext() {
            return this.next != NO_OBJECT;
        }

        public Object next() {
            Object rv = this.next;
            if (rv == NO_OBJECT) {
                throw new NoSuchElementException();
            }
            this.advance();
            return rv;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

