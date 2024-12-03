/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.CoalescedDeletes;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.FrozenBufferedDeletes;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.ReadersAndLiveDocs;
import org.apache.lucene.index.SegmentInfoPerCommit;
import org.apache.lucene.index.SegmentInfos;
import org.apache.lucene.index.SegmentReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.InfoStream;

class BufferedDeletesStream {
    private final List<FrozenBufferedDeletes> deletes = new ArrayList<FrozenBufferedDeletes>();
    private long nextGen = 1L;
    private Term lastDeleteTerm;
    private final InfoStream infoStream;
    private final AtomicLong bytesUsed = new AtomicLong();
    private final AtomicInteger numTerms = new AtomicInteger();
    private static final Comparator<SegmentInfoPerCommit> sortSegInfoByDelGen = new Comparator<SegmentInfoPerCommit>(){

        @Override
        public int compare(SegmentInfoPerCommit si1, SegmentInfoPerCommit si2) {
            long cmp = si1.getBufferedDeletesGen() - si2.getBufferedDeletesGen();
            if (cmp > 0L) {
                return 1;
            }
            if (cmp < 0L) {
                return -1;
            }
            return 0;
        }
    };

    public BufferedDeletesStream(InfoStream infoStream) {
        this.infoStream = infoStream;
    }

    public synchronized long push(FrozenBufferedDeletes packet) {
        packet.setDelGen(this.nextGen++);
        assert (packet.any());
        assert (this.checkDeleteStats());
        assert (packet.delGen() < this.nextGen);
        assert (this.deletes.isEmpty() || this.deletes.get(this.deletes.size() - 1).delGen() < packet.delGen()) : "Delete packets must be in order";
        this.deletes.add(packet);
        this.numTerms.addAndGet(packet.numTermDeletes);
        this.bytesUsed.addAndGet(packet.bytesUsed);
        if (this.infoStream.isEnabled("BD")) {
            this.infoStream.message("BD", "push deletes " + packet + " delGen=" + packet.delGen() + " packetCount=" + this.deletes.size() + " totBytesUsed=" + this.bytesUsed.get());
        }
        assert (this.checkDeleteStats());
        return packet.delGen();
    }

    public synchronized void clear() {
        this.deletes.clear();
        this.nextGen = 1L;
        this.numTerms.set(0);
        this.bytesUsed.set(0L);
    }

    public boolean any() {
        return this.bytesUsed.get() != 0L;
    }

    public int numTerms() {
        return this.numTerms.get();
    }

