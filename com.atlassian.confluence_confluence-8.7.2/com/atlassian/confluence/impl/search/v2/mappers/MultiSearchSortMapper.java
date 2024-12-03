/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.google.common.collect.Lists
 *  org.apache.lucene.search.Sort
 *  org.apache.lucene.search.SortField
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneSearchMapper;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneSortMapper;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.sort.MultiSearchSort;
import com.atlassian.confluence.search.v2.sort.RelevanceSort;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

@Internal
public class MultiSearchSortMapper
implements LuceneSortMapper<MultiSearchSort> {
    private final LuceneSearchMapper searchMapper;

    public MultiSearchSortMapper(LuceneSearchMapper searchMapper) {
        this.searchMapper = searchMapper;
    }

    @Override
    public Sort convertToLuceneSort(MultiSearchSort multiSearchSort) {
        ArrayList sortFields = Lists.newArrayList();
        for (SearchSort sort : multiSearchSort.getSearchSorts()) {
            if (sort instanceof RelevanceSort) {
                sortFields.addAll(Arrays.asList(Sort.RELEVANCE.getSort()));
                continue;
            }
            sortFields.addAll(Arrays.asList(this.searchMapper.convertToLuceneSort(sort).getSort()));
        }
        return new Sort(sortFields.toArray(new SortField[0]));
    }
}

