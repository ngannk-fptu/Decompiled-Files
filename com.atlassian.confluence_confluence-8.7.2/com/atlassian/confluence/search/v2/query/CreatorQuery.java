/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.AbstractUserQuery;
import com.atlassian.sal.api.user.UserKey;
import org.checkerframework.checker.nullness.qual.NonNull;

public class CreatorQuery
extends AbstractUserQuery
implements SearchQuery {
    private static final String KEY = "creator";

    public CreatorQuery(String creator) {
        super(creator);
    }

    public CreatorQuery(@NonNull UserKey userKey) {
        super(userKey);
    }

    @Override
    public String getKey() {
        return KEY;
    }
}

