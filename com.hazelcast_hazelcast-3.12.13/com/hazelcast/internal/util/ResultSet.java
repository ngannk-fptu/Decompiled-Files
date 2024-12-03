/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util;

import com.hazelcast.util.IterationType;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ResultSet
extends AbstractSet<Map.Entry> {
    private final List<Map.Entry> entries;
    private final IterationType iterationType;

    public ResultSet(List<Map.Entry> entries, IterationType iterationType) {
        this.entries = entries;
        this.iterationType = iterationType;
    }

    public ResultSet() {
        this(null, null);
    }

    @Override
    public Iterator iterator() {
        if (this.entries == null) {
            return Collections.EMPTY_LIST.iterator();
        }
        return new ResultIterator();
    }

    @Override
    public int size() {
        if (this.entries == null) {
            return 0;
        }
        return this.entries.size();
    }

    private class ResultIterator
    implements Iterator {
        private final Iterator<Map.Entry> iterator;

        private ResultIterator() {
            this.iterator = ResultSet.this.entries.iterator();
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        public Object next() {
            Map.Entry entry = this.iterator.next();
            switch (ResultSet.this.iterationType) {
                case KEY: {
                    return entry.getKey();
                }
                case VALUE: {
                    return entry.getValue();
                }
                case ENTRY: {
                    return entry;
                }
            }
            throw new IllegalStateException("Unrecognized iterationType:" + (Object)((Object)ResultSet.this.iterationType));
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

