/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.ArrayUtil;

class ConjunctionScorer
extends Scorer {
    protected int lastDoc = -1;
    protected final DocsAndFreqs[] docsAndFreqs;
    private final DocsAndFreqs lead;
    private final float coord;

    ConjunctionScorer(Weight weight, Scorer[] scorers) {
        this(weight, scorers, 1.0f);
    }

    ConjunctionScorer(Weight weight, Scorer[] scorers, float coord) {
        super(weight);
        this.coord = coord;
        this.docsAndFreqs = new DocsAndFreqs[scorers.length];
        for (int i = 0; i < scorers.length; ++i) {
            this.docsAndFreqs[i] = new DocsAndFreqs(scorers[i]);
        }
        ArrayUtil.timSort(this.docsAndFreqs, new Comparator<DocsAndFreqs>(){

            @Override
            public int compare(DocsAndFreqs o1, DocsAndFreqs o2) {
                return Long.signum(o1.cost - o2.cost);
            }
        });
        this.lead = this.docsAndFreqs[0];
    }

    private int doNext(int doc) throws IOException {
        while (true) {
            int i;
            block3: {
                for (i = 1; i < this.docsAndFreqs.length; ++i) {
                    if (this.docsAndFreqs[i].doc >= doc) continue;
                    this.docsAndFreqs[i].doc = this.docsAndFreqs[i].scorer.advance(doc);
                    if (this.docsAndFreqs[i].doc <= doc) {
                        continue;
                    }
                    break block3;
                }
                return doc;
            }
            doc = this.docsAndFreqs[i].doc;
            doc = this.lead.doc = this.lead.scorer.advance(doc);
        }
    }

    @Override
    public int advance(int target) throws IOException {
        this.lead.doc = this.lead.scorer.advance(target);
        this.lastDoc = this.doNext(this.lead.doc);
        return this.lastDoc;
    }

    @Override
    public int docID() {
        return this.lastDoc;
    }

    @Override
    public int nextDoc() throws IOException {
        this.lead.doc = this.lead.scorer.nextDoc();
        this.lastDoc = this.doNext(this.lead.doc);
        return this.lastDoc;
    }

    @Override
    public float score() throws IOException {
        float sum = 0.0f;
        for (DocsAndFreqs docs : this.docsAndFreqs) {
            sum += docs.scorer.score();
        }
        return sum * this.coord;
    }

    @Override
    public int freq() {
        return this.docsAndFreqs.length;
    }

    @Override
    public long cost() {
        return this.lead.scorer.cost();
    }

    @Override
    public Collection<Scorer.ChildScorer> getChildren() {
        ArrayList<Scorer.ChildScorer> children = new ArrayList<Scorer.ChildScorer>(this.docsAndFreqs.length);
        for (DocsAndFreqs docs : this.docsAndFreqs) {
            children.add(new Scorer.ChildScorer(docs.scorer, "MUST"));
        }
        return children;
    }

    static final class DocsAndFreqs {
        final long cost;
        final Scorer scorer;
        int doc = -1;

        DocsAndFreqs(Scorer scorer) {
            this.scorer = scorer;
            this.cost = scorer.cost();
        }
    }
}

