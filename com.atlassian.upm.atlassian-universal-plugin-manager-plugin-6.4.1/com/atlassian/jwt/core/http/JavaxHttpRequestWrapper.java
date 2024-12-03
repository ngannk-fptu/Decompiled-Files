/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.jwt.core.http;

import com.atlassian.jwt.CanonicalHttpRequest;
import com.atlassian.jwt.core.http.HttpRequestWrapper;
import com.atlassian.jwt.httpclient.CanonicalHttpServletRequest;
import java.util.Collections;
import java.util.Enumeration;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;

public class JavaxHttpRequestWrapper
implements HttpRequestWrapper {
    private final HttpServletRequest request;

    public JavaxHttpRequestWrapper(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public String getParameter(String parameterName) {
        return this.request.getParameter(parameterName);
    }

    @Override
    @Nonnull
    public Iterable<String> getHeaderValues(String headerName) {
        Enumeration headers = this.request.getHeaders(headerName);
        return headers != null ? Collections.list(headers) : Collections.emptyList();
    }

    @Override
    @Nonnull
    public CanonicalHttpRequest getCanonicalHttpRequest() {
        return new CanonicalHttpServletRequest(this.request);
    }
}

