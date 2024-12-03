/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReader
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.DocsEnum
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.DocIdSet
 *  org.apache.lucene.search.Filter
 *  org.apache.lucene.util.Bits
 *  org.apache.lucene.util.OpenBitSetDISI
 */
package com.atlassian.confluence.impl.search.v2.lucene.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.OpenBitSetDISI;

public class MultiTermFilter
extends Filter {
    private static final long serialVersionUID = 9080933966775117753L;
    private List<Term> terms = new ArrayList<Term>();
    private boolean negatedFlag = false;

    public MultiTermFilter() {
        this(false);
    }

    public MultiTermFilter(boolean negating) {
        this.negatedFlag = negating;
    }

    public void addTerm(Term term) {
        if (term == null) {
            throw new IllegalArgumentException("A non-null term must be supplied.");
        }
        this.terms.add(term);
    }

    public List<Term> getTerms() {
        return Collections.unmodifiableList(this.terms);
    }

    public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
        AtomicReader reader = context.reader();
        OpenBitSetDISI result = this.getInitialBitSet((IndexReader)reader);
        for (Term term : this.terms) {
            DocsEnum docsEnum = reader.termDocsEnum(term);
            while (docsEnum != null && docsEnum.nextDoc() != Integer.MAX_VALUE) {
                if (this.negatedFlag) {
                    result.fastClear(docsEnum.docID());
                    continue;
                }
                result.fastSet(docsEnum.docID());
            }
        }
        return result;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder(((Object)((Object)this)).getClass().getSimpleName() + "(");
        int minLen = buffer.length();
        for (Term term : this.terms) {
            if (buffer.length() > minLen) {
                buffer.append(' ');
            }
            buffer.append(term.toString());
        }
        return buffer.append(')').toString();
    }

    private OpenBitSetDISI getInitialBitSet(IndexReader reader) {
        OpenBitSetDISI result = new OpenBitSetDISI(reader.maxDoc());
        if (this.negatedFlag) {
            result.set(0L, (long)reader.maxDoc());
        }
        return result;
    }
}

