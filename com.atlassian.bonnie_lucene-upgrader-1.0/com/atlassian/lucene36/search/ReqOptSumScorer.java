/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.search.Scorer;
import java.io.IOException;

class ReqOptSumScorer
extends Scorer {
    private Scorer reqScorer;
    private Scorer optScorer;

    public ReqOptSumScorer(Scorer reqScorer, Scorer optScorer) {
        super(reqScorer.weight);
        this.reqScorer = reqScorer;
        this.optScorer = optScorer;
    }

    public int nextDoc() throws IOException {
        return this.reqScorer.nextDoc();
    }

    public int advance(int target) throws IOException {
        return this.reqScorer.advance(target);
    }

    public int docID() {
        return this.reqScorer.docID();
    }

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

    public float freq() throws IOException {
        this.score();
        return this.optScorer != null && this.optScorer.docID() == this.reqScorer.docID() ? 2.0f : 1.0f;
    }
}

