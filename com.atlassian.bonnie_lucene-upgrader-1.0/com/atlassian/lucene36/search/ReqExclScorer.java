/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.search.DocIdSetIterator;
import com.atlassian.lucene36.search.Scorer;
import java.io.IOException;

class ReqExclScorer
extends Scorer {
    private Scorer reqScorer;
    private DocIdSetIterator exclDisi;
    private int doc = -1;

    public ReqExclScorer(Scorer reqScorer, DocIdSetIterator exclDisi) {
        super(reqScorer.weight);
        this.reqScorer = reqScorer;
        this.exclDisi = exclDisi;
    }

    public int nextDoc() throws IOException {
        if (this.reqScorer == null) {
            return this.doc;
        }
        this.doc = this.reqScorer.nextDoc();
        if (this.doc == Integer.MAX_VALUE) {
            this.reqScorer = null;
            return this.doc;
        }
        if (this.exclDisi == null) {
            return this.doc;
        }
        this.doc = this.toNonExcluded();
        return this.doc;
    }

    private int toNonExcluded() throws IOException {
        int exclDoc = this.exclDisi.docID();
        int reqDoc = this.reqScorer.docID();
        do {
            if (reqDoc < exclDoc) {
                return reqDoc;
            }
            if (reqDoc <= exclDoc) continue;
            exclDoc = this.exclDisi.advance(reqDoc);
            if (exclDoc == Integer.MAX_VALUE) {
                this.exclDisi = null;
                return reqDoc;
            }
            if (exclDoc <= reqDoc) continue;
            return reqDoc;
        } while ((reqDoc = this.reqScorer.nextDoc()) != Integer.MAX_VALUE);
        this.reqScorer = null;
        return Integer.MAX_VALUE;
    }

    public int docID() {
        return this.doc;
    }

    public float score() throws IOException {
        return this.reqScorer.score();
    }

    public float freq() throws IOException {
        return this.reqScorer.freq();
    }

    public int advance(int target) throws IOException {
        if (this.reqScorer == null) {
            this.doc = Integer.MAX_VALUE;
            return Integer.MAX_VALUE;
        }
        if (this.exclDisi == null) {
            this.doc = this.reqScorer.advance(target);
            return this.doc;
        }
        if (this.reqScorer.advance(target) == Integer.MAX_VALUE) {
            this.reqScorer = null;
            this.doc = Integer.MAX_VALUE;
            return Integer.MAX_VALUE;
        }
        this.doc = this.toNonExcluded();
        return this.doc;
    }
}

