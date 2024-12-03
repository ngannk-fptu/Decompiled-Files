/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.search.query;

import com.atlassian.user.Entity;
import com.atlassian.user.search.query.BooleanQuery;
import com.atlassian.user.search.query.Query;
import java.util.ArrayList;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AbstractBooleanQuery<T extends Entity>
implements BooleanQuery<T> {
    protected boolean anding;
    protected List<Query<T>> queries = new ArrayList<Query<T>>();

    protected AbstractBooleanQuery(boolean anding) {
        this.anding = anding;
    }

    protected AbstractBooleanQuery(String anding) {
        if (anding.equals("&")) {
            this.anding = true;
        }
    }

    @Override
    public boolean isAND() {
        return this.anding;
    }

    @Override
    public boolean isOR() {
        return !this.anding;
    }

    @Override
    public List<Query<T>> getQueries() {
        return this.queries;
    }
}

