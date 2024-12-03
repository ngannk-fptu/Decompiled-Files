/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;

abstract class DisjunctionScorer
extends Scorer {
    protected final Scorer[] subScorers;
    protected int numScorers;

    protected DisjunctionScorer(Weight weight, Scorer[] subScorers, int numScorers) {
        super(weight);
        this.subScorers = subScorers;
        this.numScorers = numScorers;
        this.heapify();
    }

    protected final void heapify() {
        for (int i = (this.numScorers >> 1) - 1; i >= 0; --i) {
            this.heapAdjust(i);
        }
    }

    protected final void heapAdjust(int root) {
        Scorer scorer = this.subScorers[root];
        int doc = scorer.docID();
        int i = root;
        while (i <= (this.numScorers >> 1) - 1) {
            int lchild = (i << 1) + 1;
            Scorer lscorer = this.subScorers[lchild];
            int ldoc = lscorer.docID();
            int rdoc = Integer.MAX_VALUE;
            int rchild = (i << 1) + 2;
            Scorer rscorer = null;
            if (rchild < this.numScorers) {
                rscorer = this.subScorers[rchild];
                rdoc = rscorer.docID();
            }
            if (ldoc < doc) {
                if (rdoc < ldoc) {
                    this.subScorers[i] = rscorer;
                    this.subScorers[rchild] = scorer;
                    i = rchild;
                    continue;
                }
                this.subScorers[i] = lscorer;
                this.subScorers[lchild] = scorer;
                i = lchild;
                continue;
            }
            if (rdoc < doc) {
                this.subScorers[i] = rscorer;
                this.subScorers[rchild] = scorer;
                i = rchild;
                continue;
            }
            return;
        }
    }

    protected final void heapRemoveRoot() {
        if (this.numScorers == 1) {
            this.subScorers[0] = null;
            this.numScorers = 0;
        } else {
            this.subScorers[0] = this.subScorers[this.numScorers - 1];
            this.subScorers[this.numScorers - 1] = null;
            --this.numScorers;
            this.heapAdjust(0);
        }
    }

    @Override
    public final Collection<Scorer.ChildScorer> getChildren() {
        ArrayList<Scorer.ChildScorer> children = new ArrayList<Scorer.ChildScorer>(this.numScorers);
        for (int i = 0; i < this.numScorers; ++i) {
            children.add(new Scorer.ChildScorer(this.subScorers[i], "SHOULD"));
        }
        return children;
    }

    @Override
    public long cost() {
        long sum = 0L;
        for (int i = 0; i < this.numScorers; ++i) {
            sum += this.subScorers[i].cost();
        }
        return sum;
    }
}

