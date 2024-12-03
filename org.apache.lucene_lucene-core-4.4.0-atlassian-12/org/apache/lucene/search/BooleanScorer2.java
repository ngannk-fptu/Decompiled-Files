/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.ConjunctionScorer;
import org.apache.lucene.search.DisjunctionSumScorer;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.MinShouldMatchSumScorer;
import org.apache.lucene.search.ReqExclScorer;
import org.apache.lucene.search.ReqOptSumScorer;
import org.apache.lucene.search.Scorer;

class BooleanScorer2
extends Scorer {
    private final List<Scorer> requiredScorers;
    private final List<Scorer> optionalScorers;
    private final List<Scorer> prohibitedScorers;
    private final Coordinator coordinator;
    private final Scorer countingSumScorer;
    private final int minNrShouldMatch;
    private int doc = -1;

    public BooleanScorer2(BooleanQuery.BooleanWeight weight, boolean disableCoord, int minNrShouldMatch, List<Scorer> required, List<Scorer> prohibited, List<Scorer> optional, int maxCoord) throws IOException {
        super(weight);
        if (minNrShouldMatch < 0) {
            throw new IllegalArgumentException("Minimum number of optional scorers should not be negative");
        }
        this.minNrShouldMatch = minNrShouldMatch;
        this.optionalScorers = optional;
        this.requiredScorers = required;
        this.prohibitedScorers = prohibited;
        this.coordinator = new Coordinator(maxCoord, disableCoord);
        this.countingSumScorer = this.makeCountingSumScorer(disableCoord);
    }

    private Scorer countingDisjunctionSumScorer(List<Scorer> scorers, int minNrShouldMatch) throws IOException {
        if (minNrShouldMatch > 1) {
            return new MinShouldMatchSumScorer(this.weight, scorers, minNrShouldMatch){

                @Override
                public float score() throws IOException {
                    ((BooleanScorer2)BooleanScorer2.this).coordinator.nrMatchers += this.nrMatchers;
                    return super.score();
                }
            };
        }
        return new DisjunctionSumScorer(this.weight, scorers.toArray(new Scorer[scorers.size()]), null){

            @Override
            public float score() throws IOException {
                ((BooleanScorer2)BooleanScorer2.this).coordinator.nrMatchers += this.nrMatchers;
                return (float)this.score;
            }
        };
    }

    private Scorer countingConjunctionSumScorer(boolean disableCoord, List<Scorer> requiredScorers) throws IOException {
        final int requiredNrMatchers = requiredScorers.size();
        return new ConjunctionScorer(this.weight, requiredScorers.toArray(new Scorer[requiredScorers.size()])){
            private int lastScoredDoc;
            private float lastDocScore;
            {
                super(weight, scorers);
                this.lastScoredDoc = -1;
                this.lastDocScore = Float.NaN;
            }

            @Override
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

    private Scorer dualConjunctionSumScorer(boolean disableCoord, Scorer req1, Scorer req2) throws IOException {
        return new ConjunctionScorer(this.weight, new Scorer[]{req1, req2});
    }

    private Scorer makeCountingSumScorer(boolean disableCoord) throws IOException {
        return this.requiredScorers.size() == 0 ? this.makeCountingSumScorerNoReq(disableCoord) : this.makeCountingSumScorerSomeReq(disableCoord);
    }

    private Scorer makeCountingSumScorerNoReq(boolean disableCoord) throws IOException {
        int nrOptRequired;
        int n = nrOptRequired = this.minNrShouldMatch < 1 ? 1 : this.minNrShouldMatch;
        Scorer requiredCountingSumScorer = this.optionalScorers.size() > nrOptRequired ? this.countingDisjunctionSumScorer(this.optionalScorers, nrOptRequired) : (this.optionalScorers.size() == 1 ? new SingleMatchScorer(this.optionalScorers.get(0)) : this.countingConjunctionSumScorer(disableCoord, this.optionalScorers));
        return this.addProhibitedScorers(requiredCountingSumScorer);
    }

    private Scorer makeCountingSumScorerSomeReq(boolean disableCoord) throws IOException {
        Scorer requiredCountingSumScorer;
        if (this.optionalScorers.size() == this.minNrShouldMatch) {
            ArrayList<Scorer> allReq = new ArrayList<Scorer>(this.requiredScorers);
            allReq.addAll(this.optionalScorers);
            return this.addProhibitedScorers(this.countingConjunctionSumScorer(disableCoord, allReq));
        }
        Scorer scorer = requiredCountingSumScorer = this.requiredScorers.size() == 1 ? new SingleMatchScorer(this.requiredScorers.get(0)) : this.countingConjunctionSumScorer(disableCoord, this.requiredScorers);
        if (this.minNrShouldMatch > 0) {
            return this.addProhibitedScorers(this.dualConjunctionSumScorer(disableCoord, requiredCountingSumScorer, this.countingDisjunctionSumScorer(this.optionalScorers, this.minNrShouldMatch)));
        }
        return new ReqOptSumScorer(this.addProhibitedScorers(requiredCountingSumScorer), this.optionalScorers.size() == 1 ? new SingleMatchScorer(this.optionalScorers.get(0)) : this.countingDisjunctionSumScorer(this.optionalScorers, 1));
    }

    private Scorer addProhibitedScorers(Scorer requiredCountingSumScorer) throws IOException {
        return this.prohibitedScorers.size() == 0 ? requiredCountingSumScorer : new ReqExclScorer(requiredCountingSumScorer, this.prohibitedScorers.size() == 1 ? (DocIdSetIterator)this.prohibitedScorers.get(0) : new MinShouldMatchSumScorer(this.weight, this.prohibitedScorers));
    }

    @Override
    public void score(Collector collector) throws IOException {
        collector.setScorer(this);
        while ((this.doc = this.countingSumScorer.nextDoc()) != Integer.MAX_VALUE) {
            collector.collect(this.doc);
        }
    }

    @Override
    public boolean score(Collector collector, int max, int firstDocID) throws IOException {
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
    public int freq() throws IOException {
        return this.countingSumScorer.freq();
    }

    @Override
    public int advance(int target) throws IOException {
        this.doc = this.countingSumScorer.advance(target);
        return this.doc;
    }

    @Override
    public long cost() {
        return this.countingSumScorer.cost();
    }

    @Override
    public Collection<Scorer.ChildScorer> getChildren() {
        ArrayList<Scorer.ChildScorer> children = new ArrayList<Scorer.ChildScorer>();
        for (Scorer s : this.optionalScorers) {
            children.add(new Scorer.ChildScorer(s, "SHOULD"));
        }
        for (Scorer s : this.prohibitedScorers) {
            children.add(new Scorer.ChildScorer(s, "MUST_NOT"));
        }
        for (Scorer s : this.requiredScorers) {
            children.add(new Scorer.ChildScorer(s, "MUST"));
        }
        return children;
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

        @Override
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

        @Override
        public int freq() throws IOException {
            return 1;
        }

        @Override
        public int docID() {
            return this.scorer.docID();
        }

        @Override
        public int nextDoc() throws IOException {
            return this.scorer.nextDoc();
        }

        @Override
        public int advance(int target) throws IOException {
            return this.scorer.advance(target);
        }

        @Override
        public long cost() {
            return this.scorer.cost();
        }
    }

    private class Coordinator {
        final float[] coordFactors;
        int nrMatchers;

        Coordinator(int maxCoord, boolean disableCoord) {
            this.coordFactors = new float[BooleanScorer2.this.optionalScorers.size() + BooleanScorer2.this.requiredScorers.size() + 1];
            for (int i = 0; i < this.coordFactors.length; ++i) {
                this.coordFactors[i] = disableCoord ? 1.0f : ((BooleanQuery.BooleanWeight)BooleanScorer2.this.weight).coord(i, maxCoord);
            }
        }
    }
}

