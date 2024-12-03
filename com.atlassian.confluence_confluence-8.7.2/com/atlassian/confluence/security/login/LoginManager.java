/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  javax.servlet.http.HttpServletRequest
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.security.login;

import com.atlassian.confluence.security.login.LoginInfo;
import com.atlassian.confluence.security.login.LoginResult;
import com.atlassian.user.User;
import javax.servlet.http.HttpServletRequest;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface LoginManager {
    public LoginResult authenticate(String var1, String var2);

    public @Nullable LoginInfo getLoginInfo(String var1);

    public @Nullable LoginInfo getLoginInfo(User var1);

    public boolean requiresElevatedSecurityCheck(String var1);

    public boolean isElevatedSecurityCheckEnabled();

    public void onFailedLoginAttempt(String var1, HttpServletRequest var2);

    public void onSuccessfulLoginAttempt(String var1, HttpServletRequest var2);

    public void resetFailedLoginCount(User var1);
}

