/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.util.Set;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Weight;

public abstract class Query
implements Cloneable {
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

    public Weight createWeight(IndexSearcher searcher) throws IOException {
        throw new UnsupportedOperationException("Query " + this + " does not implement createWeight");
    }

    public Query rewrite(IndexReader reader) throws IOException {
        return this;
    }

    public void extractTerms(Set<Term> terms) {
        throw new UnsupportedOperationException();
    }

    public Query clone() {
        try {
            return (Query)super.clone();
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

