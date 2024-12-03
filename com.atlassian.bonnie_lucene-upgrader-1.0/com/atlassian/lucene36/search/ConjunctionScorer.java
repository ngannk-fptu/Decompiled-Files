/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.search.Scorer;
import com.atlassian.lucene36.search.Weight;
import com.atlassian.lucene36.util.ArrayUtil;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class ConjunctionScorer
extends Scorer {
    private final Scorer[] scorers;
    private int lastDoc = -1;

    public ConjunctionScorer(Weight weight, Collection<Scorer> scorers) throws IOException {
        this(weight, scorers.toArray(new Scorer[scorers.size()]));
    }

    public ConjunctionScorer(Weight weight, Scorer ... scorers) throws IOException {
        super(weight);
        this.scorers = scorers;
        for (int i = 0; i < scorers.length; ++i) {
            if (scorers[i].nextDoc() != Integer.MAX_VALUE) continue;
            this.lastDoc = Integer.MAX_VALUE;
            return;
        }
        ArrayUtil.mergeSort(scorers, new Comparator<Scorer>(){

            @Override
            public int compare(Scorer o1, Scorer o2) {
                return o1.docID() - o2.docID();
            }
        });
        if (this.doNext() == Integer.MAX_VALUE) {
            this.lastDoc = Integer.MAX_VALUE;
            return;
        }
        int end = scorers.length - 1;
        int max = end >> 1;
        for (int i = 0; i < max; ++i) {
            Scorer tmp = scorers[i];
            int idx = end - i - 1;
            scorers[i] = scorers[idx];
            scorers[idx] = tmp;
        }
    }

    private int doNext() throws IOException {
        Scorer firstScorer;
        int first = 0;
        int doc = this.scorers[this.scorers.length - 1].docID();
        while ((firstScorer = this.scorers[first]).docID() < doc) {
            doc = firstScorer.advance(doc);
            first = first == this.scorers.length - 1 ? 0 : first + 1;
        }
        return doc;
    }

    @Override
    public int advance(int target) throws IOException {
        if (this.lastDoc == Integer.MAX_VALUE) {
            return this.lastDoc;
        }
        if (this.scorers[this.scorers.length - 1].docID() < target) {
            this.scorers[this.scorers.length - 1].advance(target);
        }
        this.lastDoc = this.doNext();
        return this.lastDoc;
    }

    @Override
    public int docID() {
        return this.lastDoc;
    }

    @Override
    public int nextDoc() throws IOException {
        if (this.lastDoc == Integer.MAX_VALUE) {
            return this.lastDoc;
        }
        if (this.lastDoc == -1) {
            this.lastDoc = this.scorers[this.scorers.length - 1].docID();
            return this.lastDoc;
        }
        this.scorers[this.scorers.length - 1].nextDoc();
        this.lastDoc = this.doNext();
        return this.lastDoc;
    }

    @Override
    public float score() throws IOException {
        float sum = 0.0f;
        for (int i = 0; i < this.scorers.length; ++i) {
            sum += this.scorers[i].score();
        }
        return sum;
    }

    @Override
    public float freq() throws IOException {
        return this.scorers.length;
    }
}

