/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.sort;

import com.atlassian.confluence.search.v2.SearchPrimitive;
import com.atlassian.confluence.search.v2.SearchSort;

@SearchPrimitive
public class LowercaseFieldSort
implements SearchSort {
    public static final String KEY = "lowercaseFieldSort";
    private String fieldName;
    private SearchSort.Order order;

    public LowercaseFieldSort(String fieldName, SearchSort.Order order) {
        this.fieldName = fieldName;
        this.order = order;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public SearchSort.Order getOrder() {
        return this.order;
    }

    public SearchSort.Type getType() {
        return SearchSort.Type.STRING;
    }

    public String getFieldName() {
        return this.fieldName;
    }
}

