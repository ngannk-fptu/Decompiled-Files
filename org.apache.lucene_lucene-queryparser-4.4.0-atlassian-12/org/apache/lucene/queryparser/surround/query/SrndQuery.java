/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.BooleanClause
 *  org.apache.lucene.search.BooleanClause$Occur
 *  org.apache.lucene.search.BooleanQuery
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.surround.query;

import org.apache.lucene.queryparser.surround.query.BasicQueryFactory;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

public abstract class SrndQuery
implements Cloneable {
    private float weight = 1.0f;
    private boolean weighted = false;
    public static final Query theEmptyLcnQuery = new BooleanQuery(){

        public void setBoost(float boost) {
            throw new UnsupportedOperationException();
        }

        public void add(BooleanClause clause) {
            throw new UnsupportedOperationException();
        }

        public void add(Query query, BooleanClause.Occur occur) {
            throw new UnsupportedOperationException();
        }
    };

    public void setWeight(float w) {
        this.weight = w;
        this.weighted = true;
    }

    public boolean isWeighted() {
        return this.weighted;
    }

    public float getWeight() {
        return this.weight;
    }

    public String getWeightString() {
        return Float.toString(this.getWeight());
    }

    public String getWeightOperator() {
        return "^";
    }

    protected void weightToString(StringBuilder r) {
        if (this.isWeighted()) {
            r.append(this.getWeightOperator());
            r.append(this.getWeightString());
        }
    }

    public Query makeLuceneQueryField(String fieldName, BasicQueryFactory qf) {
        Query q = this.makeLuceneQueryFieldNoBoost(fieldName, qf);
        if (this.isWeighted()) {
            q.setBoost(this.getWeight() * q.getBoost());
        }
        return q;
    }

    public abstract Query makeLuceneQueryFieldNoBoost(String var1, BasicQueryFactory var2);

    public abstract String toString();

    public boolean isFieldsSubQueryAcceptable() {
        return true;
    }

    public SrndQuery clone() {
        try {
            return (SrndQuery)super.clone();
        }
        catch (CloneNotSupportedException cns) {
            throw new Error(cns);
        }
    }

    public int hashCode() {
        return this.getClass().hashCode() ^ this.toString().hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!this.getClass().equals(obj.getClass())) {
            return false;
        }
        return this.toString().equals(obj.toString());
    }
}

