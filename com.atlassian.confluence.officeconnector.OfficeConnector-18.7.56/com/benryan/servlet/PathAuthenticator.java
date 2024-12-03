/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.user.EntityException
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletRequestWrapper
 */
package com.benryan.servlet;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.EntityException;
import com.benryan.components.TemporaryAuthTokenManager;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class PathAuthenticator
implements Filter {
    private TemporaryAuthTokenManager tokenManager;

    public PathAuthenticator(TemporaryAuthTokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest)req;
        String[] path = this.splitPath(httpReq);
        for (int x = 0; x < path.length; ++x) {
            int y;
            if (!path[x].equalsIgnoreCase("ocauth") || x >= path.length - 1) continue;
            String token = path[x + 1];
            StringBuffer redirect = new StringBuffer("");
            for (y = 0; y < x; ++y) {
                if (path[y].trim().length() <= 0) continue;
                redirect.append('/');
                redirect.append(path[y]);
            }
            for (y = x + 2; y < path.length; ++y) {
                if (path[y].trim().length() <= 0) continue;
                redirect.append('/');
                redirect.append(path[y]);
            }
            ConfluenceUser user = null;
            try {
                user = this.tokenManager.getUser(token);
                if (user != null) {
                    AuthenticatedUserThreadLocal.set((ConfluenceUser)user);
                }
            }
            catch (EntityException entityException) {
                // empty catch block
            }
            chain.doFilter((ServletRequest)new OcAuthRequestWrapper((HttpServletRequest)req, ((HttpServletRequest)req).getContextPath() + redirect), res);
            return;
        }
        chain.doFilter(req, res);
    }

    public void init(FilterConfig arg0) throws ServletException {
    }

    private String[] splitPath(HttpServletRequest req) {
        String ctx;
        String path = req.getRequestURI();
        if (path.startsWith(ctx = req.getContextPath())) {
            path = path.substring(ctx.length());
        }
        String[] names = path.split("/");
        return names;
    }

    private class OcAuthRequestWrapper
    extends HttpServletRequestWrapper {
        private String newUri;

        public OcAuthRequestWrapper(HttpServletRequest request, String newUri) {
            super(request);
            this.newUri = newUri;
        }

        public String getRequestURI() {
            return this.newUri;
        }
    }
}

