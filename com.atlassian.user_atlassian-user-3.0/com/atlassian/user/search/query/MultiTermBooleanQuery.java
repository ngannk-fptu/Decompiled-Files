/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.search.query;

import com.atlassian.user.Entity;
import com.atlassian.user.search.query.AbstractBooleanQuery;
import com.atlassian.user.search.query.Query;
import java.util.Arrays;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MultiTermBooleanQuery<T extends Entity>
extends AbstractBooleanQuery<T> {
    private Query<T>[] collatedQueries;

    public MultiTermBooleanQuery(Query<T>[] collatedQueries, boolean anding) {
        super(anding);
        this.collatedQueries = collatedQueries;
    }

    @Override
    public List<Query<T>> getQueries() {
        return Arrays.asList(this.collatedQueries);
    }

    public static <T extends Entity> Query<T> allOf(Query<T> ... queries) {
        return new MultiTermBooleanQuery<T>(queries, true);
    }

    public static <T extends Entity> Query<T> anyOf(Query<T> ... queries) {
        return new MultiTermBooleanQuery<T>(queries, false);
    }
}

