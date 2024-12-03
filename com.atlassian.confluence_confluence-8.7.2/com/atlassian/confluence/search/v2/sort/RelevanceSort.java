/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.sort;

import com.atlassian.confluence.search.v2.SearchPrimitive;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.sort.AbstractSort;

@SearchPrimitive
public class RelevanceSort
extends AbstractSort {
    public static final String key = "relevance";

    public RelevanceSort() {
        super(key, SearchSort.Order.DESCENDING);
    }
}

