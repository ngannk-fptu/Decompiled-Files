/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.index.IndexReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ReaderUtil {
    private ReaderUtil() {
    }

    public static void gatherSubReaders(List<IndexReader> allSubReaders, IndexReader reader) {
        IndexReader[] subReaders = reader.getSequentialSubReaders();
        if (subReaders == null) {
            allSubReaders.add(reader);
        } else {
            for (int i = 0; i < subReaders.length; ++i) {
                ReaderUtil.gatherSubReaders(allSubReaders, subReaders[i]);
            }
        }
    }

    public static int subIndex(int n, int[] docStarts) {
        int size = docStarts.length;
        int lo = 0;
        int hi = size - 1;
        while (hi >= lo) {
            int mid = lo + hi >>> 1;
            int midValue = docStarts[mid];
            if (n < midValue) {
                hi = mid - 1;
                continue;
            }
            if (n > midValue) {
                lo = mid + 1;
                continue;
            }
            while (mid + 1 < size && docStarts[mid + 1] == midValue) {
                ++mid;
            }
            return mid;
        }
        return hi;
    }

    public static Collection<String> getIndexedFields(IndexReader reader) {
        HashSet<String> fields = new HashSet<String>();
        for (FieldInfo fieldInfo : ReaderUtil.getMergedFieldInfos(reader)) {
            if (!fieldInfo.isIndexed) continue;
            fields.add(fieldInfo.name);
        }
        return fields;
    }

    public static FieldInfos getMergedFieldInfos(IndexReader reader) {
        ArrayList<IndexReader> subReaders = new ArrayList<IndexReader>();
        ReaderUtil.gatherSubReaders(subReaders, reader);
        FieldInfos fieldInfos = new FieldInfos();
        for (IndexReader subReader : subReaders) {
            fieldInfos.add(subReader.getFieldInfos());
        }
        return fieldInfos;
    }

    public static abstract class Gather {
        private final IndexReader topReader;

        public Gather(IndexReader r) {
            this.topReader = r;
        }

        public int run() throws IOException {
            return this.run(0, this.topReader);
        }

        public int run(int docBase) throws IOException {
            return this.run(docBase, this.topReader);
        }

        private int run(int base, IndexReader reader) throws IOException {
            IndexReader[] subReaders = reader.getSequentialSubReaders();
            if (subReaders == null) {
                this.add(base, reader);
                return base + reader.maxDoc();
            }
            int newBase = base;
            for (int i = 0; i < subReaders.length; ++i) {
                newBase = this.run(newBase, subReaders[i]);
            }
            assert (newBase == base + reader.maxDoc());
            return newBase;
        }

        protected abstract void add(int var1, IndexReader var2) throws IOException;
    }
}

