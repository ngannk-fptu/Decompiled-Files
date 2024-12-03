/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.tuckey.web.filters.urlrewrite.extend;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.tuckey.web.filters.urlrewrite.extend.RewriteMatch;

public class RewriteRule {
    public boolean initialise(ServletContext servletContext) {
        return true;
    }

    public void destroy() {
    }

    public RewriteMatch matches(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }
}

