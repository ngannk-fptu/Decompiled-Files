/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.search.ScoreDoc;
import com.atlassian.lucene36.util.PriorityQueue;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class HitQueue
extends PriorityQueue<ScoreDoc> {
    private boolean prePopulate;

    HitQueue(int size, boolean prePopulate) {
        this.prePopulate = prePopulate;
        this.initialize(size);
    }

    @Override
    protected ScoreDoc getSentinelObject() {
        return !this.prePopulate ? null : new ScoreDoc(Integer.MAX_VALUE, Float.NEGATIVE_INFINITY);
    }

    @Override
    protected final boolean lessThan(ScoreDoc hitA, ScoreDoc hitB) {
        if (hitA.score == hitB.score) {
            return hitA.doc > hitB.doc;
        }
        return hitA.score < hitB.score;
    }
}

