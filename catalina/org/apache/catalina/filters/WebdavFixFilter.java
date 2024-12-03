/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.GenericFilter
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.filters;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.GenericFilter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.res.StringManager;

public class WebdavFixFilter
extends GenericFilter {
    private static final long serialVersionUID = 1L;
    protected static final StringManager sm = StringManager.getManager(WebdavFixFilter.class);
    private static final String UA_MINIDIR_START = "Microsoft-WebDAV-MiniRedir";
    private static final String UA_MINIDIR_5_1_2600 = "Microsoft-WebDAV-MiniRedir/5.1.2600";
    private static final String UA_MINIDIR_5_2_3790 = "Microsoft-WebDAV-MiniRedir/5.2.3790";

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;
        String ua = httpRequest.getHeader("User-Agent");
        if (ua == null || ua.length() == 0 || !ua.startsWith(UA_MINIDIR_START)) {
            chain.doFilter(request, response);
        } else if (ua.startsWith(UA_MINIDIR_5_1_2600)) {
            httpResponse.sendRedirect(this.buildRedirect(httpRequest));
        } else if (ua.startsWith(UA_MINIDIR_5_2_3790)) {
            if (!httpRequest.getContextPath().isEmpty()) {
                this.getServletContext().log(sm.getString("webDavFilter.xpRootContext"));
            }
            this.getServletContext().log(sm.getString("webDavFilter.xpProblem"));
            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect(this.buildRedirect(httpRequest));
        }
    }

    private String buildRedirect(HttpServletRequest request) {
        StringBuilder location = new StringBuilder(request.getRequestURL().length());
        location.append(request.getScheme());
        location.append("://");
        location.append(request.getServerName());
        location.append(':');
        location.append(request.getServerPort());
        location.append(request.getRequestURI());
        return location.toString();
    }
}

