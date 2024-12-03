/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;

public class SearcherFactory {
    public IndexSearcher newSearcher(IndexReader reader) throws IOException {
        return new IndexSearcher(reader);
    }
}

