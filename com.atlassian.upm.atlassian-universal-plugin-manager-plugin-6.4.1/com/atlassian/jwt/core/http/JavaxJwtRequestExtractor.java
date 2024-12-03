/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.jwt.core.http;

import com.atlassian.jwt.core.http.AbstractJwtRequestExtractor;
import com.atlassian.jwt.core.http.JavaxHttpRequestWrapper;
import javax.servlet.http.HttpServletRequest;

public class JavaxJwtRequestExtractor
extends AbstractJwtRequestExtractor<HttpServletRequest> {
    protected JavaxHttpRequestWrapper wrapRequest(HttpServletRequest request) {
        return new JavaxHttpRequestWrapper(request);
    }
}

