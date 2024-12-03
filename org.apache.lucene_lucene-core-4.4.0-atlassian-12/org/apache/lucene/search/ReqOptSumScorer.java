/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.lucene.search.Scorer;

class ReqOptSumScorer
extends Scorer {
    private Scorer reqScorer;
    private Scorer optScorer;

    public ReqOptSumScorer(Scorer reqScorer, Scorer optScorer) {
        super(reqScorer.weight);
        assert (reqScorer != null);
        assert (optScorer != null);
        this.reqScorer = reqScorer;
        this.optScorer = optScorer;
    }

    @Override
    public int nextDoc() throws IOException {
        return this.reqScorer.nextDoc();
    }

    @Override
    public int advance(int target) throws IOException {
        return this.reqScorer.advance(target);
    }

    @Override
    public int docID() {
        return this.reqScorer.docID();
    }

    @Override
    public float score() throws IOException {
        int curDoc = this.reqScorer.docID();
        float reqScore = this.reqScorer.score();
        if (this.optScorer == null) {
            return reqScore;
        }
        int optScorerDoc = this.optScorer.docID();
        if (optScorerDoc < curDoc && (optScorerDoc = this.optScorer.advance(curDoc)) == Integer.MAX_VALUE) {
            this.optScorer = null;
            return reqScore;
        }
        return optScorerDoc == curDoc ? reqScore + this.optScorer.score() : reqScore;
    }

    @Override
    public int freq() throws IOException {
        this.score();
        return this.optScorer != null && this.optScorer.docID() == this.reqScorer.docID() ? 2 : 1;
    }

    @Override
    public Collection<Scorer.ChildScorer> getChildren() {
        ArrayList<Scorer.ChildScorer> children = new ArrayList<Scorer.ChildScorer>(2);
        children.add(new Scorer.ChildScorer(this.reqScorer, "MUST"));
        children.add(new Scorer.ChildScorer(this.optScorer, "SHOULD"));
        return children;
    }

    @Override
    public long cost() {
        return this.reqScorer.cost();
    }
}

