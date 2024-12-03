/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserProfile
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.ratelimiting.user;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserProfile;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

public interface UserService {
    public Optional<UserProfile> getUser(UserKey var1);

    public Optional<UserProfile> getUser(String var1);

    public UserProfile getUser(HttpServletRequest var1);

    public UserKey getUserKey(HttpServletRequest var1);

    public List<UserProfile> searchUsersForUserPicker(String var1, int var2, int var3);
}

