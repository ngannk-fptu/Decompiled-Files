/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.IndexSearcher;
import java.io.IOException;

public class SearcherFactory {
    public IndexSearcher newSearcher(IndexReader reader) throws IOException {
        return new IndexSearcher(reader);
    }
}

