/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery$Builder
 *  com.atlassian.confluence.search.v2.query.TermQuery
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.plugins.cql.v2search.query;

import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.google.common.collect.ImmutableList;
import java.util.List;

public abstract class AbstractUserFieldQuery
implements SearchQuery {
    private static final String QUERY_KEY = "userField";
    private final List<String> parameters;

    protected AbstractUserFieldQuery(Iterable<String> values) {
        this.parameters = ImmutableList.copyOf(values);
    }

    public String getKey() {
        return QUERY_KEY;
    }

    public List<String> getParameters() {
        return this.parameters;
    }

    public abstract String getFieldName();

    public SearchQuery expand() {
        BooleanQuery.Builder result = BooleanQuery.builder();
        this.parameters.stream().forEach(param -> result.addShould((Object)new TermQuery(this.getFieldName(), param)));
        return result.build();
    }
}

