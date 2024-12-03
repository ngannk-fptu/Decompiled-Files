/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import com.atlassian.lucene36.util.BytesRef;
import java.io.IOException;
import java.util.Comparator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface BytesRefIterator {
    public static final BytesRefIterator EMPTY = new BytesRefIterator(){

        @Override
        public BytesRef next() throws IOException {
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

