/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.internal.search.v2.lucene.ILuceneConnection$ReaderAction
 *  com.atlassian.confluence.internal.search.v2.lucene.ILuceneConnection$SearcherAction
 *  com.atlassian.confluence.internal.search.v2.lucene.ILuceneConnection$SearcherWithTokenAction
 *  com.atlassian.confluence.internal.search.v2.lucene.LuceneException
 *  com.atlassian.confluence.internal.search.v2.lucene.SearchTokenExpiredException
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.confluence.internal.search.v2.lucene.ILuceneConnection;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneException;
import com.atlassian.confluence.internal.search.v2.lucene.SearchTokenExpiredException;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import java.util.EnumSet;

public interface MultiConnection {
    public Object withReader(EnumSet<SearchIndex> var1, ILuceneConnection.ReaderAction var2) throws LuceneException;

    public void withSearch(EnumSet<SearchIndex> var1, ILuceneConnection.SearcherAction var2) throws LuceneException;

    public <T> T withSearcher(EnumSet<SearchIndex> var1, ILuceneConnection.SearcherWithTokenAction<T> var2);

    public <T> T withSearcher(EnumSet<SearchIndex> var1, long var2, ILuceneConnection.SearcherWithTokenAction<T> var4) throws SearchTokenExpiredException;
}

