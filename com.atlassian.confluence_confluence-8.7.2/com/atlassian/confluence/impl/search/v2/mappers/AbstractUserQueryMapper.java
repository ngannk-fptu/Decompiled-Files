/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.confluence.impl.search.v2.mappers.BaseConstantScoreQueryMapper;
import com.atlassian.confluence.search.v2.query.AbstractUserQuery;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;

public abstract class AbstractUserQueryMapper<T extends AbstractUserQuery>
extends BaseConstantScoreQueryMapper<T> {
    private ConfluenceUserDao confluenceUserDao;

    protected String getTermValue(T query) {
        ConfluenceUser user;
        String termValue = ((AbstractUserQuery)query).userkey().isPresent() ? ((AbstractUserQuery)query).userkey().get().getStringValue() : ((user = this.confluenceUserDao.findByUsername(((AbstractUserQuery)query).username().orElse(null))) == null ? "" : user.getKey().getStringValue());
        return termValue;
    }

    public void setConfluenceUserDao(ConfluenceUserDao confluenceUserDao) {
        this.confluenceUserDao = confluenceUserDao;
    }
}

