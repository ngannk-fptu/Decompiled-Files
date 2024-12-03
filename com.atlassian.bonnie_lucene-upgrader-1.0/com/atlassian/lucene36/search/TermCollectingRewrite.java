/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.FilteredTermEnum;
import com.atlassian.lucene36.search.MultiTermQuery;
import com.atlassian.lucene36.search.Query;
import java.io.IOException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
abstract class TermCollectingRewrite<Q extends Query>
extends MultiTermQuery.RewriteMethod {
    TermCollectingRewrite() {
    }

    protected abstract Q getTopLevelQuery() throws IOException;

    protected abstract void addClause(Q var1, Term var2, float var3) throws IOException;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final void collectTerms(IndexReader reader, MultiTermQuery query, TermCollector collector) throws IOException {
        FilteredTermEnum enumerator = this.getTermsEnum(reader, query);
        try {
            Term t;
            while ((t = enumerator.term()) != null && collector.collect(t, enumerator.difference())) {
                if (enumerator.next()) continue;
                break;
            }
            Object var7_6 = null;
        }
        catch (Throwable throwable) {
            Object var7_7 = null;
            enumerator.close();
            throw throwable;
        }
        enumerator.close();
    }

    protected static interface TermCollector {
        public boolean collect(Term var1, float var2) throws IOException;
    }
}

