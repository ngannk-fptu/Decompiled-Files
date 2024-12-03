/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.tuckey.web.filters.urlrewrite.extend.RewriteMatch
 */
package com.atlassian.confluence.servlet.rewrite;

import com.atlassian.confluence.plugin.webresource.ConfluenceResourceDownloadUtils;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.tuckey.web.filters.urlrewrite.extend.RewriteMatch;

public class DisableCacheRewriteMatch
extends RewriteMatch {
    private final String rewrittenUrl;
    private final String rewrittenContextUrl;

    public DisableCacheRewriteMatch(String rewrittenUrl, String rewrittenContextUrl) {
        this.rewrittenUrl = rewrittenUrl;
        this.rewrittenContextUrl = rewrittenContextUrl;
    }

    public String getMatchingUrl() {
        return this.rewrittenUrl;
    }

    public boolean execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ConfluenceResourceDownloadUtils.addDisableCachingHeaders(request, response);
        request.getRequestDispatcher(this.rewrittenContextUrl).forward((ServletRequest)request, (ServletResponse)response);
        return true;
    }
}

