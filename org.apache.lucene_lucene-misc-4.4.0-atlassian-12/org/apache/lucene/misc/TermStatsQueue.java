/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.TermsEnum
 *  org.apache.lucene.util.BytesRef
 *  org.apache.lucene.util.PriorityQueue
 */
package org.apache.lucene.misc;

import java.io.IOException;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.PriorityQueue;

final class TermStatsQueue
extends PriorityQueue<TermStats> {
    TermStatsQueue(int size) {
        super(size);
    }

    protected boolean lessThan(TermStats termInfoA, TermStats termInfoB) {
        return termInfoA.docFreq < termInfoB.docFreq;
    }

    protected void fill(String field, TermsEnum termsEnum) throws IOException {
        BytesRef term;
        while ((term = termsEnum.next()) != null) {
            this.insertWithOverflow(new TermStats(field, term, termsEnum.docFreq()));
        }
    }
}

