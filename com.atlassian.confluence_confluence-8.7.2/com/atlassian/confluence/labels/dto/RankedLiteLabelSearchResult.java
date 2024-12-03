/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.labels.dto;

import com.atlassian.confluence.labels.dto.LiteLabelSearchResult;
import java.util.Objects;

public class RankedLiteLabelSearchResult
extends LiteLabelSearchResult {
    private int rank;

    public RankedLiteLabelSearchResult(LiteLabelSearchResult other, int rank) {
        super(other.getId(), other.getName(), other.getNamespace(), other.getLowerUserName(), other.getCount());
        this.rank = rank;
    }

    public int getRank() {
        return this.rank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        RankedLiteLabelSearchResult that = (RankedLiteLabelSearchResult)o;
        return this.rank == that.rank;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.rank);
    }
}

