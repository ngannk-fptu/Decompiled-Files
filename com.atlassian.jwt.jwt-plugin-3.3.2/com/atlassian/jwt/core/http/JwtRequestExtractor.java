/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jwt.core.http;

import com.atlassian.jwt.CanonicalHttpRequest;

public interface JwtRequestExtractor<REQ> {
    public String extractJwt(REQ var1);

    public CanonicalHttpRequest getCanonicalHttpRequest(REQ var1);
}

