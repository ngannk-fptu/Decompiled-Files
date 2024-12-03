/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReader
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.DocsEnum
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.DocIdSet
 *  org.apache.lucene.search.DocIdSetIterator
 *  org.apache.lucene.search.Filter
 *  org.apache.lucene.util.Bits
 *  org.apache.lucene.util.OpenBitSet
 */
package com.atlassian.confluence.impl.search.v2.lucene.filter;

import com.atlassian.confluence.impl.search.v2.lucene.EmptyDocIdSet;
import java.io.IOException;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.OpenBitSet;

public class TermFilter
extends Filter {
    private static final int TERM_FILTER_BIT_SET_THRESHOLD = Integer.getInteger("confluence.search.lucene.termFilterBitSetThreshold", 20);
    private final Term term;

    public TermFilter(Term term) {
        this.term = term;
    }

    public Term getTerm() {
        return this.term;
    }

    public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
        AtomicReader reader = context.reader();
        final DocsEnum matches = reader.termDocsEnum(this.term);
        if (matches == null) {
            return new EmptyDocIdSet();
        }
        if (matches.cost() > (long)(reader.maxDoc() / TERM_FILTER_BIT_SET_THRESHOLD)) {
            OpenBitSet result = new OpenBitSet((long)reader.maxDoc());
            while (matches.nextDoc() != Integer.MAX_VALUE) {
                result.fastSet(matches.docID());
            }
            return result;
        }
        return new DocIdSet(){

            public DocIdSetIterator iterator() throws IOException {
                return matches;
            }
        };
    }
}

