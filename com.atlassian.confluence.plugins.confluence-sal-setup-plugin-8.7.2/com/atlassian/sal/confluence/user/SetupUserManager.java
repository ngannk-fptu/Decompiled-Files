/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.sal.api.user.UserResolutionException
 *  javax.servlet.http.HttpServletRequest
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.sal.confluence.user;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.sal.api.user.UserResolutionException;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import org.checkerframework.checker.nullness.qual.Nullable;

public class SetupUserManager
implements UserManager {
    public @Nullable String getRemoteUsername() {
        return null;
    }

    public @Nullable UserProfile getRemoteUser() {
        return null;
    }

    public @Nullable UserKey getRemoteUserKey() {
        return null;
    }

    public @Nullable UserProfile getUserProfile(@Nullable String username) {
        return null;
    }

    public @Nullable UserProfile getUserProfile(@Nullable UserKey userKey) {
        return null;
    }

    public boolean isUserInGroup(@Nullable String username, @Nullable String group) {
        return false;
    }

    public boolean isUserInGroup(@Nullable UserKey userKey, @Nullable String group) {
        return false;
    }

    public boolean isSystemAdmin(@Nullable String username) {
        return true;
    }

    public boolean isSystemAdmin(@Nullable UserKey userKey) {
        return true;
    }

    public boolean isAdmin(@Nullable String username) {
        return true;
    }

    public boolean isAdmin(@Nullable UserKey userKey) {
        return true;
    }

    public boolean isLicensed(@Nullable UserKey userKey) {
        return true;
    }

    public boolean isLimitedUnlicensedUser(@Nullable UserKey userKey) {
        return false;
    }

    public boolean authenticate(String username, String password) {
        return false;
    }

    public @Nullable Principal resolve(String username) throws UserResolutionException {
        return null;
    }

    public Iterable<String> findGroupNamesByPrefix(String prefix, int startIndex, int maxResults) {
        return null;
    }

    public boolean isAnonymousAccessEnabled() {
        return false;
    }

    public boolean isLimitedUnlicensedAccessEnabled() {
        return false;
    }

    public @Nullable UserKey getRemoteUserKey(HttpServletRequest request) {
        return null;
    }

    public @Nullable UserProfile getRemoteUser(HttpServletRequest request) {
        return null;
    }

    public @Nullable String getRemoteUsername(HttpServletRequest request) {
        return null;
    }
}

