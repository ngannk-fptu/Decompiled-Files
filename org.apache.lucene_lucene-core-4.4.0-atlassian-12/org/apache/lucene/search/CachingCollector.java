/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Scorer;

public abstract class CachingCollector
extends Collector {
    private static final int MAX_ARRAY_SIZE = 524288;
    private static final int INITIAL_ARRAY_SIZE = 128;
    private static final int[] EMPTY_INT_ARRAY = new int[0];
    protected final Collector other;
    protected final int maxDocsToCache;
    protected final List<SegStart> cachedSegs = new ArrayList<SegStart>();
    protected final List<int[]> cachedDocs;
    private AtomicReaderContext lastReaderContext;
    protected int[] curDocs;
    protected int upto;
    protected int base;
    protected int lastDocBase;

    public static CachingCollector create(final boolean acceptDocsOutOfOrder, boolean cacheScores, double maxRAMMB) {
        Collector other = new Collector(){

            @Override
            public boolean acceptsDocsOutOfOrder() {
                return acceptDocsOutOfOrder;
            }

            @Override
            public void setScorer(Scorer scorer) {
            }

            @Override
            public void collect(int doc) {
            }

            @Override
            public void setNextReader(AtomicReaderContext context) {
            }
        };
        return CachingCollector.create(other, cacheScores, maxRAMMB);
    }

    public static CachingCollector create(Collector other, boolean cacheScores, double maxRAMMB) {
        return cacheScores ? new ScoreCachingCollector(other, maxRAMMB) : new NoScoreCachingCollector(other, maxRAMMB);
    }

    public static CachingCollector create(Collector other, boolean cacheScores, int maxDocsToCache) {
        return cacheScores ? new ScoreCachingCollector(other, maxDocsToCache) : new NoScoreCachingCollector(other, maxDocsToCache);
    }

    private CachingCollector(Collector other, double maxRAMMB, boolean cacheScores) {
        this.other = other;
        this.cachedDocs = new ArrayList<int[]>();
        this.curDocs = new int[128];
        this.cachedDocs.add(this.curDocs);
        int bytesPerDoc = 4;
        if (cacheScores) {
            bytesPerDoc += 4;
        }
        this.maxDocsToCache = (int)(maxRAMMB * 1024.0 * 1024.0 / (double)bytesPerDoc);
    }

    private CachingCollector(Collector other, int maxDocsToCache) {
        this.other = other;
        this.cachedDocs = new ArrayList<int[]>();
        this.curDocs = new int[128];
        this.cachedDocs.add(this.curDocs);
        this.maxDocsToCache = maxDocsToCache;
    }

    @Override
    public boolean acceptsDocsOutOfOrder() {
        return this.other.acceptsDocsOutOfOrder();
    }

    public boolean isCached() {
        return this.curDocs != null;
    }

    @Override
    public void setNextReader(AtomicReaderContext context) throws IOException {
        this.other.setNextReader(context);
        if (this.lastReaderContext != null) {
            this.cachedSegs.add(new SegStart(this.lastReaderContext, this.base + this.upto));
        }
        this.lastReaderContext = context;
    }

    void replayInit(Collector other) {
        if (!this.isCached()) {
            throw new IllegalStateException("cannot replay: cache was cleared because too much RAM was required");
        }
        if (!other.acceptsDocsOutOfOrder() && this.other.acceptsDocsOutOfOrder()) {
            throw new IllegalArgumentException("cannot replay: given collector does not support out-of-order collection, while the wrapped collector does. Therefore cached documents may be out-of-order.");
        }
        if (this.lastReaderContext != null) {
            this.cachedSegs.add(new SegStart(this.lastReaderContext, this.base + this.upto));
            this.lastReaderContext = null;
        }
    }

    public abstract void replay(Collector var1) throws IOException;

    private static final class NoScoreCachingCollector
    extends CachingCollector {
        NoScoreCachingCollector(Collector other, double maxRAMMB) {
            super(other, maxRAMMB, false);
        }

        NoScoreCachingCollector(Collector other, int maxDocsToCache) {
            super(other, maxDocsToCache);
        }

        @Override
        public void collect(int doc) throws IOException {
            if (this.curDocs == null) {
                this.other.collect(doc);
                return;
            }
            if (this.upto == this.curDocs.length) {
                this.base += this.upto;
                int nextLength = 8 * this.curDocs.length;
                if (nextLength > 524288) {
                    nextLength = 524288;
                }
                if (this.base + nextLength > this.maxDocsToCache && (nextLength = this.maxDocsToCache - this.base) <= 0) {
                    this.curDocs = null;
                    this.cachedSegs.clear();
                    this.cachedDocs.clear();
                    this.other.collect(doc);
                    return;
                }
                this.curDocs = new int[nextLength];
                this.cachedDocs.add(this.curDocs);
                this.upto = 0;
            }
            this.curDocs[this.upto] = doc;
            ++this.upto;
            this.other.collect(doc);
        }

        @Override
        public void replay(Collector other) throws IOException {
            this.replayInit(other);
            int curUpto = 0;
            int curbase = 0;
            int chunkUpto = 0;
            this.curDocs = EMPTY_INT_ARRAY;
            for (SegStart seg : this.cachedSegs) {
                other.setNextReader(seg.readerContext);
                while (curbase + curUpto < seg.end) {
                    if (curUpto == this.curDocs.length) {
                        curbase += this.curDocs.length;
                        this.curDocs = (int[])this.cachedDocs.get(chunkUpto);
                        ++chunkUpto;
                        curUpto = 0;
                    }
                    other.collect(this.curDocs[curUpto++]);
                }
            }
        }

        @Override
        public void setScorer(Scorer scorer) throws IOException {
            this.other.setScorer(scorer);
        }

        public String toString() {
            if (this.isCached()) {
                return "CachingCollector (" + (this.base + this.upto) + " docs cached)";
            }
            return "CachingCollector (cache was cleared)";
        }
    }

    private static final class ScoreCachingCollector
    extends CachingCollector {
        private final CachedScorer cachedScorer = new CachedScorer();
        private final List<float[]> cachedScores = new ArrayList<float[]>();
        private Scorer scorer;
        private float[] curScores = new float[128];

        ScoreCachingCollector(Collector other, double maxRAMMB) {
            super(other, maxRAMMB, true);
            this.cachedScores.add(this.curScores);
        }

        ScoreCachingCollector(Collector other, int maxDocsToCache) {
            super(other, maxDocsToCache);
            this.cachedScores.add(this.curScores);
        }

        @Override
        public void collect(int doc) throws IOException {
            if (this.curDocs == null) {
                this.cachedScorer.score = this.scorer.score();
                this.cachedScorer.doc = doc;
                this.other.collect(doc);
                return;
            }
            if (this.upto == this.curDocs.length) {
                this.base += this.upto;
                int nextLength = 8 * this.curDocs.length;
                if (nextLength > 524288) {
                    nextLength = 524288;
                }
                if (this.base + nextLength > this.maxDocsToCache && (nextLength = this.maxDocsToCache - this.base) <= 0) {
                    this.curDocs = null;
                    this.curScores = null;
                    this.cachedSegs.clear();
                    this.cachedDocs.clear();
                    this.cachedScores.clear();
                    this.cachedScorer.score = this.scorer.score();
                    this.cachedScorer.doc = doc;
                    this.other.collect(doc);
                    return;
                }
                this.curDocs = new int[nextLength];
                this.cachedDocs.add(this.curDocs);
                this.curScores = new float[nextLength];
                this.cachedScores.add(this.curScores);
                this.upto = 0;
            }
            this.curDocs[this.upto] = doc;
            this.cachedScorer.score = this.curScores[this.upto] = this.scorer.score();
            ++this.upto;
            this.cachedScorer.doc = doc;
            this.other.collect(doc);
        }

        @Override
        public void replay(Collector other) throws IOException {
            this.replayInit(other);
            int curUpto = 0;
            int curBase = 0;
            int chunkUpto = 0;
            this.curDocs = EMPTY_INT_ARRAY;
            for (SegStart seg : this.cachedSegs) {
                other.setNextReader(seg.readerContext);
                other.setScorer(this.cachedScorer);
                while (curBase + curUpto < seg.end) {
                    if (curUpto == this.curDocs.length) {
                        curBase += this.curDocs.length;
                        this.curDocs = (int[])this.cachedDocs.get(chunkUpto);
                        this.curScores = this.cachedScores.get(chunkUpto);
                        ++chunkUpto;
                        curUpto = 0;
                    }
                    this.cachedScorer.score = this.curScores[curUpto];
                    this.cachedScorer.doc = this.curDocs[curUpto];
                    other.collect(this.curDocs[curUpto++]);
                }
            }
        }

        @Override
        public void setScorer(Scorer scorer) throws IOException {
            this.scorer = scorer;
            this.other.setScorer(this.cachedScorer);
        }

        public String toString() {
            if (this.isCached()) {
                return "CachingCollector (" + (this.base + this.upto) + " docs & scores cached)";
            }
            return "CachingCollector (cache was cleared)";
        }
    }

    private static final class CachedScorer
    extends Scorer {
        int doc;
        float score;

        private CachedScorer() {
            super(null);
        }

        @Override
        public final float score() {
            return this.score;
        }

        @Override
        public final int advance(int target) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final int docID() {
            return this.doc;
        }

        @Override
        public final int freq() {
            throw new UnsupportedOperationException();
        }

        @Override
        public final int nextDoc() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long cost() {
            return 1L;
        }
    }

    private static class SegStart {
        public final AtomicReaderContext readerContext;
        public final int end;

        public SegStart(AtomicReaderContext readerContext, int end) {
            this.readerContext = readerContext;
            this.end = end;
        }
    }
}

