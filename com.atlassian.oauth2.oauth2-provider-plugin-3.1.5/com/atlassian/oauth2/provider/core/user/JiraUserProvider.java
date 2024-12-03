/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jira.user.ApplicationUser
 *  com.atlassian.jira.user.UserKeyService
 *  com.atlassian.jira.user.util.UserManager
 *  com.atlassian.sal.api.user.UserKey
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.core.user;

import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.UserKeyService;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.oauth2.provider.core.user.ActiveUserNotFoundException;
import com.atlassian.oauth2.provider.core.user.ProductUserProvider;
import com.atlassian.sal.api.user.UserKey;
import java.security.Principal;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraUserProvider
implements ProductUserProvider {
    private static final Logger logger = LoggerFactory.getLogger(JiraUserProvider.class);
    private final UserKeyService userKeyService;
    private final UserManager userManager;

    public JiraUserProvider(UserKeyService userKeyService, UserManager userManager) {
        this.userKeyService = userKeyService;
        this.userManager = userManager;
    }

    @Override
    @Nonnull
    public Optional<? extends Principal> getActiveUserByKey(@Nonnull UserKey userkey) {
        logger.debug("Looking for Jira user with key: [{}]", (Object)userkey);
        return Optional.ofNullable(this.userManager.getUserByKey(userkey.getStringValue())).filter(ApplicationUser::isActive);
    }

    @Override
    public boolean isUserDeleted(@Nonnull String userKey) throws ActiveUserNotFoundException {
        return !this.getActiveUserByKey(new UserKey(userKey)).isPresent();
    }

    @Override
    @Nonnull
    public Optional<UserKey> getKeyForUsername(@Nonnull String username) {
        return Optional.ofNullable(this.userKeyService.getKeyForUsername(username)).map(UserKey::new);
    }

    @Override
    @Nonnull
    public Optional<String> getUsernameForKey(@Nonnull UserKey userKey) {
        return Optional.ofNullable(this.userKeyService.getUsernameForKey(userKey.getStringValue()));
    }
}

