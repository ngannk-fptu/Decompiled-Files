/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.ArrayUtil;

class MinShouldMatchSumScorer
extends Scorer {
    private int numScorers;
    private final int mm;
    private final Scorer[] sortedSubScorers;
    private int sortedSubScorersIdx = 0;
    private final Scorer[] subScorers;
    private int nrInHeap;
    private final Scorer[] mmStack;
    private int doc = -1;
    protected int nrMatchers = -1;
    private double score = Double.NaN;

    public MinShouldMatchSumScorer(Weight weight, List<Scorer> subScorers, int minimumNrMatchers) throws IOException {
        super(weight);
        int i;
        this.nrInHeap = this.numScorers = subScorers.size();
        if (minimumNrMatchers <= 0) {
            throw new IllegalArgumentException("Minimum nr of matchers must be positive");
        }
        if (this.numScorers <= 1) {
            throw new IllegalArgumentException("There must be at least 2 subScorers");
        }
        this.mm = minimumNrMatchers;
        this.sortedSubScorers = subScorers.toArray(new Scorer[this.numScorers]);
        ArrayUtil.timSort(this.sortedSubScorers, new Comparator<Scorer>(){

            @Override
            public int compare(Scorer o1, Scorer o2) {
                return Long.signum(o2.cost() - o1.cost());
            }
        });
        this.mmStack = new Scorer[this.mm - 1];
        for (i = 0; i < this.mm - 1; ++i) {
            this.mmStack[i] = this.sortedSubScorers[i];
        }
        this.nrInHeap -= this.mm - 1;
        this.sortedSubScorersIdx = this.mm - 1;
        this.subScorers = new Scorer[this.nrInHeap];
        for (i = 0; i < this.nrInHeap; ++i) {
            this.subScorers[i] = this.sortedSubScorers[this.mm - 1 + i];
        }
        this.minheapHeapify();
        assert (this.minheapCheck());
    }

    public MinShouldMatchSumScorer(Weight weight, List<Scorer> subScorers) throws IOException {
        this(weight, subScorers, 1);
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
    public int nextDoc() throws IOException {
        assert (this.doc != Integer.MAX_VALUE);
        while (true) {
            if (this.subScorers[0].docID() == this.doc) {
                if (this.subScorers[0].nextDoc() != Integer.MAX_VALUE) {
                    this.minheapSiftDown(0);
                    continue;
                }
                this.minheapRemoveRoot();
                --this.numScorers;
                if (this.numScorers >= this.mm) continue;
                this.doc = Integer.MAX_VALUE;
                return Integer.MAX_VALUE;
            }
            this.evaluateSmallestDocInHeap();
            if (this.nrMatchers >= this.mm) break;
        }
        return this.doc;
    }

    private void evaluateSmallestDocInHeap() throws IOException {
        this.doc = this.subScorers[0].docID();
        if (this.doc == Integer.MAX_VALUE) {
            this.nrMatchers = Integer.MAX_VALUE;
            return;
        }
        this.score = this.subScorers[0].score();
        this.nrMatchers = 1;
        this.countMatches(1);
        this.countMatches(2);
        for (int i = this.mm - 2; i >= 0; --i) {
            if (this.mmStack[i].docID() >= this.doc || this.mmStack[i].advance(this.doc) != Integer.MAX_VALUE) {
                if (this.mmStack[i].docID() == this.doc) {
                    ++this.nrMatchers;
                    this.score += (double)this.mmStack[i].score();
                    continue;
                }
                if (this.nrMatchers + i >= this.mm) continue;
                return;
            }
            --this.numScorers;
            if (this.numScorers < this.mm) {
                this.doc = Integer.MAX_VALUE;
                this.nrMatchers = Integer.MAX_VALUE;
                return;
            }
            if (this.mm - 2 - i > 0) {
                System.arraycopy(this.mmStack, i + 1, this.mmStack, i, this.mm - 2 - i);
            }
            while (!this.minheapRemove(this.sortedSubScorers[this.sortedSubScorersIdx++])) {
            }
            this.mmStack[this.mm - 2] = this.sortedSubScorers[this.sortedSubScorersIdx - 1];
            if (this.nrMatchers + i >= this.mm) continue;
            return;
        }
    }

    private void countMatches(int root) throws IOException {
        if (root < this.nrInHeap && this.subScorers[root].docID() == this.doc) {
            ++this.nrMatchers;
            this.score += (double)this.subScorers[root].score();
            this.countMatches((root << 1) + 1);
            this.countMatches((root << 1) + 2);
        }
    }

    @Override
    public float score() throws IOException {
        return (float)this.score;
    }

    @Override
    public int docID() {
        return this.doc;
    }

    @Override
    public int freq() throws IOException {
        return this.nrMatchers;
    }

    @Override
    public int advance(int target) throws IOException {
        if (this.numScorers < this.mm) {
            this.doc = Integer.MAX_VALUE;
            return Integer.MAX_VALUE;
        }
        while (this.subScorers[0].docID() < target) {
            if (this.subScorers[0].advance(target) != Integer.MAX_VALUE) {
                this.minheapSiftDown(0);
                continue;
            }
            this.minheapRemoveRoot();
            --this.numScorers;
            if (this.numScorers >= this.mm) continue;
            this.doc = Integer.MAX_VALUE;
            return Integer.MAX_VALUE;
        }
        this.evaluateSmallestDocInHeap();
        if (this.nrMatchers >= this.mm) {
            return this.doc;
        }
        return this.nextDoc();
    }

    @Override
    public long cost() {
        long costCandidateGeneration = 0L;
        for (int i = 0; i < this.nrInHeap; ++i) {
            costCandidateGeneration += this.subScorers[i].cost();
        }
        float c1 = 1.0f;
        float c2 = 1.0f;
        return (long)(1.0f * (float)costCandidateGeneration + 1.0f * (float)costCandidateGeneration * (float)(this.mm - 1));
    }

    protected final void minheapHeapify() {
        for (int i = (this.nrInHeap >> 1) - 1; i >= 0; --i) {
            this.minheapSiftDown(i);
        }
    }

    protected final void minheapSiftDown(int root) {
        Scorer scorer = this.subScorers[root];
        int doc = scorer.docID();
        int i = root;
        while (i <= (this.nrInHeap >> 1) - 1) {
            int lchild = (i << 1) + 1;
            Scorer lscorer = this.subScorers[lchild];
            int ldoc = lscorer.docID();
            int rdoc = Integer.MAX_VALUE;
            int rchild = (i << 1) + 2;
            Scorer rscorer = null;
            if (rchild < this.nrInHeap) {
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

    protected final void minheapSiftUp(int i) {
        int parent;
        Scorer pscorer;
        int pdoc;
        Scorer scorer = this.subScorers[i];
        int doc = scorer.docID();
        while (i > 0 && (pdoc = (pscorer = this.subScorers[parent = i - 1 >> 1]).docID()) > doc) {
            this.subScorers[i] = this.subScorers[parent];
            i = parent;
        }
        this.subScorers[i] = scorer;
    }

    protected final void minheapRemoveRoot() {
        if (this.nrInHeap == 1) {
            this.nrInHeap = 0;
        } else {
            --this.nrInHeap;
            this.subScorers[0] = this.subScorers[this.nrInHeap];
            this.minheapSiftDown(0);
        }
    }

    protected final boolean minheapRemove(Scorer scorer) {
        for (int i = 0; i < this.nrInHeap; ++i) {
            if (this.subScorers[i] != scorer) continue;
            this.subScorers[i] = this.subScorers[--this.nrInHeap];
            this.minheapSiftUp(i);
            this.minheapSiftDown(i);
            return true;
        }
        return false;
    }

    boolean minheapCheck() {
        return this.minheapCheck(0);
    }

    private boolean minheapCheck(int root) {
        if (root >= this.nrInHeap) {
            return true;
        }
        int lchild = (root << 1) + 1;
        int rchild = (root << 1) + 2;
        if (lchild < this.nrInHeap && this.subScorers[root].docID() > this.subScorers[lchild].docID()) {
            return false;
        }
        if (rchild < this.nrInHeap && this.subScorers[root].docID() > this.subScorers[rchild].docID()) {
            return false;
        }
        return this.minheapCheck(lchild) && this.minheapCheck(rchild);
    }
}

