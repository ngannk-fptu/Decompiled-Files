/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.BooleanClause;
import com.atlassian.lucene36.search.BooleanQuery;
import com.atlassian.lucene36.search.Collector;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.Scorer;
import com.atlassian.lucene36.search.Similarity;
import com.atlassian.lucene36.search.Weight;
import java.io.IOException;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class BooleanScorer
extends Scorer {
    private SubScorer scorers = null;
    private BucketTable bucketTable = new BucketTable();
    private final float[] coordFactors;
    private final int minNrShouldMatch;
    private int end;
    private Bucket current;
    private static final int PROHIBITED_MASK = 1;

    BooleanScorer(BooleanQuery.BooleanWeight weight, boolean disableCoord, Similarity similarity, int minNrShouldMatch, List<Scorer> optionalScorers, List<Scorer> prohibitedScorers, int maxCoord) throws IOException {
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
    protected boolean score(Collector collector, int max, int firstDocID) throws IOException {
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
                        bs.score = this.current.score * this.coordFactors[this.current.coord];
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
    public int advance(int target) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int docID() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int nextDoc() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public float score() {
        throw new UnsupportedOperationException();
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
    public void visitSubScorers(Query parent, BooleanClause.Occur relationship, Scorer.ScorerVisitor<Query, Query, Scorer> visitor) {
        super.visitSubScorers(parent, relationship, visitor);
        Query q = this.weight.getQuery();
        SubScorer sub = this.scorers;
        while (sub != null) {
            relationship = !sub.prohibited ? BooleanClause.Occur.SHOULD : BooleanClause.Occur.MUST_NOT;
            sub.scorer.visitSubScorers(q, relationship, visitor);
            sub = sub.next;
        }
    }

    static final class SubScorer {
        public Scorer scorer;
        public boolean prohibited;
        public Collector collector;
        public SubScorer next;

        public SubScorer(Scorer scorer, boolean required, boolean prohibited, Collector collector, SubScorer next) throws IOException {
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
        float score;
        int bits;
        int coord;
        Bucket next;

        Bucket() {
        }
    }

    private static final class BucketScorer
    extends Scorer {
        float score;
        int doc = Integer.MAX_VALUE;
        int freq;

        public BucketScorer(Weight weight) {
            super(weight);
        }

        public int advance(int target) throws IOException {
            return Integer.MAX_VALUE;
        }

        public int docID() {
            return this.doc;
        }

        public float freq() {
            return this.freq;
        }

        public int nextDoc() throws IOException {
            return Integer.MAX_VALUE;
        }

        public float score() throws IOException {
            return this.score;
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
                bucket.score += this.scorer.score();
                bucket.bits |= this.mask;
                ++bucket.coord;
            }
        }

        public void setNextReader(IndexReader reader, int docBase) {
        }

        public void setScorer(Scorer scorer) throws IOException {
            this.scorer = scorer;
        }

        public boolean acceptsDocsOutOfOrder() {
            return true;
        }
    }
}