    public long bytesUsed() {
        return this.bytesUsed.get();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized ApplyDeletesResult applyDeletes(IndexWriter.ReaderPool readerPool, List<SegmentInfoPerCommit> infos) throws IOException {
        long t0 = System.currentTimeMillis();
        if (infos.size() == 0) {
            return new ApplyDeletesResult(false, this.nextGen++, null);
        }
        assert (this.checkDeleteStats());
        if (!this.any()) {
            if (this.infoStream.isEnabled("BD")) {
                this.infoStream.message("BD", "applyDeletes: no deletes; skipping");
            }
            return new ApplyDeletesResult(false, this.nextGen++, null);
        }
        if (this.infoStream.isEnabled("BD")) {
            this.infoStream.message("BD", "applyDeletes: infos=" + infos + " packetCount=" + this.deletes.size());
        }
        long gen = this.nextGen++;
        ArrayList<SegmentInfoPerCommit> infos2 = new ArrayList<SegmentInfoPerCommit>();
        infos2.addAll(infos);
        Collections.sort(infos2, sortSegInfoByDelGen);
        CoalescedDeletes coalescedDeletes = null;
        boolean anyNewDeletes = false;
        int infosIDX = infos2.size() - 1;
        int delIDX = this.deletes.size() - 1;
        ArrayList<SegmentInfoPerCommit> allDeleted = null;
        while (infosIDX >= 0) {
            boolean segAllDeletes;
            int fullDelCount;
            int delCount;
            SegmentReader reader;
            ReadersAndLiveDocs rld;
            FrozenBufferedDeletes packet = delIDX >= 0 ? this.deletes.get(delIDX) : null;
            SegmentInfoPerCommit info = (SegmentInfoPerCommit)infos2.get(infosIDX);
            long segGen = info.getBufferedDeletesGen();
            if (packet != null && segGen < packet.delGen()) {
                if (coalescedDeletes == null) {
                    coalescedDeletes = new CoalescedDeletes();
                }
                if (!packet.isSegmentPrivate) {
                    coalescedDeletes.update(packet);
                }
                --delIDX;
                continue;
            }
            if (packet != null && segGen == packet.delGen()) {
                assert (packet.isSegmentPrivate) : "Packet and Segments deletegen can only match on a segment private del packet gen=" + segGen;
                assert (readerPool.infoIsLive(info));
                rld = readerPool.get(info, true);
                reader = rld.getReader(IOContext.READ);
                delCount = 0;
                try {
                    if (coalescedDeletes != null) {
                        delCount = (int)((long)delCount + this.applyTermDeletes(coalescedDeletes.termsIterable(), rld, reader));
                        delCount = (int)((long)delCount + BufferedDeletesStream.applyQueryDeletes(coalescedDeletes.queriesIterable(), rld, reader));
                    }
                    delCount = (int)((long)delCount + BufferedDeletesStream.applyQueryDeletes(packet.queriesIterable(), rld, reader));
                    fullDelCount = rld.info.getDelCount() + rld.getPendingDeleteCount();
                    assert (fullDelCount <= rld.info.info.getDocCount());
                    segAllDeletes = fullDelCount == rld.info.info.getDocCount();
                }
                finally {
                    rld.release(reader);
                    readerPool.release(rld);
                }
                anyNewDeletes |= delCount > 0;
                if (segAllDeletes) {
                    if (allDeleted == null) {
                        allDeleted = new ArrayList<SegmentInfoPerCommit>();
                    }
                    allDeleted.add(info);
                }
                if (this.infoStream.isEnabled("BD")) {
                    this.infoStream.message("BD", "seg=" + info + " segGen=" + segGen + " segDeletes=[" + packet + "]; coalesced deletes=[" + (coalescedDeletes == null ? "null" : coalescedDeletes) + "] newDelCount=" + delCount + (segAllDeletes ? " 100% deleted" : ""));
                }
                if (coalescedDeletes == null) {
                    coalescedDeletes = new CoalescedDeletes();
                }
                --delIDX;
                --infosIDX;
                info.setBufferedDeletesGen(gen);
                continue;
            }
            if (coalescedDeletes != null) {
                assert (readerPool.infoIsLive(info));
                rld = readerPool.get(info, true);
                reader = rld.getReader(IOContext.READ);
                delCount = 0;
                try {
                    delCount = (int)((long)delCount + this.applyTermDeletes(coalescedDeletes.termsIterable(), rld, reader));
                    delCount = (int)((long)delCount + BufferedDeletesStream.applyQueryDeletes(coalescedDeletes.queriesIterable(), rld, reader));
                    fullDelCount = rld.info.getDelCount() + rld.getPendingDeleteCount();
                    assert (fullDelCount <= rld.info.info.getDocCount());
                    segAllDeletes = fullDelCount == rld.info.info.getDocCount();
                }
                finally {
                    rld.release(reader);
                    readerPool.release(rld);
                }
                anyNewDeletes |= delCount > 0;
                if (segAllDeletes) {
                    if (allDeleted == null) {
                        allDeleted = new ArrayList();
                    }
                    allDeleted.add(info);
                }
                if (this.infoStream.isEnabled("BD")) {
                    this.infoStream.message("BD", "seg=" + info + " segGen=" + segGen + " coalesced deletes=[" + (coalescedDeletes == null ? "null" : coalescedDeletes) + "] newDelCount=" + delCount + (segAllDeletes ? " 100% deleted" : ""));
                }
            }
            info.setBufferedDeletesGen(gen);
            --infosIDX;
        }
        assert (this.checkDeleteStats());
        if (this.infoStream.isEnabled("BD")) {
            this.infoStream.message("BD", "applyDeletes took " + (System.currentTimeMillis() - t0) + " msec");
        }
        return new ApplyDeletesResult(anyNewDeletes, gen, allDeleted);
    }

    synchronized long getNextGen() {
        return this.nextGen++;
    }

    public synchronized void prune(SegmentInfos segmentInfos) {
        assert (this.checkDeleteStats());
        long minGen = Long.MAX_VALUE;
        for (SegmentInfoPerCommit info : segmentInfos) {
            minGen = Math.min(info.getBufferedDeletesGen(), minGen);
        }
        if (this.infoStream.isEnabled("BD")) {
            this.infoStream.message("BD", "prune sis=" + segmentInfos + " minGen=" + minGen + " packetCount=" + this.deletes.size());
        }
        int limit = this.deletes.size();
        for (int delIDX = 0; delIDX < limit; ++delIDX) {
            if (this.deletes.get(delIDX).delGen() < minGen) continue;
            this.prune(delIDX);
            assert (this.checkDeleteStats());
            return;
        }
        this.prune(limit);
        assert (!this.any());
        assert (this.checkDeleteStats());
    }

    private synchronized void prune(int count) {
        if (count > 0) {
            if (this.infoStream.isEnabled("BD")) {
                this.infoStream.message("BD", "pruneDeletes: prune " + count + " packets; " + (this.deletes.size() - count) + " packets remain");
            }
            for (int delIDX = 0; delIDX < count; ++delIDX) {
                FrozenBufferedDeletes packet = this.deletes.get(delIDX);
                this.numTerms.addAndGet(-packet.numTermDeletes);
                assert (this.numTerms.get() >= 0);
                this.bytesUsed.addAndGet(-packet.bytesUsed);
                assert (this.bytesUsed.get() >= 0L);
            }
            this.deletes.subList(0, count).clear();
        }
    }

    private synchronized long applyTermDeletes(Iterable<Term> termsIter, ReadersAndLiveDocs rld, SegmentReader reader) throws IOException {
        long delCount = 0L;
        Fields fields = reader.fields();
        if (fields == null) {
            return 0L;
        }
        TermsEnum termsEnum = null;
        String currentField = null;
        DocsEnum docs = null;
        assert (this.checkDeleteTerm(null));
        boolean any = false;
        for (Term term : termsIter) {
            int docID;
            DocsEnum docsEnum;
            if (!term.field().equals(currentField)) {
                assert (currentField == null || currentField.compareTo(term.field()) < 0);
                currentField = term.field();
                Terms terms = fields.terms(currentField);
                termsEnum = terms != null ? terms.iterator(null) : null;
            }
            if (termsEnum == null) continue;
            assert (this.checkDeleteTerm(term));
            if (!termsEnum.seekExact(term.bytes(), false) || (docsEnum = termsEnum.docs(rld.getLiveDocs(), docs, 0)) == null) continue;
            while ((docID = docsEnum.nextDoc()) != Integer.MAX_VALUE) {
                if (!any) {
                    rld.initWritableLiveDocs();
                    any = true;
                }
                if (!rld.delete(docID)) continue;
                ++delCount;
            }
        }
        return delCount;
    }

    private static long applyQueryDeletes(Iterable<QueryAndLimit> queriesIter, ReadersAndLiveDocs rld, SegmentReader reader) throws IOException {
        long delCount = 0L;
        AtomicReaderContext readerContext = reader.getContext();
        boolean any = false;
        for (QueryAndLimit ent : queriesIter) {
            int doc;
            DocIdSetIterator it;
            Query query = ent.query;
            int limit = ent.limit;
            DocIdSet docs = new QueryWrapperFilter(query).getDocIdSet(readerContext, reader.getLiveDocs());
            if (docs == null || (it = docs.iterator()) == null) continue;
            while ((doc = it.nextDoc()) < limit) {
                if (!any) {
                    rld.initWritableLiveDocs();
                    any = true;
                }
                if (!rld.delete(doc)) continue;
                ++delCount;
            }
        }
        return delCount;
    }

    private boolean checkDeleteTerm(Term term) {
        if (term != null) assert (this.lastDeleteTerm == null || term.compareTo(this.lastDeleteTerm) > 0) : "lastTerm=" + this.lastDeleteTerm + " vs term=" + term;
        this.lastDeleteTerm = term == null ? null : new Term(term.field(), BytesRef.deepCopyOf(term.bytes));
        return true;
    }

    private boolean checkDeleteStats() {
        int numTerms2 = 0;
        long bytesUsed2 = 0L;
        for (FrozenBufferedDeletes packet : this.deletes) {
            numTerms2 += packet.numTermDeletes;
            bytesUsed2 += (long)packet.bytesUsed;
        }
        assert (numTerms2 == this.numTerms.get()) : "numTerms2=" + numTerms2 + " vs " + this.numTerms.get();
        assert (bytesUsed2 == this.bytesUsed.get()) : "bytesUsed2=" + bytesUsed2 + " vs " + this.bytesUsed;
        return true;
    }

    public static class QueryAndLimit {
        public final Query query;
        public final int limit;

        public QueryAndLimit(Query query, int limit) {
            this.query = query;
            this.limit = limit;
        }
    }

    public static class ApplyDeletesResult {
        public final boolean anyDeletes;
        public final long gen;
        public final List<SegmentInfoPerCommit> allDeleted;

        ApplyDeletesResult(boolean anyDeletes, long gen, List<SegmentInfoPerCommit> allDeleted) {
            this.anyDeletes = anyDeletes;
            this.gen = gen;
            this.allDeleted = allDeleted;
        }
    }
}

