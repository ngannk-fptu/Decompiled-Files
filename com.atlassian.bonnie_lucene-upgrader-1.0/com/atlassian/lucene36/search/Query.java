/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.BooleanClause;
import com.atlassian.lucene36.search.BooleanQuery;
import com.atlassian.lucene36.search.Searcher;
import com.atlassian.lucene36.search.Similarity;
import com.atlassian.lucene36.search.Weight;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class Query
implements Serializable,
Cloneable {
    private float boost = 1.0f;

    public void setBoost(float b) {
        this.boost = b;
    }

    public float getBoost() {
        return this.boost;
    }

    public abstract String toString(String var1);

    public String toString() {
        return this.toString("");
    }

    public Weight createWeight(Searcher searcher) throws IOException {
        throw new UnsupportedOperationException("Query " + this + " does not implement createWeight");
    }

    @Deprecated
    public final Weight weight(Searcher searcher) throws IOException {
        return searcher.createNormalizedWeight(this);
    }

    public Query rewrite(IndexReader reader) throws IOException {
        return this;
    }

    public Query combine(Query[] queries) {
        HashSet<Query> uniques = new HashSet<Query>();
        for (int i = 0; i < queries.length; ++i) {
            Query query = queries[i];
            BooleanClause[] clauses = null;
            boolean splittable = query instanceof BooleanQuery;
            if (splittable) {
                BooleanQuery bq = (BooleanQuery)query;
                splittable = bq.isCoordDisabled();
                clauses = bq.getClauses();
                for (int j = 0; splittable && j < clauses.length; ++j) {
                    splittable = clauses[j].getOccur() == BooleanClause.Occur.SHOULD;
                }
            }
            if (splittable) {
                for (int j = 0; j < clauses.length; ++j) {
                    uniques.add(clauses[j].getQuery());
                }
                continue;
            }
            uniques.add(query);
        }
        if (uniques.size() == 1) {
            return (Query)uniques.iterator().next();
        }
        BooleanQuery result = new BooleanQuery(true);
        for (Query query : uniques) {
            result.add(query, BooleanClause.Occur.SHOULD);
        }
        return result;
    }

    public void extractTerms(Set<Term> terms) {
        throw new UnsupportedOperationException();
    }

    public static Query mergeBooleanQueries(BooleanQuery ... queries) {
        HashSet<BooleanClause> allClauses = new HashSet<BooleanClause>();
        for (BooleanQuery booleanQuery : queries) {
            for (BooleanClause clause : booleanQuery) {
                allClauses.add(clause);
            }
        }
        boolean coordDisabled = queries.length == 0 ? false : queries[0].isCoordDisabled();
        BooleanQuery result = new BooleanQuery(coordDisabled);
        for (BooleanClause clause2 : allClauses) {
            result.add(clause2);
        }
        return result;
    }

    @Deprecated
    public Similarity getSimilarity(Searcher searcher) {
        return searcher.getSimilarity();
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported: " + e.getMessage());
        }
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + Float.floatToIntBits(this.boost);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Query other = (Query)obj;
        return Float.floatToIntBits(this.boost) == Float.floatToIntBits(other.boost);
    }
}

