/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.BooleanClause$Occur
 *  org.apache.lucene.search.BooleanQuery
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.surround.query;

import java.util.List;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

class SrndBooleanQuery {
    SrndBooleanQuery() {
    }

    public static void addQueriesToBoolean(BooleanQuery bq, List<Query> queries, BooleanClause.Occur occur) {
        for (int i = 0; i < queries.size(); ++i) {
            bq.add(queries.get(i), occur);
        }
    }

    public static Query makeBooleanQuery(List<Query> queries, BooleanClause.Occur occur) {
        if (queries.size() <= 1) {
            throw new AssertionError((Object)("Too few subqueries: " + queries.size()));
        }
        BooleanQuery bq = new BooleanQuery();
        SrndBooleanQuery.addQueriesToBoolean(bq, queries.subList(0, queries.size()), occur);
        return bq;
    }
}

