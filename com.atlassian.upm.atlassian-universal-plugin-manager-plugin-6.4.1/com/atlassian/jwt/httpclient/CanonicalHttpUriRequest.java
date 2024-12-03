/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.jwt.httpclient;

import com.atlassian.jwt.CanonicalHttpRequest;
import java.util.Collections;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

public class CanonicalHttpUriRequest
implements CanonicalHttpRequest {
    private final String method;
    private final String relativePath;
    private final Map<String, String[]> parameterMap;

    public CanonicalHttpUriRequest(String method, String path, String contextPath) {
        this(method, path, contextPath, Collections.emptyMap());
    }

    public CanonicalHttpUriRequest(String method, String path, String contextPath, Map<String, String[]> parameterMap) {
        this.method = CanonicalHttpUriRequest.checkMethod(method);
        String contextPathToRemove = null == contextPath || "/".equals(contextPath) ? "" : contextPath;
        this.relativePath = (String)StringUtils.defaultIfBlank((CharSequence)StringUtils.removeEnd((String)StringUtils.removeStart((String)path, (String)contextPathToRemove), (String)"/"), (CharSequence)"/");
        this.parameterMap = parameterMap;
    }

    @Override
    @Nonnull
    public String getMethod() {
        return this.method;
    }

    @Override
    public String getRelativePath() {
        return this.relativePath;
    }

    @Override
    @Nonnull
    public Map<String, String[]> getParameterMap() {
        return this.parameterMap;
    }

    private static String checkMethod(String method) {
        if (null == method) {
            throw new IllegalArgumentException("Method cannot be null!");
        }
        if ("".equals(method)) {
            throw new IllegalArgumentException("Method cannot be empty-string!");
        }
        return method.toUpperCase();
    }
}

