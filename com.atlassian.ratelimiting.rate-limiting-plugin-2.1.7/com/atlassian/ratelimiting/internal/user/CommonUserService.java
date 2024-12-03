/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.google.common.base.Strings
 *  javax.annotation.Nonnull
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.ratelimiting.internal.user;

import com.atlassian.ratelimiting.internal.user.AnonymousUserProfile;
import com.atlassian.ratelimiting.user.UserService;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.google.common.base.Strings;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;

public abstract class CommonUserService
implements UserService {
    public static final UserProfile ANONYMOUS_REPRESENTATIVE_USER = new AnonymousUserProfile();
    protected final UserManager userManager;

    public CommonUserService(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public Optional<UserProfile> getUser(UserKey userKey) {
        return AnonymousUserProfile.isAnonymousRepresentativeUser(userKey) ? Optional.of(ANONYMOUS_REPRESENTATIVE_USER) : Optional.ofNullable(this.userManager.getUserProfile(userKey));
    }

    @Override
    public Optional<UserProfile> getUser(String username) {
        return AnonymousUserProfile.isAnonymousRepresentativeUser(username) ? Optional.of(ANONYMOUS_REPRESENTATIVE_USER) : Optional.ofNullable(this.userManager.getUserProfile(username));
    }

    @Override
    public UserProfile getUser(HttpServletRequest request) {
        UserProfile userProfile = this.userManager.getRemoteUser(request);
        return Objects.isNull(userProfile) ? ANONYMOUS_REPRESENTATIVE_USER : userProfile;
    }

    @Override
    @Nonnull
    public UserKey getUserKey(HttpServletRequest request) {
        UserKey userKey = this.userManager.getRemoteUserKey(request);
        return Objects.isNull(userKey) ? ANONYMOUS_REPRESENTATIVE_USER.getUserKey() : userKey;
    }

    protected int determineResultSetSizeForSearch(boolean shouldResultsReturnAnonymousUser, int maxNumberOfResults) {
        return shouldResultsReturnAnonymousUser ? maxNumberOfResults - 1 : maxNumberOfResults;
    }

    protected boolean resultsShouldReturnAnonymousUser(String criteria) {
        return Strings.isNullOrEmpty((String)criteria) || ANONYMOUS_REPRESENTATIVE_USER.getFullName().toLowerCase().contains(criteria.toLowerCase());
    }
}

