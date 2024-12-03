/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.apache.lucene.search.FieldComparatorSource
 *  org.apache.lucene.search.Sort
 *  org.apache.lucene.search.SortField
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.impl.search.v2.lucene.CaseInsensitiveSortComparatorSource;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneSortMapper;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.sort.LowercaseFieldSort;
import org.apache.lucene.search.FieldComparatorSource;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

@Internal
public class LowercaseFieldSortMapper
implements LuceneSortMapper<LowercaseFieldSort> {
    @Override
    public Sort convertToLuceneSort(LowercaseFieldSort searchSort) {
        if (SearchSort.Order.ASCENDING.equals((Object)searchSort.getOrder())) {
            return new Sort(new SortField(searchSort.getFieldName(), (FieldComparatorSource)new CaseInsensitiveSortComparatorSource()));
        }
        return new Sort(new SortField(searchSort.getFieldName(), (FieldComparatorSource)new CaseInsensitiveSortComparatorSource(), true));
    }
}

