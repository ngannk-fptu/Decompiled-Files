/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserResolutionException
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.sal.usercompatibility;

import com.atlassian.sal.api.user.UserResolutionException;
import com.atlassian.sal.usercompatibility.UserKey;
import com.atlassian.sal.usercompatibility.UserProfile;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;

public interface UserManager {
    public UserProfile getRemoteUser();

    public UserKey getRemoteUserKey();

    public UserProfile getRemoteUser(HttpServletRequest var1);

    public UserKey getRemoteUserKey(HttpServletRequest var1);

    public UserProfile getUserProfile(UserKey var1);

    public UserProfile getUserProfileByUsername(String var1);

    public boolean isUserInGroup(UserKey var1, String var2);

    public boolean isSystemAdmin(UserKey var1);

    public boolean isAdmin(UserKey var1);

    public boolean authenticate(String var1, String var2);

    public Principal resolve(String var1) throws UserResolutionException;
}

