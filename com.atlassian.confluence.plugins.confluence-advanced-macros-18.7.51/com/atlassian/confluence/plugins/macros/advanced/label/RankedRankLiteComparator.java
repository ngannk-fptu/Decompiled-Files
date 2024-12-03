/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.labels.dto.RankedLiteLabelSearchResult
 */
package com.atlassian.confluence.plugins.macros.advanced.label;

import com.atlassian.confluence.labels.dto.RankedLiteLabelSearchResult;
import java.util.Comparator;

public class RankedRankLiteComparator
implements Comparator<RankedLiteLabelSearchResult> {
    @Override
    public int compare(RankedLiteLabelSearchResult infoA, RankedLiteLabelSearchResult infoB) {
        if (infoA.getRank() == infoB.getRank()) {
            if (infoA.getCount() == infoB.getCount()) {
                return infoA.getName().compareTo(infoB.getName());
            }
            return infoA.getCount() < infoB.getCount() ? 1 : -1;
        }
        return infoA.getRank() < infoB.getRank() ? 1 : -1;
    }
}

