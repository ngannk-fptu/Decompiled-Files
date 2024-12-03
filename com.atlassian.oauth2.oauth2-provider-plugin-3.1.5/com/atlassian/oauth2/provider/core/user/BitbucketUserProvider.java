/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bitbucket.user.UserService
 *  com.atlassian.sal.api.user.UserKey
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.core.user;

import com.atlassian.bitbucket.user.UserService;
import com.atlassian.oauth2.provider.core.user.ActiveUserNotFoundException;
import com.atlassian.oauth2.provider.core.user.ProductUserProvider;
import com.atlassian.sal.api.user.UserKey;
import java.security.Principal;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BitbucketUserProvider
implements ProductUserProvider {
    private static final Logger logger = LoggerFactory.getLogger(BitbucketUserProvider.class);
    private final UserService userService;

    public BitbucketUserProvider(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Nonnull
    public Optional<? extends Principal> getActiveUserByKey(@Nonnull UserKey userkey) throws ActiveUserNotFoundException {
        logger.debug("Looking for Bitbucket user with key: [{}]", (Object)userkey);
        if (StringUtils.isNumeric((CharSequence)userkey.getStringValue())) {
            return Optional.ofNullable(this.userService.getUserById(Integer.parseInt(userkey.getStringValue())));
        }
        return Optional.empty();
    }

    @Override
    public boolean isUserDeleted(@Nonnull String userKey) throws ActiveUserNotFoundException {
        return this.userService.getUserByName(userKey) == null;
    }

    @Override
    @Nonnull
    public Optional<UserKey> getKeyForUsername(@Nonnull String username) {
        return Optional.of(new UserKey(username));
    }

    @Override
    @Nonnull
    public Optional<String> getUsernameForKey(@Nonnull UserKey userKey) {
        return Optional.of(this.userService.getUserById(Integer.parseInt(userKey.getStringValue())).getSlug());
    }
}

