/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.search.DocIdSet
 *  org.apache.lucene.search.Filter
 *  org.apache.lucene.search.MatchAllDocsQuery
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.QueryWrapperFilter
 *  org.apache.lucene.util.Bits
 */
package com.atlassian.confluence.impl.search.v2.lucene.filter;

import java.io.IOException;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.util.Bits;

@Deprecated
public class MatchAllDocsFilter
extends Filter {
    private static Filter instance = new MatchAllDocsFilter();
    private final Filter delegate = new QueryWrapperFilter((Query)new MatchAllDocsQuery());

    public static Filter getInstance() {
        return instance;
    }

    private MatchAllDocsFilter() {
    }

    public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
        return this.delegate.getDocIdSet(context, acceptDocs);
    }
}

