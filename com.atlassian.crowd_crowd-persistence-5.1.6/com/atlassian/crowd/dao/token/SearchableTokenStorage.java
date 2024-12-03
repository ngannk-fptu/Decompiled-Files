/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.token.AuthenticationToken
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 */
package com.atlassian.crowd.dao.token;

import com.atlassian.crowd.model.token.AuthenticationToken;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import java.util.List;

public interface SearchableTokenStorage {
    public List<AuthenticationToken> search(EntityQuery<? extends AuthenticationToken> var1);
}

