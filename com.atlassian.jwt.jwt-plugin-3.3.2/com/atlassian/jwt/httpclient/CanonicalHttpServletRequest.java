/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.jwt.httpclient;

import com.atlassian.jwt.CanonicalHttpRequest;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public class CanonicalHttpServletRequest
implements CanonicalHttpRequest {
    private final HttpServletRequest request;

    public CanonicalHttpServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    @Nonnull
    public String getMethod() {
        return this.request.getMethod();
    }

    @Override
    public String getRelativePath() {
        return StringUtils.removeStart((String)this.request.getRequestURI(), (String)this.request.getContextPath());
    }

    @Override
    @Nonnull
    public Map<String, String[]> getParameterMap() {
        return this.request.getParameterMap();
    }
}

