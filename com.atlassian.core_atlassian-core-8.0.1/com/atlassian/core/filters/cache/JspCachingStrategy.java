/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.core.filters.cache;

import com.atlassian.core.filters.cache.CachingStrategy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

public final class JspCachingStrategy
implements CachingStrategy {
    @Override
    public final boolean matches(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return StringUtils.indexOf((CharSequence)uri, (CharSequence)".jsp") > 0;
    }

    @Override
    public final void setCachingHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0L);
    }
}

