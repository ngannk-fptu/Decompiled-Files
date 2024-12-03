/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jwt.core.http.auth;

public interface JwtAuthenticator<REQ, RES, S> {
    public S authenticate(REQ var1, RES var2);
}

