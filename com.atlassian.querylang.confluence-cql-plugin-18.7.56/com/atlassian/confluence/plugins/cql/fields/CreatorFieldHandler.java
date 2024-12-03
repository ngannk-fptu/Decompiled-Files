/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.fields.AbstractUserFieldHandler
 *  com.atlassian.confluence.plugins.cql.spi.fields.UserSubFieldFactory
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.CreatorQuery
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.plugins.cql.fields;

import com.atlassian.confluence.plugins.cql.spi.fields.AbstractUserFieldHandler;
import com.atlassian.confluence.plugins.cql.spi.fields.UserSubFieldFactory;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.CreatorQuery;
import com.atlassian.sal.api.user.UserKey;

public class CreatorFieldHandler
extends AbstractUserFieldHandler {
    private static final String FIELD_NAME = "creator";

    public CreatorFieldHandler(UserSubFieldFactory subFieldFactory) {
        super(FIELD_NAME, subFieldFactory);
    }

    public SearchQuery createUserQuery(String username) {
        return this.createUserQuery(null, username);
    }

    public SearchQuery createUserQuery(UserKey key, String username) {
        if (key != null) {
            return new CreatorQuery(key);
        }
        return new CreatorQuery(username);
    }

    protected String getIndexFieldName() {
        return SearchFieldNames.CREATOR;
    }
}

