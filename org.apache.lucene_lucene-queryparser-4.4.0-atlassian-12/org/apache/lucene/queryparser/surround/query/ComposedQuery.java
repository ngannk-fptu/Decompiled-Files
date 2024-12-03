/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.surround.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.lucene.queryparser.surround.query.BasicQueryFactory;
import org.apache.lucene.queryparser.surround.query.SrndQuery;
import org.apache.lucene.search.Query;

public abstract class ComposedQuery
extends SrndQuery {
    protected String opName;
    protected List<SrndQuery> queries;
    private boolean operatorInfix;

    public ComposedQuery(List<SrndQuery> qs, boolean operatorInfix, String opName) {
        this.recompose(qs);
        this.operatorInfix = operatorInfix;
        this.opName = opName;
    }

    protected void recompose(List<SrndQuery> queries) {
        if (queries.size() < 2) {
            throw new AssertionError((Object)"Too few subqueries");
        }
        this.queries = queries;
    }

    public String getOperatorName() {
        return this.opName;
    }

    public Iterator<SrndQuery> getSubQueriesIterator() {
        return this.queries.listIterator();
    }

    public int getNrSubQueries() {
        return this.queries.size();
    }

    public SrndQuery getSubQuery(int qn) {
        return this.queries.get(qn);
    }

    public boolean isOperatorInfix() {
        return this.operatorInfix;
    }

    public List<Query> makeLuceneSubQueriesField(String fn, BasicQueryFactory qf) {
        ArrayList<Query> luceneSubQueries = new ArrayList<Query>();
        Iterator<SrndQuery> sqi = this.getSubQueriesIterator();
        while (sqi.hasNext()) {
            luceneSubQueries.add(sqi.next().makeLuceneQueryField(fn, qf));
        }
        return luceneSubQueries;
    }

    @Override
    public String toString() {
        StringBuilder r = new StringBuilder();
        if (this.isOperatorInfix()) {
            this.infixToString(r);
        } else {
            this.prefixToString(r);
        }
        this.weightToString(r);
        return r.toString();
    }

    protected String getPrefixSeparator() {
        return ", ";
    }

    protected String getBracketOpen() {
        return "(";
    }

    protected String getBracketClose() {
        return ")";
    }

    protected void infixToString(StringBuilder r) {
        Iterator<SrndQuery> sqi = this.getSubQueriesIterator();
        r.append(this.getBracketOpen());
        if (sqi.hasNext()) {
            r.append(sqi.next().toString());
            while (sqi.hasNext()) {
                r.append(" ");
                r.append(this.getOperatorName());
                r.append(" ");
                r.append(sqi.next().toString());
            }
        }
        r.append(this.getBracketClose());
    }

    protected void prefixToString(StringBuilder r) {
        Iterator<SrndQuery> sqi = this.getSubQueriesIterator();
        r.append(this.getOperatorName());
        r.append(this.getBracketOpen());
        if (sqi.hasNext()) {
            r.append(sqi.next().toString());
            while (sqi.hasNext()) {
                r.append(this.getPrefixSeparator());
                r.append(sqi.next().toString());
            }
        }
        r.append(this.getBracketClose());
    }

    @Override
    public boolean isFieldsSubQueryAcceptable() {
        Iterator<SrndQuery> sqi = this.getSubQueriesIterator();
        while (sqi.hasNext()) {
            if (!sqi.next().isFieldsSubQueryAcceptable()) continue;
            return true;
        }
        return false;
    }
}

