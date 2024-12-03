/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.misc;

import java.util.Comparator;
import org.apache.lucene.misc.TermStats;

final class TotalTermFreqComparatorSortDescending
implements Comparator<TermStats> {
    TotalTermFreqComparatorSortDescending() {
    }

    @Override
    public int compare(TermStats a, TermStats b) {
        if (a.totalTermFreq < b.totalTermFreq) {
            return 1;
        }
        if (a.totalTermFreq > b.totalTermFreq) {
            return -1;
        }
        return 0;
    }
}

