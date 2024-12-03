/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryMapper;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneSortMapper;

public interface LuceneSearchMapperRegistry {
    public LuceneQueryMapper getQueryMapper(String var1);

    default public LuceneSortMapper getSortMapper(String key) {
        return null;
    }
}

