/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.labels.actions;

import com.atlassian.confluence.labels.persistence.dao.RankedLabelSearchResult;
import java.util.Comparator;

public class RankedRankComparator
implements Comparator<RankedLabelSearchResult> {
    @Override
    public int compare(RankedLabelSearchResult infoA, RankedLabelSearchResult infoB) {
        if (infoA.getRank() == infoB.getRank()) {
            if (infoA.getCount() == infoB.getCount()) {
                return infoA.getLabel().getName().compareTo(infoB.getLabel().getName());
            }
            if (infoA.getCount() < infoB.getCount()) {
                return 1;
            }
            return -1;
        }
        if (infoA.getRank() < infoB.getRank()) {
            return 1;
        }
        return -1;
    }
}

