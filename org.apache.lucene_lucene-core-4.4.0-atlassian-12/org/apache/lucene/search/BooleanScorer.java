/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;

final class BooleanScorer
extends Scorer {
    private SubScorer scorers = null;
    private BucketTable bucketTable = new BucketTable();
    private final float[] coordFactors;
    private final int minNrShouldMatch;
    private int end;
    private Bucket current;
    private static final int PROHIBITED_MASK = 1;

    BooleanScorer(BooleanQuery.BooleanWeight weight, boolean disableCoord, int minNrShouldMatch, List<Scorer> optionalScorers, List<Scorer> prohibitedScorers, int maxCoord) throws IOException {
        super(weight);
        this.minNrShouldMatch = minNrShouldMatch;
        if (optionalScorers != null && optionalScorers.size() > 0) {
            for (Scorer scorer : optionalScorers) {
                if (scorer.nextDoc() == Integer.MAX_VALUE) continue;
                this.scorers = new SubScorer(scorer, false, false, this.bucketTable.newCollector(0), this.scorers);
            }
        }
        if (prohibitedScorers != null && prohibitedScorers.size() > 0) {
            for (Scorer scorer : prohibitedScorers) {
                if (scorer.nextDoc() == Integer.MAX_VALUE) continue;
                this.scorers = new SubScorer(scorer, false, true, this.bucketTable.newCollector(1), this.scorers);
            }
        }
        this.coordFactors = new float[optionalScorers.size() + 1];
        for (int i = 0; i < this.coordFactors.length; ++i) {
            this.coordFactors[i] = disableCoord ? 1.0f : weight.coord(i, maxCoord);
        }
    }

    @Override
    public boolean score(Collector collector, int max, int firstDocID) throws IOException {
        boolean more;
        assert (firstDocID == -1);
        BucketScorer bs = new BucketScorer(this.weight);
        collector.setScorer(bs);
        do {
            this.bucketTable.first = null;
            while (this.current != null) {
                if ((this.current.bits & 1) == 0) {
                    if (this.current.doc >= max) {
                        Bucket tmp = this.current;
                        this.current = this.current.next;
                        tmp.next = this.bucketTable.first;
                        this.bucketTable.first = tmp;
                        continue;
                    }
                    if (this.current.coord >= this.minNrShouldMatch) {
                        bs.score = this.current.score * (double)this.coordFactors[this.current.coord];
                        bs.doc = this.current.doc;
                        bs.freq = this.current.coord;
                        collector.collect(this.current.doc);
                    }
                }
                this.current = this.current.next;
            }
            if (this.bucketTable.first != null) {
                this.current = this.bucketTable.first;
                this.bucketTable.first = this.current.next;
                return true;
            }
            more = false;
            this.end += 2048;
            SubScorer sub = this.scorers;
            while (sub != null) {
                int subScorerDocID = sub.scorer.docID();
                if (subScorerDocID != Integer.MAX_VALUE) {
                    more |= sub.scorer.score(sub.collector, this.end, subScorerDocID);
                }
                sub = sub.next;
            }
            this.current = this.bucketTable.first;
        } while (this.current != null || more);
        return false;
    }

    @Override
    public int advance(int target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int docID() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int nextDoc() {
        throw new UnsupportedOperationException();
    }

    @Override
    public float score() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int freq() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long cost() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void score(Collector collector) throws IOException {
        this.score(collector, Integer.MAX_VALUE, -1);
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("boolean(");
        SubScorer sub = this.scorers;
        while (sub != null) {
            buffer.append(sub.scorer.toString());
            buffer.append(" ");
            sub = sub.next;
        }
        buffer.append(")");
        return buffer.toString();
    }

    @Override
    public Collection<Scorer.ChildScorer> getChildren() {
        throw new UnsupportedOperationException();
    }

    static final class SubScorer {
        public Scorer scorer;
        public boolean prohibited;
        public Collector collector;
        public SubScorer next;

        public SubScorer(Scorer scorer, boolean required, boolean prohibited, Collector collector, SubScorer next) {
            if (required) {
                throw new IllegalArgumentException("this scorer cannot handle required=true");
            }
            this.scorer = scorer;
            this.prohibited = prohibited;
            this.collector = collector;
            this.next = next;
        }
    }

    static final class BucketTable {
        public static final int SIZE = 2048;
        public static final int MASK = 2047;
        final Bucket[] buckets = new Bucket[2048];
        Bucket first = null;

        public BucketTable() {
            for (int idx = 0; idx < 2048; ++idx) {
                this.buckets[idx] = new Bucket();
            }
        }

        public Collector newCollector(int mask) {
            return new BooleanScorerCollector(mask, this);
        }

        public int size() {
            return 2048;
        }
    }

    static final class Bucket {
        int doc = -1;
        double score;
        int bits;
        int coord;
        Bucket next;

        Bucket() {
        }
    }

    private static final class BucketScorer
    extends Scorer {
        double score;
        int doc = Integer.MAX_VALUE;
        int freq;

        public BucketScorer(Weight weight) {
            super(weight);
        }

        @Override
        public int advance(int target) {
            return Integer.MAX_VALUE;
        }

        @Override
        public int docID() {
            return this.doc;
        }

        @Override
        public int freq() {
            return this.freq;
        }

        @Override
        public int nextDoc() {
            return Integer.MAX_VALUE;
        }

        @Override
        public float score() {
            return (float)this.score;
        }

        @Override
        public long cost() {
            return 1L;
        }
    }

    private static final class BooleanScorerCollector
    extends Collector {
        private BucketTable bucketTable;
        private int mask;
        private Scorer scorer;

        public BooleanScorerCollector(int mask, BucketTable bucketTable) {
            this.mask = mask;
            this.bucketTable = bucketTable;
        }

        @Override
        public void collect(int doc) throws IOException {
            BucketTable table = this.bucketTable;
            int i = doc & 0x7FF;
            Bucket bucket = table.buckets[i];
            if (bucket.doc != doc) {
                bucket.doc = doc;
                bucket.score = this.scorer.score();
                bucket.bits = this.mask;
                bucket.coord = 1;
                bucket.next = table.first;
                table.first = bucket;
            } else {
                bucket.score += (double)this.scorer.score();
                bucket.bits |= this.mask;
                ++bucket.coord;
            }
        }

        @Override
        public void setNextReader(AtomicReaderContext context) {
        }

        @Override
        public void setScorer(Scorer scorer) {
            this.scorer = scorer;
        }

        @Override
        public boolean acceptsDocsOutOfOrder() {
            return true;
        }
    }
}

