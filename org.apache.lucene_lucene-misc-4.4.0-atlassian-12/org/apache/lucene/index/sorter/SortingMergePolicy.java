/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReader
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.IndexWriter
 *  org.apache.lucene.index.MergePolicy
 *  org.apache.lucene.index.MergePolicy$DocMap
 *  org.apache.lucene.index.MergePolicy$MergeSpecification
 *  org.apache.lucene.index.MergePolicy$MergeTrigger
 *  org.apache.lucene.index.MergePolicy$OneMerge
 *  org.apache.lucene.index.MergeState
 *  org.apache.lucene.index.MultiReader
 *  org.apache.lucene.index.SegmentInfoPerCommit
 *  org.apache.lucene.index.SegmentInfos
 *  org.apache.lucene.index.SegmentReader
 *  org.apache.lucene.index.SlowCompositeReaderWrapper
 *  org.apache.lucene.store.Directory
 *  org.apache.lucene.util.Bits
 *  org.apache.lucene.util.packed.MonotonicAppendingLongBuffer
 */
package org.apache.lucene.index.sorter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.MergeState;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.SegmentInfoPerCommit;
import org.apache.lucene.index.SegmentInfos;
import org.apache.lucene.index.SegmentReader;
import org.apache.lucene.index.SlowCompositeReaderWrapper;
import org.apache.lucene.index.sorter.Sorter;
import org.apache.lucene.index.sorter.SortingAtomicReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.packed.MonotonicAppendingLongBuffer;

public final class SortingMergePolicy
extends MergePolicy {
    public static final String SORTER_ID_PROP = "sorter";
    final MergePolicy in;
    final Sorter sorter;

    public static boolean isSorted(AtomicReader reader, Sorter sorter) {
        if (reader instanceof SegmentReader) {
            SegmentReader segReader = (SegmentReader)reader;
            Map diagnostics = segReader.getSegmentInfo().info.getDiagnostics();
            if (diagnostics != null && sorter.getID().equals(diagnostics.get(SORTER_ID_PROP))) {
                return true;
            }
        }
        return false;
    }

    private MergePolicy.MergeSpecification sortedMergeSpecification(MergePolicy.MergeSpecification specification) {
        if (specification == null) {
            return null;
        }
        SortingMergeSpecification sortingSpec = new SortingMergeSpecification();
        for (MergePolicy.OneMerge merge : specification.merges) {
            sortingSpec.add(merge);
        }
        return sortingSpec;
    }

    public SortingMergePolicy(MergePolicy in, Sorter sorter) {
        this.in = in;
        this.sorter = sorter;
    }

    public MergePolicy.MergeSpecification findMerges(MergePolicy.MergeTrigger mergeTrigger, SegmentInfos segmentInfos) throws IOException {
        return this.sortedMergeSpecification(this.in.findMerges(mergeTrigger, segmentInfos));
    }

    public MergePolicy.MergeSpecification findForcedMerges(SegmentInfos segmentInfos, int maxSegmentCount, Map<SegmentInfoPerCommit, Boolean> segmentsToMerge) throws IOException {
        return this.sortedMergeSpecification(this.in.findForcedMerges(segmentInfos, maxSegmentCount, segmentsToMerge));
    }

    public MergePolicy.MergeSpecification findForcedDeletesMerges(SegmentInfos segmentInfos) throws IOException {
        return this.sortedMergeSpecification(this.in.findForcedDeletesMerges(segmentInfos));
    }

    public MergePolicy clone() {
        return new SortingMergePolicy(this.in.clone(), this.sorter);
    }

    public void close() {
        this.in.close();
    }

    public boolean useCompoundFile(SegmentInfos segments, SegmentInfoPerCommit newSegment) throws IOException {
        return this.in.useCompoundFile(segments, newSegment);
    }

    public void setIndexWriter(IndexWriter writer) {
        this.in.setIndexWriter(writer);
    }

    public String toString() {
        return "SortingMergePolicy(" + this.in + ", sorter=" + this.sorter + ")";
    }

    class SortingMergeSpecification
    extends MergePolicy.MergeSpecification {
        SortingMergeSpecification() {
        }

        public void add(MergePolicy.OneMerge merge) {
            super.add((MergePolicy.OneMerge)new SortingOneMerge(merge.segments));
        }

        public String segString(Directory dir) {
            return "SortingMergeSpec(" + super.segString(dir) + ", sorter=" + SortingMergePolicy.this.sorter + ")";
        }
    }

    class SortingOneMerge
    extends MergePolicy.OneMerge {
        List<AtomicReader> unsortedReaders;
        Sorter.DocMap docMap;
        AtomicReader sortedView;

        SortingOneMerge(List<SegmentInfoPerCommit> segments) {
            super(segments);
        }

        public List<AtomicReader> getMergeReaders() throws IOException {
            if (this.unsortedReaders == null) {
                AtomicReader atomicView;
                this.unsortedReaders = super.getMergeReaders();
                if (this.unsortedReaders.size() == 1) {
                    atomicView = this.unsortedReaders.get(0);
                } else {
                    MultiReader multiReader = new MultiReader((IndexReader[])this.unsortedReaders.toArray(new AtomicReader[this.unsortedReaders.size()]));
                    atomicView = SlowCompositeReaderWrapper.wrap((IndexReader)multiReader);
                }
                this.docMap = SortingMergePolicy.this.sorter.sort(atomicView);
                this.sortedView = SortingAtomicReader.wrap(atomicView, this.docMap);
            }
            return this.docMap == null ? this.unsortedReaders : Collections.singletonList(this.sortedView);
        }

        public void setInfo(SegmentInfoPerCommit info) {
            Map diagnostics = info.info.getDiagnostics();
            diagnostics.put(SortingMergePolicy.SORTER_ID_PROP, SortingMergePolicy.this.sorter.getID());
            super.setInfo(info);
        }

        private MonotonicAppendingLongBuffer getDeletes(List<AtomicReader> readers) {
            MonotonicAppendingLongBuffer deletes = new MonotonicAppendingLongBuffer();
            int deleteCount = 0;
            for (AtomicReader reader : readers) {
                int maxDoc = reader.maxDoc();
                Bits liveDocs = reader.getLiveDocs();
                for (int i = 0; i < maxDoc; ++i) {
                    if (liveDocs != null && !liveDocs.get(i)) {
                        ++deleteCount;
                        continue;
                    }
                    deletes.add((long)deleteCount);
                }
            }
            return deletes;
        }

        public MergePolicy.DocMap getDocMap(final MergeState mergeState) {
            if (this.unsortedReaders == null) {
                throw new IllegalStateException();
            }
            if (this.docMap == null) {
                return super.getDocMap(mergeState);
            }
            assert (mergeState.docMaps.length == 1);
            final MonotonicAppendingLongBuffer deletes = this.getDeletes(this.unsortedReaders);
            return new MergePolicy.DocMap(){

                public int map(int old) {
                    int oldWithDeletes = old + (int)deletes.get((long)old);
                    int newWithDeletes = SortingOneMerge.this.docMap.oldToNew(oldWithDeletes);
                    return mergeState.docMaps[0].get(newWithDeletes);
                }
            };
        }
    }
}

