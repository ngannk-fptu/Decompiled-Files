/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.Sort
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.confluence.search.v2.SearchSort;
import org.apache.lucene.search.Sort;

@Deprecated
public interface LuceneSortMapper<T extends SearchSort> {
    public Sort convertToLuceneSort(T var1);
}

