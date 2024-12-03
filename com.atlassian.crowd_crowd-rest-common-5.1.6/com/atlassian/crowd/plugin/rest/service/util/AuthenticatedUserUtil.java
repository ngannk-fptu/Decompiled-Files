/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.crowd.plugin.rest.service.util;

import com.atlassian.crowd.plugin.rest.service.util.RestAuthSessionHelperUtil;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

public class AuthenticatedUserUtil
extends RestAuthSessionHelperUtil {
    @VisibleForTesting
    public static final String USER_ATTRIBUTE_KEY = "com.atlassian.crowd.authenticated.user.name";

    private AuthenticatedUserUtil() {
    }

    @Nullable
    public static String getAuthenticatedUser(HttpServletRequest request) {
        return AuthenticatedUserUtil.fetchStringFromRequestSession(request, USER_ATTRIBUTE_KEY);
    }

    public static void setAuthenticatedUser(HttpServletRequest request, String username) {
        Preconditions.checkNotNull((Object)username);
        request.getSession().setAttribute(USER_ATTRIBUTE_KEY, (Object)username);
    }
}

