/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 */
package com.atlassian.crowd.plugin.rest.service.util;

import com.google.common.base.Preconditions;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class RestAuthSessionHelperUtil {
    @Nullable
    protected static String fetchStringFromRequestSession(HttpServletRequest request, String key) {
        Preconditions.checkNotNull((Object)request);
        Preconditions.checkNotNull((Object)key);
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (String)session.getAttribute(key);
    }
}

