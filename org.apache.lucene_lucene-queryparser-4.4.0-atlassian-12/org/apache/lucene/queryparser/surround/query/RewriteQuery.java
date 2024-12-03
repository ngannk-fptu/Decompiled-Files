/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.surround.query;

import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.surround.query.BasicQueryFactory;
import org.apache.lucene.queryparser.surround.query.SrndQuery;
import org.apache.lucene.search.Query;

abstract class RewriteQuery<SQ extends SrndQuery>
extends Query {
    protected final SQ srndQuery;
    protected final String fieldName;
    protected final BasicQueryFactory qf;

    RewriteQuery(SQ srndQuery, String fieldName, BasicQueryFactory qf) {
        this.srndQuery = srndQuery;
        this.fieldName = fieldName;
        this.qf = qf;
    }

    public abstract Query rewrite(IndexReader var1) throws IOException;

    public String toString() {
        return this.toString(null);
    }

    public String toString(String field) {
        return ((Object)((Object)this)).getClass().getName() + (field == null ? "" : "(unused: " + field + ")") + "(" + this.fieldName + ", " + ((SrndQuery)this.srndQuery).toString() + ", " + this.qf.toString() + ")";
    }

    public int hashCode() {
        return ((Object)((Object)this)).getClass().hashCode() ^ this.fieldName.hashCode() ^ this.qf.hashCode() ^ ((SrndQuery)this.srndQuery).hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!((Object)((Object)this)).getClass().equals(obj.getClass())) {
            return false;
        }
        RewriteQuery other = (RewriteQuery)((Object)obj);
        return this.fieldName.equals(other.fieldName) && this.qf.equals(other.qf) && ((SrndQuery)this.srndQuery).equals(other.srndQuery);
    }

    public RewriteQuery clone() {
        throw new UnsupportedOperationException();
    }
}

