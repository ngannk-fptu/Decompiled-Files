/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.surround.query;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.apache.lucene.queryparser.surround.query.BasicQueryFactory;
import org.apache.lucene.queryparser.surround.query.OrQuery;
import org.apache.lucene.queryparser.surround.query.SrndQuery;
import org.apache.lucene.search.Query;

public class FieldsQuery
extends SrndQuery {
    private SrndQuery q;
    private List<String> fieldNames;
    private final char fieldOp;
    private final String OrOperatorName = "OR";

    public FieldsQuery(SrndQuery q, List<String> fieldNames, char fieldOp) {
        this.q = q;
        this.fieldNames = fieldNames;
        this.fieldOp = fieldOp;
    }

    public FieldsQuery(SrndQuery q, String fieldName, char fieldOp) {
        this.q = q;
        this.fieldNames = new ArrayList<String>();
        this.fieldNames.add(fieldName);
        this.fieldOp = fieldOp;
    }

    @Override
    public boolean isFieldsSubQueryAcceptable() {
        return false;
    }

    public Query makeLuceneQueryNoBoost(BasicQueryFactory qf) {
        if (this.fieldNames.size() == 1) {
            return this.q.makeLuceneQueryFieldNoBoost(this.fieldNames.get(0), qf);
        }
        ArrayList<SrndQuery> queries = new ArrayList<SrndQuery>();
        ListIterator<String> fni = this.getFieldNames().listIterator();
        while (fni.hasNext()) {
            SrndQuery qc = this.q.clone();
            queries.add(new FieldsQuery(qc, (String)fni.next(), this.fieldOp));
        }
        OrQuery oq = new OrQuery(queries, true, "OR");
        return oq.makeLuceneQueryField(null, qf);
    }

    @Override
    public Query makeLuceneQueryFieldNoBoost(String fieldName, BasicQueryFactory qf) {
        return this.makeLuceneQueryNoBoost(qf);
    }

    public List<String> getFieldNames() {
        return this.fieldNames;
    }

    public char getFieldOperator() {
        return this.fieldOp;
    }

    @Override
    public String toString() {
        StringBuilder r = new StringBuilder();
        r.append("(");
        this.fieldNamesToString(r);
        r.append(this.q.toString());
        r.append(")");
        return r.toString();
    }

    protected void fieldNamesToString(StringBuilder r) {
        ListIterator<String> fni = this.getFieldNames().listIterator();
        while (fni.hasNext()) {
            r.append((String)fni.next());
            r.append(this.getFieldOperator());
        }
    }
}

