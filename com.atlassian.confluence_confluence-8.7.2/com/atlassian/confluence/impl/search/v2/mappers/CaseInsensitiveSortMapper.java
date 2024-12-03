/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.FieldComparatorSource
 *  org.apache.lucene.search.Sort
 *  org.apache.lucene.search.SortField
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.confluence.impl.search.v2.lucene.CaseInsensitiveSortComparatorSource;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneSortMapper;
import com.atlassian.confluence.search.v2.SearchSort;
import org.apache.lucene.search.FieldComparatorSource;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

@Deprecated
public class CaseInsensitiveSortMapper
implements LuceneSortMapper {
    private final String sortFieldName;

    public CaseInsensitiveSortMapper(String fieldName) {
        this.sortFieldName = fieldName;
    }

    public Sort convertToLuceneSort(SearchSort searchSort) {
        if (SearchSort.Order.ASCENDING.equals((Object)searchSort.getOrder())) {
            return new Sort(new SortField(this.sortFieldName, (FieldComparatorSource)new CaseInsensitiveSortComparatorSource()));
        }
        return new Sort(new SortField(this.sortFieldName, (FieldComparatorSource)new CaseInsensitiveSortComparatorSource(), true));
    }

    public String getSortFieldName() {
        return this.sortFieldName;
    }
}

