/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReader
 *  org.apache.lucene.util.TimSorter
 *  org.apache.lucene.util.packed.MonotonicAppendingLongBuffer
 */
package org.apache.lucene.index.sorter;

import java.io.IOException;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.util.TimSorter;
import org.apache.lucene.util.packed.MonotonicAppendingLongBuffer;

public abstract class Sorter {
    public static final Sorter REVERSE_DOCS = new Sorter(){

        @Override
        public DocMap sort(AtomicReader reader) throws IOException {
            final int maxDoc = reader.maxDoc();
            return new DocMap(){

                @Override
                public int oldToNew(int docID) {
                    return maxDoc - docID - 1;
                }

                @Override
                public int newToOld(int docID) {
                    return maxDoc - docID - 1;
                }

                @Override
                public int size() {
                    return maxDoc;
                }
            };
        }

        @Override
        public String getID() {
            return "ReverseDocs";
        }
    };

    static boolean isConsistent(DocMap docMap) {
        int maxDoc = docMap.size();
        for (int i = 0; i < maxDoc; ++i) {
            int newID = docMap.oldToNew(i);
            int oldID = docMap.newToOld(newID);
            assert (newID >= 0 && newID < maxDoc) : "doc IDs must be in [0-" + maxDoc + "[, got " + newID;
            assert (i == oldID) : "mapping is inconsistent: " + i + " --oldToNew--> " + newID + " --newToOld--> " + oldID;
            if (i == oldID && newID >= 0 && newID < maxDoc) continue;
            return false;
        }
        return true;
    }

    protected static DocMap sort(final int maxDoc, DocComparator comparator) {
        int i;
        boolean sorted = true;
        for (int i2 = 1; i2 < maxDoc; ++i2) {
            if (comparator.compare(i2 - 1, i2) <= 0) continue;
            sorted = false;
            break;
        }
        if (sorted) {
            return null;
        }
        int[] docs = new int[maxDoc];
        for (int i3 = 0; i3 < maxDoc; ++i3) {
            docs[i3] = i3;
        }
        DocValueSorter sorter = new DocValueSorter(docs, comparator);
        sorter.sort(0, docs.length);
        final MonotonicAppendingLongBuffer newToOld = new MonotonicAppendingLongBuffer();
        for (i = 0; i < maxDoc; ++i) {
            newToOld.add((long)docs[i]);
        }
        for (i = 0; i < maxDoc; ++i) {
            docs[(int)newToOld.get((long)((long)i))] = i;
        }
        final MonotonicAppendingLongBuffer oldToNew = new MonotonicAppendingLongBuffer();
        for (int i4 = 0; i4 < maxDoc; ++i4) {
            oldToNew.add((long)docs[i4]);
        }
        return new DocMap(){

            @Override
            public int oldToNew(int docID) {
                return (int)oldToNew.get((long)docID);
            }

            @Override
            public int newToOld(int docID) {
                return (int)newToOld.get((long)docID);
            }

            @Override
            public int size() {
                return maxDoc;
            }
        };
    }

    public abstract DocMap sort(AtomicReader var1) throws IOException;

    public abstract String getID();

    public String toString() {
        return this.getID();
    }

    private static final class DocValueSorter
    extends TimSorter {
        private final int[] docs;
        private final DocComparator comparator;
        private final int[] tmp;

        public DocValueSorter(int[] docs, DocComparator comparator) {
            super(docs.length / 64);
            this.docs = docs;
            this.comparator = comparator;
            this.tmp = new int[docs.length / 64];
        }

        protected int compare(int i, int j) {
            return this.comparator.compare(this.docs[i], this.docs[j]);
        }

        protected void swap(int i, int j) {
            int tmpDoc = this.docs[i];
            this.docs[i] = this.docs[j];
            this.docs[j] = tmpDoc;
        }

        protected void copy(int src, int dest) {
            this.docs[dest] = this.docs[src];
        }

        protected void save(int i, int len) {
            System.arraycopy(this.docs, i, this.tmp, 0, len);
        }

        protected void restore(int i, int j) {
            this.docs[j] = this.tmp[i];
        }

        protected int compareSaved(int i, int j) {
            return this.comparator.compare(this.tmp[i], this.docs[j]);
        }
    }

    public static abstract class DocComparator {
        public abstract int compare(int var1, int var2);
    }

    public static abstract class DocMap {
        public abstract int oldToNew(int var1);

        public abstract int newToOld(int var1);

        public abstract int size();
    }
}

