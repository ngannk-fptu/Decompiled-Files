/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 */
package com.atlassian.crowd.search.query.entity;

import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.model.token.Token;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.query.entity.EntityQuery;

@Deprecated
public class TokenQuery
extends EntityQuery<Token> {
    public TokenQuery(SearchRestriction searchRestriction, int startIndex, int maxResults) {
        super(Token.class, EntityDescriptor.token(), searchRestriction, startIndex, maxResults);
    }
}

