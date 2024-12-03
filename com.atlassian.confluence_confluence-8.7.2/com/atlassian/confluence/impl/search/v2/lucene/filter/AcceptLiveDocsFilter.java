/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.search.BitsFilteredDocIdSet
 *  org.apache.lucene.search.DocIdSet
 *  org.apache.lucene.search.Filter
 *  org.apache.lucene.util.Bits
 */
package com.atlassian.confluence.impl.search.v2.lucene.filter;

import com.google.common.base.Preconditions;
import java.io.IOException;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.BitsFilteredDocIdSet;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.Bits;

public class AcceptLiveDocsFilter
extends Filter {
    private final Filter delegate;

    public AcceptLiveDocsFilter(Filter delegate) {
        Preconditions.checkNotNull((Object)delegate);
        this.delegate = delegate;
    }

    public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
        return BitsFilteredDocIdSet.wrap((DocIdSet)this.delegate.getDocIdSet(context, acceptDocs), (Bits)acceptDocs);
    }
}

