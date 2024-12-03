/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.search.BooleanClause$Occur
 *  org.apache.lucene.search.BooleanQuery
 *  org.apache.lucene.search.BooleanQuery$BooleanWeight
 *  org.apache.lucene.search.IndexSearcher
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.Weight
 */
package org.apache.lucene.queries;

import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Weight;

public class BoostingQuery
extends Query {
    private final float boost;
    private final Query match;
    private final Query context;

    public BoostingQuery(Query match, Query context, float boost) {
        this.match = match;
        this.context = context.clone();
        this.boost = boost;
        this.context.setBoost(0.0f);
    }

    public Query rewrite(IndexReader reader) throws IOException {
        BooleanQuery result = new BooleanQuery(){

            public Weight createWeight(IndexSearcher searcher) throws IOException {
                return new BooleanQuery.BooleanWeight(searcher, false){

                    public float coord(int overlap, int max) {
                        switch (overlap) {
                            case 1: {
                                return 1.0f;
                            }
                            case 2: {
                                return BoostingQuery.this.boost;
                            }
                        }
                        return 0.0f;
                    }
                };
            }
        };
        result.add(this.match, BooleanClause.Occur.MUST);
        result.add(this.context, BooleanClause.Occur.SHOULD);
        return result;
    }

    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + Float.floatToIntBits(this.boost);
        result = 31 * result + (this.context == null ? 0 : this.context.hashCode());
        result = 31 * result + (this.match == null ? 0 : this.match.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (((Object)((Object)this)).getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        BoostingQuery other = (BoostingQuery)((Object)obj);
        if (Float.floatToIntBits(this.boost) != Float.floatToIntBits(other.boost)) {
            return false;
        }
        if (this.context == null ? other.context != null : !this.context.equals((Object)other.context)) {
            return false;
        }
        return !(this.match == null ? other.match != null : !this.match.equals((Object)other.match));
    }

    public String toString(String field) {
        return this.match.toString(field) + "/" + this.context.toString(field);
    }
}

