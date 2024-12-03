/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.pats.checker;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.pats.checker.ProductUserProvider;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.security.Principal;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceProductUserProvider
implements ProductUserProvider {
    private static final Logger logger = LoggerFactory.getLogger(ConfluenceProductUserProvider.class);
    private final UserAccessor userAccessor;

    public ConfluenceProductUserProvider(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    @Override
    @Nonnull
    public Optional<? extends Principal> getActiveUserByKey(@Nonnull UserKey userkey) {
        logger.debug("Looking for Confluence user with key: [{}]", (Object)userkey);
        return Optional.ofNullable(this.userAccessor.getUserByKey(userkey)).filter(user -> !this.userAccessor.isDeactivated((User)user));
    }

    @Override
    public boolean isUserDeleted(@Nonnull String userKey) {
        logger.debug("Checking if Confluence user with key: [{}] is deleted", (Object)userKey);
        return !Optional.ofNullable(this.userAccessor.getExistingUserByKey(new UserKey(userKey))).isPresent();
    }

    @Override
    @Nonnull
    public Optional<UserKey> getKeyForUsername(@Nonnull String username) {
        return Optional.ofNullable(this.userAccessor.getUserByName(username)).map(ConfluenceUser::getKey);
    }
}

