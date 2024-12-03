/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.AbstractParameterListQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ConstantScoreQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import java.util.Set;
import java.util.stream.Collectors;

public class InSpaceQuery
extends AbstractParameterListQuery<String> {
    private static final String KEY = "inSpace";

    public InSpaceQuery(String spaceKey) {
        super(spaceKey);
    }

    public InSpaceQuery(Set<String> spaceKeys) {
        super(spaceKeys);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public SearchQuery expand() {
        Set termQueries = this.getParameters().stream().map(spaceKey -> new TermQuery(SearchFieldNames.SPACE_KEY, (String)spaceKey)).collect(Collectors.toSet());
        SearchQuery booleanQuery = (SearchQuery)BooleanQuery.builder().addShould(termQueries).build();
        return new ConstantScoreQuery(booleanQuery);
    }
}

