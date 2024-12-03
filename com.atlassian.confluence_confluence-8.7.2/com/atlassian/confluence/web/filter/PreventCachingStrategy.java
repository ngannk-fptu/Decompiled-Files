/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.cache.CachingStrategy
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.web.filter;

import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.web.filter.CachingHeaders;
import com.atlassian.core.filters.cache.CachingStrategy;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class PreventCachingStrategy
implements CachingStrategy {
    PreventCachingStrategy() {
    }

    public boolean matches(HttpServletRequest request) {
        String uri;
        try {
            uri = this.getNormalisedPathFrom(request);
        }
        catch (URISyntaxException invalidUriInRequest) {
            return true;
        }
        if (uri.contains("/rest/")) {
            return false;
        }
        return uri.equals("/") || uri.contains(".vm") || uri.contains(".action") || uri.contains("/display/");
    }

    private String getNormalisedPathFrom(HttpServletRequest request) throws URISyntaxException {
        String decodedUri = HtmlUtil.urlDecode(this.stripContextFrom(request));
        return new URI(decodedUri).normalize().toString();
    }

    private String stripContextFrom(HttpServletRequest request) {
        return request.getRequestURI().substring(request.getContextPath().length());
    }

    public void setCachingHeaders(HttpServletResponse response) {
        CachingHeaders.PREVENT_CACHING.apply(response);
    }
}

