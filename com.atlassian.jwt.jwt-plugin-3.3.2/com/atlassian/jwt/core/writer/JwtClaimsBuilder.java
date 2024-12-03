/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jwt.core.writer;

import com.atlassian.jwt.CanonicalHttpRequest;
import com.atlassian.jwt.core.HttpRequestCanonicalizer;
import com.atlassian.jwt.writer.JwtJsonBuilder;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class JwtClaimsBuilder {
    public static void appendHttpRequestClaims(JwtJsonBuilder jsonBuilder, CanonicalHttpRequest request) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        jsonBuilder.queryHash(HttpRequestCanonicalizer.computeCanonicalRequestHash(request));
    }
}

