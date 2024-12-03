/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryMapper;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneSortMapper;
import com.atlassian.confluence.search.v2.SearchQuery;

public interface LuceneSearchMapper
extends LuceneQueryMapper<SearchQuery>,
LuceneSortMapper {
}

