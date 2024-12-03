/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.TermQuery
 *  org.apache.lucene.search.spans.SpanTermQuery
 */
package org.apache.lucene.queryparser.surround.query;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.surround.query.TooManyBasicQueries;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.spans.SpanTermQuery;

public class BasicQueryFactory {
    private int maxBasicQueries;
    private int queriesMade;

    public BasicQueryFactory(int maxBasicQueries) {
        this.maxBasicQueries = maxBasicQueries;
        this.queriesMade = 0;
    }

    public BasicQueryFactory() {
        this(1024);
    }

    public int getNrQueriesMade() {
        return this.queriesMade;
    }

    public int getMaxBasicQueries() {
        return this.maxBasicQueries;
    }

    public String toString() {
        return this.getClass().getName() + "(maxBasicQueries: " + this.maxBasicQueries + ", queriesMade: " + this.queriesMade + ")";
    }

    private boolean atMax() {
        return this.queriesMade >= this.maxBasicQueries;
    }

    protected synchronized void checkMax() throws TooManyBasicQueries {
        if (this.atMax()) {
            throw new TooManyBasicQueries(this.getMaxBasicQueries());
        }
        ++this.queriesMade;
    }

    public TermQuery newTermQuery(Term term) throws TooManyBasicQueries {
        this.checkMax();
        return new TermQuery(term);
    }

    public SpanTermQuery newSpanTermQuery(Term term) throws TooManyBasicQueries {
        this.checkMax();
        return new SpanTermQuery(term);
    }

    public int hashCode() {
        return this.getClass().hashCode() ^ (this.atMax() ? 7 : 992);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof BasicQueryFactory)) {
            return false;
        }
        BasicQueryFactory other = (BasicQueryFactory)obj;
        return this.atMax() == other.atMax();
    }
}

