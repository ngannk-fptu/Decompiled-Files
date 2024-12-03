/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.fields.AbstractUserFieldHandler
 *  com.atlassian.confluence.plugins.cql.spi.fields.UserSubFieldFactory
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.sal.api.user.UserKey
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.cql.fields;

import com.atlassian.confluence.plugins.cql.spi.fields.AbstractUserFieldHandler;
import com.atlassian.confluence.plugins.cql.spi.fields.UserSubFieldFactory;
import com.atlassian.confluence.plugins.cql.v2search.query.WatcherQuery;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.user.UserKey;
import javax.annotation.Nullable;

public class WatcherFieldHandler
extends AbstractUserFieldHandler {
    private static final String FIELD_NAME = "watcher";
    private final UserAccessor userAccessor;

    public WatcherFieldHandler(UserSubFieldFactory subFieldFactory, @Nullable UserAccessor userAccessor) {
        super(FIELD_NAME, subFieldFactory);
        this.userAccessor = userAccessor;
    }

    protected SearchQuery createUserQuery(String value) {
        return this.createUserQuery(null, value);
    }

    public SearchQuery createUserQuery(UserKey key, String username) {
        if (key != null) {
            return new WatcherQuery(key);
        }
        return new WatcherQuery(this.userAccessor, username);
    }

    protected String getIndexFieldName() {
        return "watchers";
    }
}

