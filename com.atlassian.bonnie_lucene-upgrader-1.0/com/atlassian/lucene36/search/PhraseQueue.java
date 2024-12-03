/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.search.PhrasePositions;
import com.atlassian.lucene36.util.PriorityQueue;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class PhraseQueue
extends PriorityQueue<PhrasePositions> {
    PhraseQueue(int size) {
        this.initialize(size);
    }

    @Override
    protected final boolean lessThan(PhrasePositions pp1, PhrasePositions pp2) {
        if (pp1.doc == pp2.doc) {
            if (pp1.position == pp2.position) {
                if (pp1.offset == pp2.offset) {
                    return pp1.ord < pp2.ord;
                }
                return pp1.offset < pp2.offset;
            }
            return pp1.position < pp2.position;
        }
        return pp1.doc < pp2.doc;
    }
}

