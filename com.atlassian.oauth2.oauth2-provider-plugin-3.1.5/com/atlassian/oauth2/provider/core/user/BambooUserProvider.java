/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bamboo.user.BambooUserManager
 *  com.atlassian.sal.api.user.UserKey
 *  javax.annotation.Nonnull
 *  org.acegisecurity.userdetails.UserDetails
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.core.user;

import com.atlassian.bamboo.user.BambooUserManager;
import com.atlassian.oauth2.provider.core.user.ActiveUserNotFoundException;
import com.atlassian.oauth2.provider.core.user.ProductUserProvider;
import com.atlassian.sal.api.user.UserKey;
import java.security.Principal;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.acegisecurity.userdetails.UserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BambooUserProvider
implements ProductUserProvider {
    private static final Logger logger = LoggerFactory.getLogger(BambooUserProvider.class);
    private final BambooUserManager userManager;

    public BambooUserProvider(BambooUserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    @Nonnull
    public Optional<? extends Principal> getActiveUserByKey(@Nonnull UserKey userkey) {
        logger.debug("Looking for Jira user with key: [{}]", (Object)userkey);
        return Optional.ofNullable(this.userManager.getBambooUser(userkey.getStringValue())).filter(UserDetails::isEnabled);
    }

    @Override
    public boolean isUserDeleted(@Nonnull String userKey) throws ActiveUserNotFoundException {
        return !this.getActiveUserByKey(new UserKey(userKey)).isPresent();
    }

    @Override
    @Nonnull
    public Optional<UserKey> getKeyForUsername(@Nonnull String username) {
        return Optional.ofNullable(this.userManager.getBambooUser(username)).map(UserDetails::getUsername).map(UserKey::new);
    }

    @Override
    @Nonnull
    public Optional<String> getUsernameForKey(@Nonnull UserKey userKey) {
        return Optional.of(userKey.getStringValue());
    }
}

