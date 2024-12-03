/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.TermVectorEntry;
import java.util.Comparator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TermVectorEntryFreqSortedComparator
implements Comparator<TermVectorEntry> {
    @Override
    public int compare(TermVectorEntry entry, TermVectorEntry entry1) {
        int result = 0;
        result = entry1.getFrequency() - entry.getFrequency();
        if (result == 0 && (result = entry.getTerm().compareTo(entry1.getTerm())) == 0) {
            result = entry.getField().compareTo(entry1.getField());
        }
        return result;
    }
}

