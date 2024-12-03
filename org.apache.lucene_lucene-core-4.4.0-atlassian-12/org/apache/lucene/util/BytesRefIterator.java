/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import java.io.IOException;
import java.util.Comparator;
import org.apache.lucene.util.BytesRef;

public interface BytesRefIterator {
    public static final BytesRefIterator EMPTY = new BytesRefIterator(){

        @Override
        public BytesRef next() {
            return null;
        }

        @Override
        public Comparator<BytesRef> getComparator() {
            return null;
        }
    };

    public BytesRef next() throws IOException;

    public Comparator<BytesRef> getComparator();
}

