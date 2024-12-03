/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.HitQueue;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopDocsCollector;

public abstract class TopScoreDocCollector
extends TopDocsCollector<ScoreDoc> {
    ScoreDoc pqTop = (ScoreDoc)this.pq.top();
    int docBase = 0;
    Scorer scorer;

    public static TopScoreDocCollector create(int numHits, boolean docsScoredInOrder) {
        return TopScoreDocCollector.create(numHits, null, docsScoredInOrder);
    }

    public static TopScoreDocCollector create(int numHits, ScoreDoc after, boolean docsScoredInOrder) {
        if (numHits <= 0) {
            throw new IllegalArgumentException("numHits must be > 0; please use TotalHitCountCollector if you just need the total hit count");
        }
        if (docsScoredInOrder) {
            return after == null ? new InOrderTopScoreDocCollector(numHits) : new InOrderPagingScoreDocCollector(after, numHits);
        }
        return after == null ? new OutOfOrderTopScoreDocCollector(numHits) : new OutOfOrderPagingScoreDocCollector(after, numHits);
    }

    private TopScoreDocCollector(int numHits) {
        super(new HitQueue(numHits, true));
    }

    @Override
    protected TopDocs newTopDocs(ScoreDoc[] results, int start) {
        if (results == null) {
            return EMPTY_TOPDOCS;
        }
        float maxScore = Float.NaN;
        if (start == 0) {
            maxScore = results[0].score;
        } else {
            for (int i = this.pq.size(); i > 1; --i) {
                this.pq.pop();
            }
            maxScore = ((ScoreDoc)this.pq.pop()).score;
        }
        return new TopDocs(this.totalHits, results, maxScore);
    }

    @Override
    public void setNextReader(AtomicReaderContext context) {
        this.docBase = context.docBase;
    }

    @Override
    public void setScorer(Scorer scorer) throws IOException {
        this.scorer = scorer;
    }

    private static class OutOfOrderPagingScoreDocCollector
    extends TopScoreDocCollector {
        private final ScoreDoc after;
        private int afterDoc;
        private int collectedHits;

        private OutOfOrderPagingScoreDocCollector(ScoreDoc after, int numHits) {
            super(numHits);
            this.after = after;
        }

        @Override
        public void collect(int doc) throws IOException {
            float score = this.scorer.score();
            assert (!Float.isNaN(score));
            ++this.totalHits;
            if (score > this.after.score || score == this.after.score && doc <= this.afterDoc) {
                return;
            }
            if (score < this.pqTop.score) {
                return;
            }
            if (score == this.pqTop.score && (doc += this.docBase) > this.pqTop.doc) {
                return;
            }
            ++this.collectedHits;
            this.pqTop.doc = doc;
            this.pqTop.score = score;
            this.pqTop = (ScoreDoc)this.pq.updateTop();
        }

        @Override
        public boolean acceptsDocsOutOfOrder() {
            return true;
        }

        @Override
        public void setNextReader(AtomicReaderContext context) {
            super.setNextReader(context);
            this.afterDoc = this.after.doc - this.docBase;
        }

        @Override
        protected int topDocsSize() {
            return this.collectedHits < this.pq.size() ? this.collectedHits : this.pq.size();
        }

        @Override
        protected TopDocs newTopDocs(ScoreDoc[] results, int start) {
            return results == null ? new TopDocs(this.totalHits, new ScoreDoc[0], Float.NaN) : new TopDocs(this.totalHits, results);
        }
    }

    private static class OutOfOrderTopScoreDocCollector
    extends TopScoreDocCollector {
        private OutOfOrderTopScoreDocCollector(int numHits) {
            super(numHits);
        }

        @Override
        public void collect(int doc) throws IOException {
            float score = this.scorer.score();
            assert (!Float.isNaN(score));
            ++this.totalHits;
            if (score < this.pqTop.score) {
                return;
            }
            if (score == this.pqTop.score && (doc += this.docBase) > this.pqTop.doc) {
                return;
            }
            this.pqTop.doc = doc;
            this.pqTop.score = score;
            this.pqTop = (ScoreDoc)this.pq.updateTop();
        }

        @Override
        public boolean acceptsDocsOutOfOrder() {
            return true;
        }
    }

    private static class InOrderPagingScoreDocCollector
    extends TopScoreDocCollector {
        private final ScoreDoc after;
        private int afterDoc;
        private int collectedHits;

        private InOrderPagingScoreDocCollector(ScoreDoc after, int numHits) {
            super(numHits);
            this.after = after;
        }

        @Override
        public void collect(int doc) throws IOException {
            float score = this.scorer.score();
            assert (score != Float.NEGATIVE_INFINITY);
            assert (!Float.isNaN(score));
            ++this.totalHits;
            if (score > this.after.score || score == this.after.score && doc <= this.afterDoc) {
                return;
            }
            if (score <= this.pqTop.score) {
                return;
            }
            ++this.collectedHits;
            this.pqTop.doc = doc + this.docBase;
            this.pqTop.score = score;
            this.pqTop = (ScoreDoc)this.pq.updateTop();
        }

        @Override
        public boolean acceptsDocsOutOfOrder() {
            return false;
        }

        @Override
        public void setNextReader(AtomicReaderContext context) {
            super.setNextReader(context);
            this.afterDoc = this.after.doc - this.docBase;
        }

        @Override
        protected int topDocsSize() {
            return this.collectedHits < this.pq.size() ? this.collectedHits : this.pq.size();
        }

        @Override
        protected TopDocs newTopDocs(ScoreDoc[] results, int start) {
            return results == null ? new TopDocs(this.totalHits, new ScoreDoc[0], Float.NaN) : new TopDocs(this.totalHits, results);
        }
    }

    private static class InOrderTopScoreDocCollector
    extends TopScoreDocCollector {
        private InOrderTopScoreDocCollector(int numHits) {
            super(numHits);
        }

        @Override
        public void collect(int doc) throws IOException {
            float score = this.scorer.score();
            assert (score != Float.NEGATIVE_INFINITY);
            assert (!Float.isNaN(score));
            ++this.totalHits;
            if (score <= this.pqTop.score) {
                return;
            }
            this.pqTop.doc = doc + this.docBase;
            this.pqTop.score = score;
            this.pqTop = (ScoreDoc)this.pq.updateTop();
        }

        @Override
        public boolean acceptsDocsOutOfOrder() {
            return false;
        }
    }
}

