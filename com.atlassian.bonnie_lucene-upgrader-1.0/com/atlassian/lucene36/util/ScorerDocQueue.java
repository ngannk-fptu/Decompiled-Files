/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import com.atlassian.lucene36.search.Scorer;
import java.io.IOException;

@Deprecated
public class ScorerDocQueue {
    private final HeapedScorerDoc[] heap;
    private final int maxSize;
    private int size = 0;
    private HeapedScorerDoc topHSD;

    public ScorerDocQueue(int maxSize) {
        int heapSize = maxSize + 1;
        this.heap = new HeapedScorerDoc[heapSize];
        this.maxSize = maxSize;
        this.topHSD = this.heap[1];
    }

    public final void put(Scorer scorer) {
        ++this.size;
        this.heap[this.size] = new HeapedScorerDoc(scorer);
        this.upHeap();
    }

    public boolean insert(Scorer scorer) {
        if (this.size < this.maxSize) {
            this.put(scorer);
            return true;
        }
        int docNr = scorer.docID();
        if (this.size > 0 && docNr >= this.topHSD.doc) {
            this.heap[1] = new HeapedScorerDoc(scorer, docNr);
            this.downHeap();
            return true;
        }
        return false;
    }

    public final Scorer top() {
        return this.topHSD.scorer;
    }

    public final int topDoc() {
        return this.topHSD.doc;
    }

    public final float topScore() throws IOException {
        return this.topHSD.scorer.score();
    }

    public final boolean topNextAndAdjustElsePop() throws IOException {
        return this.checkAdjustElsePop(this.topHSD.scorer.nextDoc() != Integer.MAX_VALUE);
    }

    public final boolean topSkipToAndAdjustElsePop(int target) throws IOException {
        return this.checkAdjustElsePop(this.topHSD.scorer.advance(target) != Integer.MAX_VALUE);
    }

    private boolean checkAdjustElsePop(boolean cond) {
        if (cond) {
            this.topHSD.doc = this.topHSD.scorer.docID();
        } else {
            this.heap[1] = this.heap[this.size];
            this.heap[this.size] = null;
            --this.size;
        }
        this.downHeap();
        return cond;
    }

    public final Scorer pop() {
        Scorer result = this.topHSD.scorer;
        this.popNoResult();
        return result;
    }

    private final void popNoResult() {
        this.heap[1] = this.heap[this.size];
        this.heap[this.size] = null;
        --this.size;
        this.downHeap();
    }

    public final void adjustTop() {
        this.topHSD.adjust();
        this.downHeap();
    }

    public final int size() {
        return this.size;
    }

    public final void clear() {
        for (int i = 0; i <= this.size; ++i) {
            this.heap[i] = null;
        }
        this.size = 0;
    }

    private final void upHeap() {
        int i = this.size;
        HeapedScorerDoc node = this.heap[i];
        for (int j = i >>> 1; j > 0 && node.doc < this.heap[j].doc; j >>>= 1) {
            this.heap[i] = this.heap[j];
            i = j;
        }
        this.heap[i] = node;
        this.topHSD = this.heap[1];
    }

    private final void downHeap() {
        int i = 1;
        HeapedScorerDoc node = this.heap[i];
        int j = i << 1;
        int k = j + 1;
        if (k <= this.size && this.heap[k].doc < this.heap[j].doc) {
            j = k;
        }
        while (j <= this.size && this.heap[j].doc < node.doc) {
            this.heap[i] = this.heap[j];
            i = j;
            k = (j = i << 1) + 1;
            if (k > this.size || this.heap[k].doc >= this.heap[j].doc) continue;
            j = k;
        }
        this.heap[i] = node;
        this.topHSD = this.heap[1];
    }

    private class HeapedScorerDoc {
        Scorer scorer;
        int doc;

        HeapedScorerDoc(Scorer s) {
            this(s, s.docID());
        }

        HeapedScorerDoc(Scorer scorer, int doc) {
            this.scorer = scorer;
            this.doc = doc;
        }

        void adjust() {
            this.doc = this.scorer.docID();
        }
    }
}

