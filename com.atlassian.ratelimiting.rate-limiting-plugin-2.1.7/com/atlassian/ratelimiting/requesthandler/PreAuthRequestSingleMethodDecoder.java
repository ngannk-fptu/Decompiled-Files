/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.ratelimiting.requesthandler;

import com.atlassian.sal.api.user.UserKey;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

public interface PreAuthRequestSingleMethodDecoder {
    public Optional<UserKey> getUserKey(HttpServletRequest var1);
}

