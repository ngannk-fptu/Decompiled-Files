/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.user;

import com.atlassian.confluence.internal.user.SearchRequest;

public abstract class AbstractSearchRequestBuilder<T extends SearchRequest> {
    protected boolean showUnlicensedUsers;

    public AbstractSearchRequestBuilder<T> showUnlicensedUsers(boolean showUnlicensedUsers) {
        this.showUnlicensedUsers = showUnlicensedUsers;
        return this;
    }

    public abstract T build();
}

