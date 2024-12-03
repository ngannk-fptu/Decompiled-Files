/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.search.query;

import com.atlassian.user.Entity;
import com.atlassian.user.search.query.AbstractBooleanQuery;
import com.atlassian.user.search.query.Query;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TwoTermBooleanQuery<T extends Entity>
extends AbstractBooleanQuery<T> {
    private Query<T> query1;
    private Query<T> query2;

    public TwoTermBooleanQuery(Query<T> query1, Query<T> query2) {
        super(true);
        this.query1 = query1;
        this.query2 = query2;
        this.queries.add(query1);
        this.queries.add(query2);
    }

    public TwoTermBooleanQuery(Query<T> query1, Query<T> query2, String anding) {
        super(anding);
        this.query1 = query1;
        this.query2 = query2;
        this.queries.add(query1);
        this.queries.add(query2);
    }

    public TwoTermBooleanQuery(Query<T> query1, Query<T> query2, boolean anding) {
        super(anding);
        this.query1 = query1;
        this.query2 = query2;
        this.anding = anding;
        this.queries.add(query1);
        this.queries.add(query2);
    }

    public Query<T> getFirstQuery() {
        return this.query1;
    }

    public Query<T> getSecondQuery() {
        return this.query2;
    }
}

