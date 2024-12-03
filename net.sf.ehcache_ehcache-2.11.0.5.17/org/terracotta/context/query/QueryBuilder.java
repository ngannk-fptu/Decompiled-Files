/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.context.query;

import org.terracotta.context.TreeNode;
import org.terracotta.context.query.ChainedQuery;
import org.terracotta.context.query.Children;
import org.terracotta.context.query.Descendants;
import org.terracotta.context.query.EmptyQuery;
import org.terracotta.context.query.EnsureUnique;
import org.terracotta.context.query.Filter;
import org.terracotta.context.query.Matcher;
import org.terracotta.context.query.NullQuery;
import org.terracotta.context.query.Query;

public class QueryBuilder {
    private Query current = NullQuery.INSTANCE;

    private QueryBuilder() {
    }

    public static QueryBuilder queryBuilder() {
        return new QueryBuilder();
    }

    public QueryBuilder filter(Matcher<? super TreeNode> filter) {
        return this.chain(new Filter(filter));
    }

    public QueryBuilder children() {
        return this.chain(Children.INSTANCE);
    }

    public QueryBuilder descendants() {
        return this.chain(Descendants.INSTANCE);
    }

    public QueryBuilder chain(Query query) {
        this.current = new ChainedQuery(this.current, query);
        return this;
    }

    public QueryBuilder ensureUnique() {
        return this.chain(EnsureUnique.INSTANCE);
    }

    public QueryBuilder empty() {
        this.current = EmptyQuery.INSTANCE;
        return this;
    }

    public Query build() {
        return this.current;
    }
}

