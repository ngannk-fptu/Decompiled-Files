/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.user.User
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.cache;

import com.atlassian.crowd.cache.UserAuthorisationCache;
import com.atlassian.crowd.model.user.User;
import javax.annotation.Nullable;

public class DelegatingUserAuthorisationCache
implements UserAuthorisationCache {
    private final UserAuthorisationCache delegate;

    public DelegatingUserAuthorisationCache(UserAuthorisationCache delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setPermitted(User user, String applicationName, boolean permitted) {
        this.delegate.setPermitted(user, applicationName, permitted);
    }

    @Override
    @Nullable
    public Boolean isPermitted(User user, String applicationName) {
        return this.delegate.isPermitted(user, applicationName);
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }
}

