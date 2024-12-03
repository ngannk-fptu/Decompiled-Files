/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.tuckey.web.filters.urlrewrite;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.tuckey.web.filters.urlrewrite.RewrittenUrl;
import org.tuckey.web.filters.urlrewrite.extend.RewriteMatch;

class RewrittenUrlClass
implements RewrittenUrl {
    RewriteMatch rewriteMatch;
    private String matchingUrl;

    protected RewrittenUrlClass(RewriteMatch rewriteMatch) {
        this.matchingUrl = rewriteMatch.getMatchingUrl();
        this.rewriteMatch = rewriteMatch;
    }

    public boolean doRewrite(HttpServletRequest hsRequest, HttpServletResponse hsResponse, FilterChain chain) throws IOException, ServletException {
        return this.rewriteMatch.execute(hsRequest, hsResponse);
    }

    public String getTarget() {
        return this.matchingUrl;
    }
}

