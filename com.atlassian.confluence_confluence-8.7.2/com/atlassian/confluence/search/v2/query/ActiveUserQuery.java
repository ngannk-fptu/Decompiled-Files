/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.NotAnonymousUserQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import java.util.Collections;
import java.util.List;

public class ActiveUserQuery
implements SearchQuery {
    private static final ActiveUserQuery instance = new ActiveUserQuery();
    public static final String KEY = "activeUser";

    private ActiveUserQuery() {
    }

    public static ActiveUserQuery getInstance() {
        return instance;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List getParameters() {
        return Collections.emptyList();
    }

    @Override
    public SearchQuery expand() {
        BooleanQuery.Builder queryBuilder = BooleanQuery.builder();
        BooleanQuery.Builder orUserStatusQuery = BooleanQuery.builder();
        orUserStatusQuery.addShould(new TermQuery(SearchFieldNames.IS_LICENSED_USER, Boolean.FALSE.toString()));
        orUserStatusQuery.addShould(new TermQuery(SearchFieldNames.IS_EXTERNALLY_DELETED_USER, Boolean.TRUE.toString()));
        orUserStatusQuery.addShould(new TermQuery(SearchFieldNames.IS_DEACTIVATED_USER, Boolean.TRUE.toString()));
        orUserStatusQuery.addShould(new TermQuery(SearchFieldNames.IS_SHADOWED_USER, Boolean.TRUE.toString()));
        queryBuilder.addMustNot(orUserStatusQuery.build());
        queryBuilder.addMust(NotAnonymousUserQuery.getInstance());
        return queryBuilder.build();
    }
}

