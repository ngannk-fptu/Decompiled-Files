/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.servlet.ResourceDownloadUtils
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.tuckey.web.filters.urlrewrite.extend.RewriteMatch
 *  org.tuckey.web.filters.urlrewrite.extend.RewriteRule
 */
package com.atlassian.plugin.webresource.filter.rewrite;

import com.atlassian.plugin.servlet.ResourceDownloadUtils;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.tuckey.web.filters.urlrewrite.extend.RewriteMatch;
import org.tuckey.web.filters.urlrewrite.extend.RewriteRule;

public class ResourceDownloadRewriteRule
extends RewriteRule {
    private static final Pattern NON_WEB_INF_RESOURCES_URI_PATTERN = Pattern.compile("^/s/(.*)/_/((?i)(?!WEB-INF).*)");

    public RewriteMatch matches(HttpServletRequest request, HttpServletResponse response) {
        String normalisedRequestUriPath;
        try {
            normalisedRequestUriPath = this.getNormalisedPathFrom(request);
        }
        catch (URISyntaxException invalidUriInRequest) {
            return null;
        }
        final Matcher nonWebInfResourcesPatternMatcher = NON_WEB_INF_RESOURCES_URI_PATTERN.matcher(normalisedRequestUriPath);
        if (!nonWebInfResourcesPatternMatcher.matches()) {
            return null;
        }
        final String rewrittenUriPath = "/" + nonWebInfResourcesPatternMatcher.group(2);
        final String rewrittenUrl = request.getContextPath() + rewrittenUriPath;
        return new RewriteMatch(){

            public String getMatchingUrl() {
                return rewrittenUrl;
            }

            public boolean execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
                ResourceDownloadUtils.addPublicCachingHeaders((HttpServletRequest)request, (HttpServletResponse)response);
                request.setAttribute("_statichash", (Object)nonWebInfResourcesPatternMatcher.group(1));
                request.getRequestDispatcher(rewrittenUriPath).forward((ServletRequest)request, (ServletResponse)response);
                return true;
            }
        };
    }

    private String getNormalisedPathFrom(HttpServletRequest request) throws URISyntaxException {
        return new URI(this.stripContextFrom(request)).normalize().toString();
    }

    private String stripContextFrom(HttpServletRequest request) {
        return request.getRequestURI().substring(request.getContextPath().length());
    }
}

