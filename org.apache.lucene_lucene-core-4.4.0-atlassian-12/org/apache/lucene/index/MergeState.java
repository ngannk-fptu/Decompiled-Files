/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.util.List;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.index.SegmentReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.InfoStream;
import org.apache.lucene.util.packed.MonotonicAppendingLongBuffer;

public class MergeState {
    public final SegmentInfo segmentInfo;
    public FieldInfos fieldInfos;
    public final List<AtomicReader> readers;
    public DocMap[] docMaps;
    public int[] docBase;
    public final CheckAbort checkAbort;
    public final InfoStream infoStream;
    public SegmentReader[] matchingSegmentReaders;
    public int matchedCount;

    MergeState(List<AtomicReader> readers, SegmentInfo segmentInfo, InfoStream infoStream, CheckAbort checkAbort) {
        this.readers = readers;
        this.segmentInfo = segmentInfo;
        this.infoStream = infoStream;
        this.checkAbort = checkAbort;
    }

    public static class CheckAbort {
        private double workCount;
        private final MergePolicy.OneMerge merge;
        private final Directory dir;
        static final CheckAbort NONE = new CheckAbort(null, null){

            @Override
            public void work(double units) {
            }
        };

        public CheckAbort(MergePolicy.OneMerge merge, Directory dir) {
            this.merge = merge;
            this.dir = dir;
        }

        public void work(double units) throws MergePolicy.MergeAbortedException {
            this.workCount += units;
            if (this.workCount >= 10000.0) {
                this.merge.checkAborted(this.dir);
                this.workCount = 0.0;
            }
        }
    }

    private static class NoDelDocMap
    extends DocMap {
        private final int maxDoc;

        private NoDelDocMap(int maxDoc) {
            this.maxDoc = maxDoc;
        }

        @Override
        public int get(int docID) {
            return docID;
        }

        @Override
        public int maxDoc() {
            return this.maxDoc;
        }

        @Override
        public int numDeletedDocs() {
            return 0;
        }
    }

    public static abstract class DocMap {
        DocMap() {
        }

        public abstract int get(int var1);

        public abstract int maxDoc();

        public final int numDocs() {
            return this.maxDoc() - this.numDeletedDocs();
        }

        public abstract int numDeletedDocs();

        public boolean hasDeletions() {
            return this.numDeletedDocs() > 0;
        }

        public static DocMap build(AtomicReader reader) {
            int maxDoc = reader.maxDoc();
            if (!reader.hasDeletions()) {
                return new NoDelDocMap(maxDoc);
            }
            Bits liveDocs = reader.getLiveDocs();
            return DocMap.build(maxDoc, liveDocs);
        }

        static DocMap build(final int maxDoc, final Bits liveDocs) {
            assert (liveDocs != null);
            final MonotonicAppendingLongBuffer docMap = new MonotonicAppendingLongBuffer();
            int del = 0;
            for (int i = 0; i < maxDoc; ++i) {
                docMap.add(i - del);
                if (liveDocs.get(i)) continue;
                ++del;
            }
            final int numDeletedDocs = del;
            assert (docMap.size() == (long)maxDoc);
            return new DocMap(){

                @Override
                public int get(int docID) {
                    if (!liveDocs.get(docID)) {
                        return -1;
                    }
                    return (int)docMap.get(docID);
                }

                @Override
                public int maxDoc() {
                    return maxDoc;
                }

                @Override
                public int numDeletedDocs() {
                    return numDeletedDocs;
                }
            };
        }
    }
}

