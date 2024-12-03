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
 */
package com.atlassian.confluence.servlet.rewrite;

import com.atlassian.plugin.servlet.ResourceDownloadUtils;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.tuckey.web.filters.urlrewrite.extend.RewriteMatch;

public class CachedRewriteMatch
extends RewriteMatch {
    private final String staticHash;
    private final String rewrittenUrl;
    private final String rewrittenContextUrl;

    public CachedRewriteMatch(String rewrittenUrl, String rewrittenContextUrl, String staticHash) {
        this.rewrittenUrl = rewrittenUrl;
        this.rewrittenContextUrl = rewrittenContextUrl;
        this.staticHash = staticHash;
    }

    public String getMatchingUrl() {
        return this.rewrittenUrl;
    }

    public boolean execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ResourceDownloadUtils.addPublicCachingHeaders((HttpServletRequest)request, (HttpServletResponse)response);
        request.setAttribute("_statichash", (Object)this.staticHash);
        request.getRequestDispatcher(this.rewrittenContextUrl).forward((ServletRequest)request, (ServletResponse)response);
        return true;
    }
}

