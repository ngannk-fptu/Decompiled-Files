/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.Sort
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneSortMapper;
import com.atlassian.confluence.search.v2.SearchSort;
import org.apache.lucene.search.Sort;

public class NoOpSortMapper
implements LuceneSortMapper {
    public Sort convertToLuceneSort(SearchSort searchSort) {
        return null;
    }
}

