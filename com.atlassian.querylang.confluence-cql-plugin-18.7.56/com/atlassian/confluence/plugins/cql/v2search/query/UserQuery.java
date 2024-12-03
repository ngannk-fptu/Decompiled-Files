/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.v2.BooleanOperator
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.TermQuery
 *  com.atlassian.confluence.search.v2.query.TextFieldQuery
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.plugins.cql.v2search.query;

import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.BooleanOperator;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.atlassian.confluence.search.v2.query.TextFieldQuery;
import com.google.common.collect.ImmutableList;
import java.util.List;

public class UserQuery
implements SearchQuery {
    public static final String CQL_USER_QUERY = "cqlUserQuery";
    private final UserQueryType queryType;
    private final String query;

    public UserQuery(String query, UserQueryType queryType) {
        this.queryType = queryType;
        this.query = query;
    }

    public String getKey() {
        return CQL_USER_QUERY;
    }

    public List getParameters() {
        return ImmutableList.of((Object)this.query);
    }

    public UserQueryType getUserQueryType() {
        return this.queryType;
    }

    public String getRawQuery() {
        return this.query;
    }

    public SearchQuery expand() {
        return (SearchQuery)BooleanQuery.builder().addMust((Object)this.getFieldQuery()).addMust((Object)new TermQuery(SearchFieldNames.TYPE, ContentTypeEnum.PERSONAL_INFORMATION.getRepresentation())).build();
    }

    private SearchQuery getFieldQuery() {
        switch (this.queryType) {
            case FULLNAME: {
                return new TextFieldQuery(SearchFieldNames.USER_FULLNAME, this.query, BooleanOperator.AND);
            }
            case USERKEY: {
                return new TermQuery(SearchFieldNames.USER_KEY, this.query);
            }
            case USERNAME: {
                return new TextFieldQuery(SearchFieldNames.USER_NAME, this.query, BooleanOperator.AND);
            }
        }
        throw new UnsupportedOperationException("Unknown field " + this.queryType);
    }

    public static enum UserQueryType {
        USERNAME,
        USERKEY,
        FULLNAME;

    }
}

