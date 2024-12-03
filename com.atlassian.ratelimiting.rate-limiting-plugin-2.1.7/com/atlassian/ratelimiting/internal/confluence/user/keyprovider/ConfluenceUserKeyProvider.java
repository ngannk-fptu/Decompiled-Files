/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.ratelimiting.internal.confluence.user.keyprovider;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.ratelimiting.user.keyprovider.UserKeyProvider;
import com.atlassian.sal.api.user.UserKey;
import java.util.Optional;

public class ConfluenceUserKeyProvider
implements UserKeyProvider {
    private final UserAccessor userAccessor;

    public ConfluenceUserKeyProvider(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    @Override
    public Optional<UserKey> apply(String username) {
        return Optional.ofNullable(this.userAccessor.getUserByName(username)).map(ConfluenceUser::getKey);
    }
}

