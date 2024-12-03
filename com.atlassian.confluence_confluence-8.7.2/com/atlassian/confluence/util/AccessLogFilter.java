/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.seraph.config.SecurityConfig
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util;

import com.atlassian.seraph.config.SecurityConfig;
import java.io.IOException;
import java.security.Principal;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccessLogFilter
implements Filter {
    private static final Logger log = LoggerFactory.getLogger(AccessLogFilter.class);
    private FilterConfig config;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.config = filterConfig;
        log.info("AccessLogFilter initialized. Format is: <user> <url> <starting memory free (kb)> +- <difference in free mem (kb)> <query time (ms)> <remote address>");
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        if (log.isInfoEnabled()) {
            HttpServletRequest request = (HttpServletRequest)servletRequest;
            HttpServletResponse response = (HttpServletResponse)servletResponse;
            String url = request.getRequestURL().toString();
            if (this.interestingURL(url)) {
                Principal user = ((SecurityConfig)this.config.getServletContext().getAttribute("seraph_config")).getAuthenticator().getUser(request, response);
                long startMem = Runtime.getRuntime().freeMemory() >> 10;
                long t1 = System.currentTimeMillis();
                chain.doFilter((ServletRequest)request, (ServletResponse)response);
                long filterDuration = System.currentTimeMillis() - t1;
                long endMem = Runtime.getRuntime().freeMemory() >> 10;
                long memDiff = endMem - startMem;
                String username = user == null ? "-" : user.getName();
                Object memDiffStr = "";
                if (memDiff > 0L) {
                    memDiffStr = "+" + memDiff;
                } else if (memDiff < 0L) {
                    memDiffStr = Long.toString(memDiff);
                }
                log.info("{} {} {} {}{} {} {}", new Object[]{username, request.getMethod(), url, startMem, memDiffStr, filterDuration, servletRequest.getRemoteAddr()});
                return;
            }
        }
        chain.doFilter(servletRequest, servletResponse);
    }

    private boolean interestingURL(String url) {
        return url != null && !url.endsWith(".gif") && !url.endsWith(".png") && !url.endsWith(".jpg") && !url.endsWith(".css") && !url.endsWith(".ico") && !url.endsWith(".js");
    }

    public void destroy() {
        this.config = null;
    }
}

