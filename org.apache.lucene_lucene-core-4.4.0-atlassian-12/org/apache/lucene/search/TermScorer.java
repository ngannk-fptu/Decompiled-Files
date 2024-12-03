/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.similarities.Similarity;

final class TermScorer
extends Scorer {
    private final DocsEnum docsEnum;
    private final Similarity.SimScorer docScorer;

    TermScorer(Weight weight, DocsEnum td, Similarity.SimScorer docScorer) {
        super(weight);
        this.docScorer = docScorer;
        this.docsEnum = td;
    }

    @Override
    public int docID() {
        return this.docsEnum.docID();
    }

    @Override
    public int freq() throws IOException {
        return this.docsEnum.freq();
    }

    @Override
    public int nextDoc() throws IOException {
        return this.docsEnum.nextDoc();
    }

    @Override
    public float score() throws IOException {
        assert (this.docID() != Integer.MAX_VALUE);
        return this.docScorer.score(this.docsEnum.docID(), this.docsEnum.freq());
    }

    @Override
    public int advance(int target) throws IOException {
        return this.docsEnum.advance(target);
    }

    @Override
    public long cost() {
        return this.docsEnum.cost();
    }

    public String toString() {
        return "scorer(" + this.weight + ")";
    }
}

