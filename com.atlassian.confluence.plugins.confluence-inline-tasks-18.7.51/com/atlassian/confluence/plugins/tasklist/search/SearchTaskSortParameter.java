/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.tasklist.search;

import com.atlassian.confluence.plugins.tasklist.search.SortColumn;
import com.atlassian.confluence.plugins.tasklist.search.SortOrder;

public class SearchTaskSortParameter {
    private final SortColumn sortColumn;
    private final SortOrder sortOrder;

    public SearchTaskSortParameter(SortColumn sortColumn, SortOrder sortOrder) {
        this.sortColumn = sortColumn;
        this.sortOrder = sortOrder;
    }

    public SortColumn getSortColumn() {
        return this.sortColumn;
    }

    public SortOrder getSortOrder() {
        return this.sortOrder;
    }
}

