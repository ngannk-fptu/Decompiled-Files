/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.sal.api.user;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.sal.api.user.UserResolutionException;
import java.security.Principal;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

public interface UserManager {
    @Deprecated
    @Nullable
    public String getRemoteUsername();

    @Nullable
    public UserProfile getRemoteUser();

    @Nullable
    public UserKey getRemoteUserKey();

    @Deprecated
    @Nullable
    public String getRemoteUsername(HttpServletRequest var1);

    @Nullable
    public UserProfile getRemoteUser(HttpServletRequest var1);

    @Nullable
    public UserKey getRemoteUserKey(HttpServletRequest var1);

    @Nullable
    public UserProfile getUserProfile(@Nullable String var1);

    @Nullable
    public UserProfile getUserProfile(@Nullable UserKey var1);

    @Deprecated
    public boolean isUserInGroup(@Nullable String var1, @Nullable String var2);

    public boolean isUserInGroup(@Nullable UserKey var1, @Nullable String var2);

    @Deprecated
    public boolean isSystemAdmin(@Nullable String var1);

    public boolean isSystemAdmin(@Nullable UserKey var1);

    @Deprecated
    public boolean isAdmin(@Nullable String var1);

    public boolean isAdmin(@Nullable UserKey var1);

    public boolean isLicensed(@Nullable UserKey var1);

    default public boolean isLimitedUnlicensedUser(@Nullable UserKey userKey) {
        return this.isLimitedUnlicensedAccessEnabled() && this.getUserProfile(userKey) != null && !this.isLicensed(userKey);
    }

    public boolean authenticate(String var1, String var2);

    @Nullable
    public Principal resolve(String var1) throws UserResolutionException;

    public Iterable<String> findGroupNamesByPrefix(String var1, int var2, int var3);

    public boolean isAnonymousAccessEnabled();

    public boolean isLimitedUnlicensedAccessEnabled();
}

