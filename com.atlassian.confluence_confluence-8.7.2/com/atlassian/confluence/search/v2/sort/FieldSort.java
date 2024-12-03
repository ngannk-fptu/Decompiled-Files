/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.sort;

import com.atlassian.confluence.search.v2.SearchPrimitive;
import com.atlassian.confluence.search.v2.SearchSort;

@SearchPrimitive
public final class FieldSort
implements SearchSort {
    public static final String KEY = "fieldSort";
    private String fieldName;
    private SearchSort.Type type;
    private SearchSort.Order order;

    public FieldSort(String fieldName, SearchSort.Type type, SearchSort.Order order) {
        this.fieldName = fieldName;
        this.type = type;
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
        return this.type;
    }

    public String getFieldName() {
        return this.fieldName;
    }
}

