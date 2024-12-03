/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.labels.persistence.dao;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.persistence.dao.LabelSearchResult;

public class RankedLabelSearchResult
extends LabelSearchResult {
    private int rank;

    public RankedLabelSearchResult(LabelSearchResult other, int rank) {
        this(other.getLabel(), rank, other.getCount());
    }

    public RankedLabelSearchResult(Label label, int rank, int count) {
        super(label, count);
        this.rank = rank;
    }

    public int getRank() {
        return this.rank;
    }
}

