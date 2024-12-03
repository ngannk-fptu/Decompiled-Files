/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.labels.actions;

import com.atlassian.confluence.labels.persistence.dao.RankedLabelSearchResult;
import java.util.Comparator;

public class RankedNameComparator
implements Comparator<RankedLabelSearchResult> {
    @Override
    public int compare(RankedLabelSearchResult infoA, RankedLabelSearchResult infoB) {
        return infoA.getLabel().getName().compareTo(infoB.getLabel().getName());
    }
}

