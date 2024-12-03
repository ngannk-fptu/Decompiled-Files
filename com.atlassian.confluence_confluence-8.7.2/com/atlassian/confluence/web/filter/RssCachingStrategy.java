/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.cache.CachingStrategy
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.web.filter;

import com.atlassian.confluence.web.filter.CachingHeaders;
import com.atlassian.core.filters.cache.CachingStrategy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

class RssCachingStrategy
implements CachingStrategy {
    private static final String URL_PATTERN = "createrssfeed.action";

    RssCachingStrategy() {
    }

    public boolean matches(HttpServletRequest request) {
        return StringUtils.contains((CharSequence)request.getRequestURI(), (CharSequence)URL_PATTERN);
    }

    public void setCachingHeaders(HttpServletResponse response) {
        CachingHeaders.PRIVATE_SHORT_TERM.apply(response);
    }
}

