/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpRequest
 */
package com.atlassian.security.auth.trustedapps.request.commonshttpclient;

import com.atlassian.security.auth.trustedapps.request.TrustedRequest;
import org.apache.http.HttpRequest;

public class CommonsHttpClientTrustedRequest
implements TrustedRequest {
    private final HttpRequest httpMethod;

    public CommonsHttpClientTrustedRequest(HttpRequest httpMethod) {
        if (httpMethod == null) {
            throw new IllegalArgumentException("HttpMethod must not be null!");
        }
        this.httpMethod = httpMethod;
    }

    @Override
    public void addRequestParameter(String name, String value) {
        this.httpMethod.addHeader(name, value);
    }
}

