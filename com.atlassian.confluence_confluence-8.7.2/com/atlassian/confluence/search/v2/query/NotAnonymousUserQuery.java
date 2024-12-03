/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import java.util.Collections;
import java.util.List;

public class NotAnonymousUserQuery
implements SearchQuery {
    private static final NotAnonymousUserQuery instance = new NotAnonymousUserQuery();
    public static final String KEY = "anonymousUser";

    private NotAnonymousUserQuery() {
    }

    public static NotAnonymousUserQuery getInstance() {
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
        return (SearchQuery)BooleanQuery.builder().addMustNot((SearchQuery)BooleanQuery.builder().addMust(new TermQuery(SearchFieldNames.LAST_MODIFIER, "")).addMust(new TermQuery(SearchFieldNames.TYPE, ContentTypeEnum.PERSONAL_INFORMATION.getRepresentation())).build()).build();
    }
}

