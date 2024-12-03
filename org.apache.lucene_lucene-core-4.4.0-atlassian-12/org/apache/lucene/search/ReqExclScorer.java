/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Scorer;

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

    @Override
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

    @Override
    public int docID() {
        return this.doc;
    }

    @Override
    public float score() throws IOException {
        return this.reqScorer.score();
    }

    @Override
    public int freq() throws IOException {
        return this.reqScorer.freq();
    }

    @Override
    public Collection<Scorer.ChildScorer> getChildren() {
        return Collections.singleton(new Scorer.ChildScorer(this.reqScorer, "FILTERED"));
    }

    @Override
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

    @Override
    public long cost() {
        return this.reqScorer.cost();
    }
}

