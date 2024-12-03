/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.search.Query;
import java.io.Serializable;

public class BooleanClause
implements Serializable {
    private Query query;
    private Occur occur;

    public BooleanClause(Query query, Occur occur) {
        this.query = query;
        this.occur = occur;
    }

    public Occur getOccur() {
        return this.occur;
    }

    public void setOccur(Occur occur) {
        this.occur = occur;
    }

    public Query getQuery() {
        return this.query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public boolean isProhibited() {
        return Occur.MUST_NOT == this.occur;
    }

    public boolean isRequired() {
        return Occur.MUST == this.occur;
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof BooleanClause)) {
            return false;
        }
        BooleanClause other = (BooleanClause)o;
        return this.query.equals(other.query) && this.occur == other.occur;
    }

    public int hashCode() {
        return this.query.hashCode() ^ (Occur.MUST == this.occur ? 1 : 0) ^ (Occur.MUST_NOT == this.occur ? 2 : 0);
    }

    public String toString() {
        return this.occur.toString() + this.query.toString();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Occur {
        MUST{

            public String toString() {
                return "+";
            }
        }
        ,
        SHOULD{

            public String toString() {
                return "";
            }
        }
        ,
        MUST_NOT{

            public String toString() {
                return "-";
            }
        };

    }
}

