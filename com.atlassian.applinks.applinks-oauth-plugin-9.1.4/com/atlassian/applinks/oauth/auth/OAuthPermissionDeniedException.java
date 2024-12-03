/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.ResponseException
 */
package com.atlassian.applinks.oauth.auth;

import com.atlassian.sal.api.net.ResponseException;

public class OAuthPermissionDeniedException
extends ResponseException {
    public OAuthPermissionDeniedException(String message) {
        super(message);
    }
}

