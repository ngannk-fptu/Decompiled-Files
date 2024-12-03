/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.CoalescedDeletes;
import com.atlassian.lucene36.index.FrozenBufferedDeletes;
import com.atlassian.lucene36.index.IndexWriter;
import com.atlassian.lucene36.index.SegmentInfo;
import com.atlassian.lucene36.index.SegmentInfos;
import com.atlassian.lucene36.index.SegmentReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermDocs;
import com.atlassian.lucene36.search.DocIdSet;
import com.atlassian.lucene36.search.DocIdSetIterator;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.QueryWrapperFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class BufferedDeletesStream {
    private final List<FrozenBufferedDeletes> deletes = new ArrayList<FrozenBufferedDeletes>();
    private long nextGen = 1L;
    private Term lastDeleteTerm;
    private PrintStream infoStream;
    private final AtomicLong bytesUsed = new AtomicLong();
    private final AtomicInteger numTerms = new AtomicInteger();
    private final int messageID;
    private static final Comparator<SegmentInfo> sortByDelGen = new Comparator<SegmentInfo>(){

        @Override
        public int compare(SegmentInfo si1, SegmentInfo si2) {
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

    public BufferedDeletesStream(int messageID) {
        this.messageID = messageID;
    }

    private synchronized void message(String message) {
        if (this.infoStream != null) {
            this.infoStream.println("BD " + this.messageID + " [" + new Date() + "; " + Thread.currentThread().getName() + "]: " + message);
        }
    }

    public synchronized void setInfoStream(PrintStream infoStream) {
        this.infoStream = infoStream;
    }

    public synchronized void push(FrozenBufferedDeletes packet) {
        assert (packet.any());
        assert (this.checkDeleteStats());
        assert (packet.gen < this.nextGen);
        this.deletes.add(packet);
        this.numTerms.addAndGet(packet.numTermDeletes);
        this.bytesUsed.addAndGet(packet.bytesUsed);
        if (this.infoStream != null) {
            this.message("push deletes " + packet + " delGen=" + packet.gen + " packetCount=" + this.deletes.size());
        }
        assert (this.checkDeleteStats());
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
    public synchronized ApplyDeletesResult applyDeletes(IndexWriter.ReaderPool readerPool, List<SegmentInfo> infos) throws IOException {
        long t0 = System.currentTimeMillis();
        if (infos.size() == 0) {
            return new ApplyDeletesResult(false, this.nextGen++, null);
        }
        assert (this.checkDeleteStats());
        if (!this.any()) {
            this.message("applyDeletes: no deletes; skipping");
            return new ApplyDeletesResult(false, this.nextGen++, null);
        }
        if (this.infoStream != null) {
            this.message("applyDeletes: infos=" + infos + " packetCount=" + this.deletes.size());
        }
        ArrayList<SegmentInfo> infos2 = new ArrayList<SegmentInfo>();
        infos2.addAll(infos);
        Collections.sort(infos2, sortByDelGen);
        CoalescedDeletes coalescedDeletes = null;
        boolean anyNewDeletes = false;
        int infosIDX = infos2.size() - 1;
        int delIDX = this.deletes.size() - 1;
        ArrayList<SegmentInfo> allDeleted = null;
        while (infosIDX >= 0) {
            boolean segAllDeletes;
            int delCount;
            SegmentReader reader;
            FrozenBufferedDeletes packet = delIDX >= 0 ? this.deletes.get(delIDX) : null;
            SegmentInfo info = (SegmentInfo)infos2.get(infosIDX);
            long segGen = info.getBufferedDeletesGen();
            if (packet != null && segGen < packet.gen) {
                if (coalescedDeletes == null) {
                    coalescedDeletes = new CoalescedDeletes();
                }
                coalescedDeletes.update(packet);
                --delIDX;
                continue;
            }
            if (packet != null && segGen == packet.gen) {
                Object var19_16;
                assert (readerPool.infoIsLive(info));
                reader = readerPool.get(info, false);
                delCount = 0;
                try {
                    if (coalescedDeletes != null) {
                        delCount = (int)((long)delCount + this.applyTermDeletes(coalescedDeletes.termsIterable(), reader));
                        delCount = (int)((long)delCount + this.applyQueryDeletes(coalescedDeletes.queriesIterable(), reader));
                    }
                    delCount = (int)((long)delCount + this.applyQueryDeletes(packet.queriesIterable(), reader));
                    segAllDeletes = reader.numDocs() == 0;
                    var19_16 = null;
                }
                catch (Throwable throwable) {
                    var19_16 = null;
                    readerPool.release(reader);
                    throw throwable;
                }
                readerPool.release(reader);
                anyNewDeletes |= delCount > 0;
                if (segAllDeletes) {
                    if (allDeleted == null) {
                        allDeleted = new ArrayList<SegmentInfo>();
                    }
                    allDeleted.add(info);
                }
                if (this.infoStream != null) {
                    this.message("seg=" + info + " segGen=" + segGen + " segDeletes=[" + packet + "]; coalesced deletes=[" + (coalescedDeletes == null ? "null" : coalescedDeletes) + "] delCount=" + delCount + (segAllDeletes ? " 100% deleted" : ""));
                }
                if (coalescedDeletes == null) {
                    coalescedDeletes = new CoalescedDeletes();
                }
                coalescedDeletes.update(packet);
                --delIDX;
                --infosIDX;
                info.setBufferedDeletesGen(this.nextGen);
                continue;
            }
            if (coalescedDeletes != null) {
                Object var21_17;
                assert (readerPool.infoIsLive(info));
                reader = readerPool.get(info, false);
                delCount = 0;
                try {
                    delCount = (int)((long)delCount + this.applyTermDeletes(coalescedDeletes.termsIterable(), reader));
                    delCount = (int)((long)delCount + this.applyQueryDeletes(coalescedDeletes.queriesIterable(), reader));
                    segAllDeletes = reader.numDocs() == 0;
                    var21_17 = null;
                }
                catch (Throwable throwable) {
                    var21_17 = null;
                    readerPool.release(reader);
                    throw throwable;
                }
                readerPool.release(reader);
                anyNewDeletes |= delCount > 0;
                if (segAllDeletes) {
                    if (allDeleted == null) {
                        allDeleted = new ArrayList();
                    }
                    allDeleted.add(info);
                }
                if (this.infoStream != null) {
                    this.message("seg=" + info + " segGen=" + segGen + " coalesced deletes=[" + (coalescedDeletes == null ? "null" : coalescedDeletes) + "] delCount=" + delCount + (segAllDeletes ? " 100% deleted" : ""));
                }
            }
            info.setBufferedDeletesGen(this.nextGen);
            --infosIDX;
        }
        assert (this.checkDeleteStats());
        if (this.infoStream != null) {
            this.message("applyDeletes took " + (System.currentTimeMillis() - t0) + " msec");
        }
        return new ApplyDeletesResult(anyNewDeletes, this.nextGen++, allDeleted);
    }

    public synchronized long getNextGen() {
        return this.nextGen++;
    }

    public synchronized void prune(SegmentInfos segmentInfos) {
        assert (this.checkDeleteStats());
        long minGen = Long.MAX_VALUE;
        for (SegmentInfo info : segmentInfos) {
            minGen = Math.min(info.getBufferedDeletesGen(), minGen);
        }
        if (this.infoStream != null) {
            this.message("prune sis=" + segmentInfos + " minGen=" + minGen + " packetCount=" + this.deletes.size());
        }
        int limit = this.deletes.size();
        for (int delIDX = 0; delIDX < limit; ++delIDX) {
            if (this.deletes.get((int)delIDX).gen < minGen) continue;
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
            if (this.infoStream != null) {
                this.message("pruneDeletes: prune " + count + " packets; " + (this.deletes.size() - count) + " packets remain");
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

    private synchronized long applyTermDeletes(Iterable<Term> termsIter, SegmentReader reader) throws IOException {
        long delCount = 0L;
        assert (this.checkDeleteTerm(null));
        TermDocs docs = reader.termDocs();
        for (Term term : termsIter) {
            assert (this.checkDeleteTerm(term));
            docs.seek(term);
            while (docs.next()) {
                int docID = docs.doc();
                reader.deleteDocument(docID);
                ++delCount;
            }
        }
        return delCount;
    }

    private synchronized long applyQueryDeletes(Iterable<QueryAndLimit> queriesIter, SegmentReader reader) throws IOException {
        long delCount = 0L;
        for (QueryAndLimit ent : queriesIter) {
            int doc;
            DocIdSetIterator it;
            Query query = ent.query;
            int limit = ent.limit;
            DocIdSet docs = new QueryWrapperFilter(query).getDocIdSet(reader);
            if (docs == null || (it = docs.iterator()) == null) continue;
            while ((doc = it.nextDoc()) < limit) {
                reader.deleteDocument(doc);
                ++delCount;
            }
        }
        return delCount;
    }

    private boolean checkDeleteTerm(Term term) {
        if (term != null) assert (this.lastDeleteTerm == null || term.compareTo(this.lastDeleteTerm) > 0) : "lastTerm=" + this.lastDeleteTerm + " vs term=" + term;
        this.lastDeleteTerm = term == null ? null : new Term(term.field(), term.text());
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

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ApplyDeletesResult {
        public final boolean anyDeletes;
        public final long gen;
        public final List<SegmentInfo> allDeleted;

        ApplyDeletesResult(boolean anyDeletes, long gen, List<SegmentInfo> allDeleted) {
            this.anyDeletes = anyDeletes;
            this.gen = gen;
            this.allDeleted = allDeleted;
        }
    }
}

