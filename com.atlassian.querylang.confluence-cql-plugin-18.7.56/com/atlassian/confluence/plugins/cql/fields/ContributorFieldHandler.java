/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.fields.AbstractUserFieldHandler
 *  com.atlassian.confluence.plugins.cql.spi.fields.UserSubFieldFactory
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.ContributorQuery
 *  com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.plugins.cql.fields;

import com.atlassian.confluence.plugins.cql.spi.fields.AbstractUserFieldHandler;
import com.atlassian.confluence.plugins.cql.spi.fields.UserSubFieldFactory;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.ContributorQuery;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;

public class ContributorFieldHandler
extends AbstractUserFieldHandler {
    private static final String FIELD_NAME = "contributor";
    private final ConfluenceUserDao confluenceUserDao;

    public ContributorFieldHandler(UserSubFieldFactory subFieldFactory, @ComponentImport ConfluenceUserDao confluenceUserDao) {
        super(FIELD_NAME, subFieldFactory);
        this.confluenceUserDao = confluenceUserDao;
    }

    protected SearchQuery createUserQuery(String value) {
        return this.createUserQuery(null, value);
    }

    public SearchQuery createUserQuery(UserKey key, String username) {
        if (key != null) {
            return new ContributorQuery(key, this.confluenceUserDao);
        }
        return new ContributorQuery(username, this.confluenceUserDao);
    }

    protected String getIndexFieldName() {
        return this.createUserQuery("").getKey();
    }
}

