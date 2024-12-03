/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.tuckey.web.filters.urlrewrite.sample;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.tuckey.web.filters.urlrewrite.extend.RewriteMatch;
import org.tuckey.web.filters.urlrewrite.extend.RewriteRule;
import org.tuckey.web.filters.urlrewrite.sample.SampleRewriteMatch;

public class SampleRewriteRule
extends RewriteRule {
    public RewriteMatch matches(HttpServletRequest request, HttpServletResponse response) {
        if (!request.getRequestURI().startsWith("/staff/")) {
            return null;
        }
        Integer id = null;
        try {
            id = Integer.valueOf(request.getRequestURI().replaceFirst("/staff/([0-9]+)/", "$1"));
        }
        catch (NumberFormatException e) {
            return null;
        }
        return new SampleRewriteMatch(id);
    }
}

