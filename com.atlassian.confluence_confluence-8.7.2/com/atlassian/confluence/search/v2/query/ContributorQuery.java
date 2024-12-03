/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.AbstractUserQuery;
import com.atlassian.confluence.search.v2.query.MatchNoDocsQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import com.atlassian.sal.api.user.UserKey;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ContributorQuery
extends AbstractUserQuery
implements SearchQuery {
    private static final String KEY = "contributor";
    private final ConfluenceUserDao confluenceUserDao;

    public ContributorQuery(String username, ConfluenceUserDao confluenceUserDao) {
        super(username);
        this.confluenceUserDao = confluenceUserDao;
    }

    public ContributorQuery(@NonNull UserKey userKey, ConfluenceUserDao confluenceUserDao) {
        super(userKey);
        this.confluenceUserDao = confluenceUserDao;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public SearchQuery expand() {
        if (this.userkey().isPresent()) {
            return new TermQuery(SearchFieldNames.LAST_MODIFIERS, this.userkey().get().getStringValue());
        }
        ConfluenceUser user = this.confluenceUserDao.findByUsername(this.username().orElse(null));
        return user == null ? MatchNoDocsQuery.getInstance() : new TermQuery(SearchFieldNames.LAST_MODIFIERS, user.getKey().getStringValue());
    }
}

