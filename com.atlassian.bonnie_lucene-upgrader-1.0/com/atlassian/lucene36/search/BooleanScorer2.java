/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.search.BooleanClause;
import com.atlassian.lucene36.search.BooleanQuery;
import com.atlassian.lucene36.search.Collector;
import com.atlassian.lucene36.search.ConjunctionScorer;
import com.atlassian.lucene36.search.DisjunctionSumScorer;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.ReqExclScorer;
import com.atlassian.lucene36.search.ReqOptSumScorer;
import com.atlassian.lucene36.search.Scorer;
import com.atlassian.lucene36.search.Similarity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class BooleanScorer2
extends Scorer {
    private final List<Scorer> requiredScorers;
    private final List<Scorer> optionalScorers;
    private final List<Scorer> prohibitedScorers;
    private final Coordinator coordinator;
    private final Scorer countingSumScorer;
    private final int minNrShouldMatch;
    private int doc = -1;

    public BooleanScorer2(BooleanQuery.BooleanWeight weight, boolean disableCoord, Similarity similarity, int minNrShouldMatch, List<Scorer> required, List<Scorer> prohibited, List<Scorer> optional, int maxCoord) throws IOException {
        super(weight);
        if (minNrShouldMatch < 0) {
            throw new IllegalArgumentException("Minimum number of optional scorers should not be negative");
        }
        this.coordinator = new Coordinator();
        this.minNrShouldMatch = minNrShouldMatch;
        this.coordinator.maxCoord = maxCoord;
        this.optionalScorers = optional;
        this.requiredScorers = required;
        this.prohibitedScorers = prohibited;
        this.coordinator.init(weight, disableCoord);
        this.countingSumScorer = this.makeCountingSumScorer(disableCoord, similarity);
    }

    private Scorer countingDisjunctionSumScorer(List<Scorer> scorers, int minNrShouldMatch) throws IOException {
        return new DisjunctionSumScorer(this.weight, scorers, minNrShouldMatch){
            private int lastScoredDoc;
            private float lastDocScore;
            {
                this.lastScoredDoc = -1;
                this.lastDocScore = Float.NaN;
            }

            public float score() throws IOException {
                int doc = this.docID();
                if (doc >= this.lastScoredDoc) {
                    if (doc > this.lastScoredDoc) {
                        this.lastDocScore = super.score();
                        this.lastScoredDoc = doc;
                    }
                    ((BooleanScorer2)BooleanScorer2.this).coordinator.nrMatchers += this.nrMatchers;
                }
                return this.lastDocScore;
            }
        };
    }

    private Scorer countingConjunctionSumScorer(boolean disableCoord, Similarity similarity, List<Scorer> requiredScorers) throws IOException {
        final int requiredNrMatchers = requiredScorers.size();
        return new ConjunctionScorer(this.weight, requiredScorers){
            private int lastScoredDoc;
            private float lastDocScore;
            {
                super(x0, x1);
                this.lastScoredDoc = -1;
                this.lastDocScore = Float.NaN;
            }

            public float score() throws IOException {
                int doc = this.docID();
                if (doc >= this.lastScoredDoc) {
                    if (doc > this.lastScoredDoc) {
                        this.lastDocScore = super.score();
                        this.lastScoredDoc = doc;
                    }
                    ((BooleanScorer2)BooleanScorer2.this).coordinator.nrMatchers += requiredNrMatchers;
                }
                return this.lastDocScore;
            }
        };
    }

    private Scorer dualConjunctionSumScorer(boolean disableCoord, Similarity similarity, Scorer req1, Scorer req2) throws IOException {
        return new ConjunctionScorer(this.weight, req1, req2);
    }

    private Scorer makeCountingSumScorer(boolean disableCoord, Similarity similarity) throws IOException {
        return this.requiredScorers.size() == 0 ? this.makeCountingSumScorerNoReq(disableCoord, similarity) : this.makeCountingSumScorerSomeReq(disableCoord, similarity);
    }

    private Scorer makeCountingSumScorerNoReq(boolean disableCoord, Similarity similarity) throws IOException {
        int nrOptRequired;
        int n = nrOptRequired = this.minNrShouldMatch < 1 ? 1 : this.minNrShouldMatch;
        Scorer requiredCountingSumScorer = this.optionalScorers.size() > nrOptRequired ? this.countingDisjunctionSumScorer(this.optionalScorers, nrOptRequired) : (this.optionalScorers.size() == 1 ? new SingleMatchScorer(this.optionalScorers.get(0)) : this.countingConjunctionSumScorer(disableCoord, similarity, this.optionalScorers));
        return this.addProhibitedScorers(requiredCountingSumScorer);
    }

    private Scorer makeCountingSumScorerSomeReq(boolean disableCoord, Similarity similarity) throws IOException {
        Scorer requiredCountingSumScorer;
        if (this.optionalScorers.size() == this.minNrShouldMatch) {
            ArrayList<Scorer> allReq = new ArrayList<Scorer>(this.requiredScorers);
            allReq.addAll(this.optionalScorers);
            return this.addProhibitedScorers(this.countingConjunctionSumScorer(disableCoord, similarity, allReq));
        }
        Scorer scorer = requiredCountingSumScorer = this.requiredScorers.size() == 1 ? new SingleMatchScorer(this.requiredScorers.get(0)) : this.countingConjunctionSumScorer(disableCoord, similarity, this.requiredScorers);
        if (this.minNrShouldMatch > 0) {
            return this.addProhibitedScorers(this.dualConjunctionSumScorer(disableCoord, similarity, requiredCountingSumScorer, this.countingDisjunctionSumScorer(this.optionalScorers, this.minNrShouldMatch)));
        }
        return new ReqOptSumScorer(this.addProhibitedScorers(requiredCountingSumScorer), this.optionalScorers.size() == 1 ? new SingleMatchScorer(this.optionalScorers.get(0)) : this.countingDisjunctionSumScorer(this.optionalScorers, 1));
    }

    private Scorer addProhibitedScorers(Scorer requiredCountingSumScorer) throws IOException {
        return this.prohibitedScorers.size() == 0 ? requiredCountingSumScorer : new ReqExclScorer(requiredCountingSumScorer, this.prohibitedScorers.size() == 1 ? this.prohibitedScorers.get(0) : new DisjunctionSumScorer(this.weight, this.prohibitedScorers));
    }

    @Override
    public void score(Collector collector) throws IOException {
        collector.setScorer(this);
        while ((this.doc = this.countingSumScorer.nextDoc()) != Integer.MAX_VALUE) {
            collector.collect(this.doc);
        }
    }

    @Override
    protected boolean score(Collector collector, int max, int firstDocID) throws IOException {
        this.doc = firstDocID;
        collector.setScorer(this);
        while (this.doc < max) {
            collector.collect(this.doc);
            this.doc = this.countingSumScorer.nextDoc();
        }
        return this.doc != Integer.MAX_VALUE;
    }

    @Override
    public int docID() {
        return this.doc;
    }

    @Override
    public int nextDoc() throws IOException {
        this.doc = this.countingSumScorer.nextDoc();
        return this.doc;
    }

    @Override
    public float score() throws IOException {
        this.coordinator.nrMatchers = 0;
        float sum = this.countingSumScorer.score();
        return sum * this.coordinator.coordFactors[this.coordinator.nrMatchers];
    }

    @Override
    public float freq() throws IOException {
        return this.countingSumScorer.freq();
    }

    @Override
    public int advance(int target) throws IOException {
        this.doc = this.countingSumScorer.advance(target);
        return this.doc;
    }

    @Override
    public void visitSubScorers(Query parent, BooleanClause.Occur relationship, Scorer.ScorerVisitor<Query, Query, Scorer> visitor) {
        super.visitSubScorers(parent, relationship, visitor);
        Query q = this.weight.getQuery();
        for (Scorer s : this.optionalScorers) {
            s.visitSubScorers(q, BooleanClause.Occur.SHOULD, visitor);
        }
        for (Scorer s : this.prohibitedScorers) {
            s.visitSubScorers(q, BooleanClause.Occur.MUST_NOT, visitor);
        }
        for (Scorer s : this.requiredScorers) {
            s.visitSubScorers(q, BooleanClause.Occur.MUST, visitor);
        }
    }

    private class SingleMatchScorer
    extends Scorer {
        private Scorer scorer;
        private int lastScoredDoc;
        private float lastDocScore;

        SingleMatchScorer(Scorer scorer) {
            super(scorer.weight);
            this.lastScoredDoc = -1;
            this.lastDocScore = Float.NaN;
            this.scorer = scorer;
        }

        public float score() throws IOException {
            int doc = this.docID();
            if (doc >= this.lastScoredDoc) {
                if (doc > this.lastScoredDoc) {
                    this.lastDocScore = this.scorer.score();
                    this.lastScoredDoc = doc;
                }
                ++((BooleanScorer2)BooleanScorer2.this).coordinator.nrMatchers;
            }
            return this.lastDocScore;
        }

        public float freq() throws IOException {
            return 1.0f;
        }

        public int docID() {
            return this.scorer.docID();
        }

        public int nextDoc() throws IOException {
            return this.scorer.nextDoc();
        }

        public int advance(int target) throws IOException {
            return this.scorer.advance(target);
        }
    }

    private class Coordinator {
        float[] coordFactors = null;
        int maxCoord = 0;
        int nrMatchers;

        private Coordinator() {
        }

        void init(BooleanQuery.BooleanWeight weight, boolean disableCoord) {
            this.coordFactors = new float[BooleanScorer2.this.optionalScorers.size() + BooleanScorer2.this.requiredScorers.size() + 1];
            for (int i = 0; i < this.coordFactors.length; ++i) {
                this.coordFactors[i] = disableCoord ? 1.0f : weight.coord(i, this.maxCoord);
            }
        }
    }
}

