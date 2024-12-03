/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.SearchQuery;
import java.util.Collections;
import java.util.List;

public class UserInfoQuery
implements SearchQuery {
    private static final String KEY = "userInfo";
    private final String queryString;

    public UserInfoQuery(String queryString) {
        this.queryString = queryString;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List getParameters() {
        return Collections.emptyList();
    }

    public String getQueryString() {
        return this.queryString;
    }
}

